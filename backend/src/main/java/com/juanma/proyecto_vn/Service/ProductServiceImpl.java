package com.juanma.proyecto_vn.Service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductServiceImpl implements IProductService {

        @Autowired
        private ProductRepository productRepository;

        @Autowired
        private ProviderRepository providerRepository;

        @Autowired
        private CategoryRepository categoryRepository;

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
        public GetProductDto getProductById(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
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
        public GetProductDto deleteProduct(UUID id) {
                Product product = productRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Product not found"));
                product.setDeleted(true);

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
