package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.UUID;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;

import com.juanma.proyecto_vn.Dtos.Product.CreateProductDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;
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
// @CacheConfig(cacheNames = "products")
public class ProductServiceImpl implements IProductService {

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
                        remoteCache = rcm.administration().getOrCreateCache("productCache", "default");
                } catch (Exception e) {
                        System.out.println("Error inicializando cache: " + e.getMessage());
                }
        }

        @Override
        public List<GetProductDto> getAllProducts(int page, int size, String sortBy, String orderBy, String filterBy,
                        String filterValue) {
                Sort.Direction direction = orderBy.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
                Page<Product> pageProducts;

                if (filterBy != null && filterValue != null && filterBy.equalsIgnoreCase("name")) {
                        pageProducts = productRepository.findByNameContainingIgnoreCase(filterValue, pageable);
                } else {
                        pageProducts = productRepository.findAll(pageable);
                }

                // Get content se usa para obtener la lista de productos de la página
                // y no la página completa
                return pageProducts.getContent().stream()
                                .map(this::mapToGetProductDto)
                                .toList();
        }

        @Override
        // @Cacheable(value = "products", key = "#id")
        public GetProductDto getProductById(UUID id) {
                String key = id.toString();
                Product cachedProduct = remoteCache.get(key);
                if (cachedProduct != null) {
                        return mapToGetProductDto(cachedProduct);
                }

                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                remoteCache.put(key, product);
                return mapToGetProductDto(product);
        }

        @Override
        public GetProductDto createProduct(CreateProductDto product) {
                Provider provider = providerRepository.findById(product.getProvider())
                                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

                Category category = categoryRepository.findById(product.getCategory())
                                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

                Product newProduct = Product.builder()
                                .name(product.getName())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .description(product.getDescription())
                                .provider(provider)
                                .category(category)
                                .build();

                productRepository.save(newProduct);
                return mapToGetProductDto(newProduct);
        }

        @Override
        @CachePut(value = "products", key = "#id") // Actualiza la cache después de modificar el producto
        public GetProductDto updateProduct(CreateProductDto product, UUID id) {
                Product existingProduct = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

                Provider provider = providerRepository.findById(product.getProvider())
                                .orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

                Category category = categoryRepository.findById(product.getCategory())
                                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

                existingProduct.setName(product.getName());
                existingProduct.setPrice(product.getPrice());
                existingProduct.setImage(product.getImage());
                existingProduct.setDescription(product.getDescription());
                existingProduct.setProvider(provider);
                existingProduct.setCategory(category);

                productRepository.save(existingProduct);
                return mapToGetProductDto(existingProduct);
        }

        @Override
        @CacheEvict(value = "products", key = "#id") // Elimina el producto de la cache
        public GetProductDto deleteProduct(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                productRepository.delete(product);

                return mapToGetProductDto(product);
        }

        private GetProductDto mapToGetProductDto(Product product) {
                System.out.println("Product: " + product.toString());
                return GetProductDto.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .description(product.getDescription())
                                .provider(product.getProvider().getName())
                                .category(product.getCategory().getName())
                                .build();
        }

}
