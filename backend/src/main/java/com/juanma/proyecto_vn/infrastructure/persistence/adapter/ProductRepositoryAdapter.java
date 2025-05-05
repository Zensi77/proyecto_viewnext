package com.juanma.proyecto_vn.infrastructure.persistence.adapter;

import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.repository.ProductRepository;
import com.juanma.proyecto_vn.infrastructure.persistence.entity.ProductEntity;
import com.juanma.proyecto_vn.infrastructure.persistence.repository.JpaProductRepository;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;
import com.juanma.proyecto_vn.infrastructure.persistence.mapper.ProductMapper;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;

import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Join;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Adaptador que implementa el puerto de repositorio de productos conectando con
 * JPA
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ProductRepositoryAdapter implements ProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @Override
    public Page<Product> getAllProducts(int page, int size, String sortBy, String orderBy,
            Map<String, Object> filters) {
        Sort sort = Sort.by(Sort.Direction.fromString(orderBy), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Specification<ProductEntity> spec = getProductSpecification(filters);
        Page<ProductEntity> pageResult = jpaProductRepository.findAll(spec, pageable);

        Page<Product> mappedPage = pageResult.map(productMapper::toDomain);

        return mappedPage;
    }

    @Override
    public Product findById(UUID id) {
        ProductEntity productEntity = jpaProductRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Product not found with id: " + id));

        return productMapper.toDomain(productEntity);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        ProductEntity savedEntity = jpaProductRepository.save(entity);
        return productMapper.toDomain(savedEntity);
    }

    @Override
    public void delete(Product product) {
        ProductEntity entity = productMapper.toEntity(product);
        jpaProductRepository.delete(entity);
    }

    @Override
    public void deleteById(UUID id) {
        jpaProductRepository.deleteById(id);
    }

    @Override
    public List<Map<String, Object>> findAllNames() {
        return jpaProductRepository.findAllNames();
    }

    public Specification<ProductEntity> getProductSpecification(Map<String, Object> filters) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filters.containsKey("name")) {
                String name = filters.get("name").toString().toLowerCase();
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name + "%"));
            }

            if (filters.containsKey("category")) {
                Join<Object, Object> categoryJoin = root.join("category");
                @SuppressWarnings("unchecked")
                List<String> categoryIds = (List<String>) filters.get("category");
                predicates.add(categoryJoin.get("id").in(categoryIds));
            }

            if (filters.containsKey("provider")) {
                Join<Object, Object> providerJoin = root.join("provider");
                @SuppressWarnings("unchecked")
                List<String> providerIds = (List<String>) filters.get("provider");
                predicates.add(providerJoin.get("id").in(providerIds));
            }

            if (filters.containsKey("priceMin")) {
                Object priceMinObj = filters.get("priceMin");
                if (priceMinObj != null) {
                    double minPrice;
                    if (priceMinObj instanceof Double) {
                        minPrice = (Double) priceMinObj;
                    } else {
                        minPrice = Double.parseDouble(priceMinObj.toString());
                    }
                    log.debug("Aplicando filtro priceMin: {}", minPrice);
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), minPrice));
                }
            }

            if (filters.containsKey("priceMax")) {
                Object priceMaxObj = filters.get("priceMax");
                if (priceMaxObj != null) {
                    double maxPrice;
                    if (priceMaxObj instanceof Double) {
                        maxPrice = (Double) priceMaxObj;
                    } else {
                        maxPrice = Double.parseDouble(priceMaxObj.toString());
                    }
                    log.debug("Aplicando filtro priceMax: {}", maxPrice);
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), maxPrice));
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    @Override
    public List<Product> getRandom(int limit) {
        List<ProductEntity> products = jpaProductRepository.findRandom(limit);

        return products.stream()
                .map(productMapper::toDomain)
                .toList();
    }

}