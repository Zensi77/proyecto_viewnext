package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.juanma.proyecto_vn.Dtos.Category.CategoryDto;
import com.juanma.proyecto_vn.Dtos.Product.CreateProductDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;
import com.juanma.proyecto_vn.Dtos.Provider.ProviderDto;
import com.juanma.proyecto_vn.Exception.ResourceNotFoundException;
import com.juanma.proyecto_vn.Repositorys.CategoryRepository;
import com.juanma.proyecto_vn.Repositorys.ProductRepository;
import com.juanma.proyecto_vn.Repositorys.ProviderRepository;
import com.juanma.proyecto_vn.interfaces.IProductService;
import com.juanma.proyecto_vn.models.Category;
import com.juanma.proyecto_vn.models.Product;
import com.juanma.proyecto_vn.models.Provider;

import jakarta.annotation.PostConstruct;
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
        private RemoteCacheManager rcm; // Manejador de cache remota de Infinispan

        private RemoteCache<String, Product> remoteCache; // Cache de Infinispan

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
        public Map<String, String> getAllProducts(int page, int size, String sortBy, String orderBy, String filterBy,
                        String filterValue) {
                Sort.Direction direction = orderBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
                Page<Product> pageProducts;

                if (filterBy != null && filterValue != null && filterBy.equalsIgnoreCase("name")) {
                        pageProducts = productRepository.findByNameContainingIgnoreCase(filterValue, pageable);
                } else {
                        pageProducts = productRepository.findAll(pageable);
                }

                Map<String, String> response = Map.of("totalElements", String.valueOf(pageProducts.getTotalElements()),
                                "totalPages", String.valueOf(pageProducts.getTotalPages()),
                                "currentPage", String.valueOf(pageProducts.getNumber()),
                                "pageSize", String.valueOf(pageProducts.getSize()),
                                "numberOfElements", String.valueOf(pageProducts.getNumberOfElements()),
                                "sortBy", sortBy,
                                "orderBy", orderBy,
                                "filterBy", filterBy,
                                "filterValue", filterValue);

                List<GetProductDto> products = pageProducts.getContent().stream()
                                .map(this::mapProductDto)
                                .toList();

                response.put("products", products.toString());

                return response;
        }

        @Override
        @Transactional
        public List<GetProductDto> getRandomProducts(int quantity) {
                List<Product> products = productRepository.findRandomProducts(quantity);
                return products.stream()
                                .map(this::mapProductDto)
                                .toList();
        }

        @Override
        @Transactional
        // @Cacheable(value = "products", key = "#id", unless = "#result == null") //
        public GetProductDto getProductById(UUID id) {
                String key = id.toString();

                Product cachedProduct = null;
                if (remoteCache != null) {
                        // Intenta obtener el producto de la cache
                        cachedProduct = remoteCache.get(key);
                        if (cachedProduct != null) {
                                return mapProductDto(cachedProduct);
                        }

                }

                Product productDb = null;
                if (cachedProduct == null) {
                        // Si no está en la cache, lo buscamos en la base de datos
                        productDb = productRepository.findById(id)
                                        .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
                }

                if (remoteCache != null && cachedProduct == null) {
                        remoteCache.put(key, productDb);
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
                        remoteCache.put(id.toString(), existingProduct);
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
                                                .build())
                                .category(CategoryDto.builder()
                                                .id(product.getCategory().getId())
                                                .name(product.getCategory().getName())
                                                .build())
                                .build();
        }

}
