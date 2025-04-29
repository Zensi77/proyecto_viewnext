package com.juanma.proyecto_vn.Application.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.juanma.proyecto_vn.domain.service.IProductService;
import com.juanma.proyecto_vn.infrastructure.cache.modelsProto.ProductProto;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Category;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Product;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.Provider;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.CategoryRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.ProductCartRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.ProductRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.ProviderRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.CreateProductDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;

@Service
@Transactional
@CacheConfig(cacheNames = "productCache") // Configuración de la cache
public class ProductServiceImpl implements IProductService {
        private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProviderRepository providerRepository;

        @Autowired
        private CategoryRepository categoryRepository;

        @Autowired
        private ProductCartRepository productCartRepository;

        @Autowired
        private MetricsService producerService; // Servicio para enviar métricas a la cola de mensajes

        @Autowired
        private RemoteCacheManager rcm; // Manejador de cache remota de Infinispan

        private RemoteCache<String, ProductProto> remoteCache; // Cache de Infinispan

        @PostConstruct
        public void init() {
                try {
                        // Inicializa el RemoteCacheManager y el RemoteCache
                        // Crear o obtener la cache distribuida
                        rcm.administration().getOrCreateCache("productCache", DefaultTemplate.DIST_SYNC);

                        remoteCache = rcm.getCache("productCache");
                        logger.info("Cache de Infinispan inicializada correctamente");
                } catch (Exception e) {
                        logger.warn("Error al inicializar el cache de Infinispan: " + e.getMessage());
                }
        }

        @Override
        @Transactional
        public Map<String, Object> getAllProducts(int page, int size, String sortBy, String orderBy, String filterBy,
                        String filterValue) {
                Sort.Direction direction = orderBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
                Page<Product> pageProducts;

                if (filterBy != null && filterValue != null && filterBy.equalsIgnoreCase("name")) {
                        pageProducts = productRepository.findByNameContainingIgnoreCase(filterValue, pageable);
                } else {
                        pageProducts = productRepository.findAll(pageable);
                }

                Map<String, Object> response = new HashMap<>();
                response.put("totalPages", String.valueOf(pageProducts.getTotalPages()));
                response.put("totalElements", String.valueOf(pageProducts.getTotalElements()));
                response.put("currentPage", String.valueOf(pageProducts.getNumber()));
                response.put("pageSize", String.valueOf(pageProducts.getSize()));
                response.put("sortBy", sortBy);
                response.put("orderBy", orderBy);
                response.put("filterBy", filterBy);
                response.put("filterValue", filterValue);
                response.put("hasNext", String.valueOf(pageProducts.hasNext()));
                response.put("hasPrevious", String.valueOf(pageProducts.hasPrevious()));

                List<GetProductDto> products = pageProducts.getContent().stream()
                                .map(this::mapProductDto)
                                .toList();

                response.put("products", products);

                return response;
        }

        @Override
        @Transactional
        public List<GetProductDto> getRandomProducts(int quantity) {
                List<Product> allProducts = productRepository.findAll();
                Collections.shuffle(allProducts);
                return allProducts.stream()
                                .map(this::mapProductDto)
                                .limit(quantity)
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional
        public GetProductDto getProductById(UUID id, HttpServletRequest http) {
                Object attr = http.getAttribute("userId");
                String userId = null;
                if (attr != null) {
                        userId = attr.toString();
                }
                producerService.sendFunnelEvent("product_viewed", userId, Map.of(
                                "product_id", id.toString()));

                String key = id.toString();
                ProductProto productProto = null;

                if (remoteCache != null) {
                        try {
                                productProto = remoteCache.get(key);
                                if (productProto != null) {
                                        logger.info("Producto obtenido de la caché: {}", key);
                                        return ProductProto.toDto(productProto);
                                }
                        } catch (Exception e) {
                                // Si hay un error al acceder a la caché, lo registramos pero seguimos
                                logger.warn("Error al acceder a la caché: {}. Obteniendo datos de la base de datos.",
                                                e.getMessage());
                        }
                }

                Product productDb = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                if (remoteCache != null) {
                        try {
                                productProto = ProductProto.fromDto(mapProductDto(productDb));
                                remoteCache.put(key, productProto);
                                logger.debug("Producto guardado en caché: {}", key);
                        } catch (Exception e) {
                                // Si hay un error al guardar en la caché, lo registramos pero continuamos
                                logger.warn("Error al guardar en la caché: {}", e.getMessage());
                        }
                }

                return mapProductDto(productDb);
        }

        @Override
        @Transactional
        public GetProductDto createProduct(CreateProductDto product) {
                Provider provider = providerRepository.findById(UUID.fromString(product.getProvider()))
                                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

                Category category = categoryRepository.findById(UUID.fromString(product.getCategory()))
                                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

                Product newProduct = Product.builder()
                                .name(product.getName())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .stock(product.getStock())
                                .description(product.getDescription())
                                .provider(provider)
                                .category(category)
                                .build();

                productRepository.save(newProduct);
                return mapProductDto(newProduct);
        }

        @Override
        @Transactional
        // @CachePut(value = "productCache", key = "#id")
        public GetProductDto updateProduct(CreateProductDto product, UUID id) {
                Product existingProduct = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                Provider provider = providerRepository.findById(UUID.fromString(product.getProvider()))
                                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

                Category category = categoryRepository.findById(UUID.fromString(product.getCategory()))
                                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setImage(product.getImage());
                existingProduct.setStock(product.getStock());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setProvider(provider);
                existingProduct.setCategory(category);

                productRepository.save(existingProduct);

                if (remoteCache != null) {
                        // Actualiza el producto en la cache
                        remoteCache.put(id.toString(), ProductProto.fromDto(mapProductDto(existingProduct)));
                }
                return mapProductDto(existingProduct);
        }

        @Override
        @Transactional
        // @CacheEvict(value = "productCache", key = "#id") // Elimina el producto de la
        // cache
        public GetProductDto deleteProduct(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                productRepository.delete(product);

                productCartRepository.deleteByProduct(product.getId()); // Elimina el producto de la tabla product_cart

                if (remoteCache != null) {
                        // Elimina el producto de la cache
                        remoteCache.remove(id.toString());
                }

                return mapProductDto(product);
        }

        private GetProductDto mapProductDto(Product product) {
                return GetProductDto.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .stock(product.getStock())
                                .description(product.getDescription())
                                .provider(ProviderDto.builder()
                                                .id(product.getProvider().getId())
                                                .name(product.getProvider().getName())
                                                .address(product.getProvider().getAddress())
                                                .build())
                                .category(CategoryDto.builder()
                                                .id(product.getCategory().getId())
                                                .name(product.getCategory().getName())
                                                .build())
                                .build();
        }

}
