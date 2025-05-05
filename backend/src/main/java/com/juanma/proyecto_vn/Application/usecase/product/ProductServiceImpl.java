package com.juanma.proyecto_vn.Application.usecase.product;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.domain.repository.CategoryRepository;
import com.juanma.proyecto_vn.domain.repository.ProductRepository;
import com.juanma.proyecto_vn.domain.repository.ProviderRepository;
import com.juanma.proyecto_vn.domain.service.IProductService;
import com.juanma.proyecto_vn.domain.service.IMetricsService;
import com.juanma.proyecto_vn.domain.service.IProductCacheService;

/**
 * Implementación de los casos de uso relacionados con productos
 */
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProviderRepository providerRepository;
    private final IProductCacheService cacheService;
    private final IMetricsService metricsService;

    @Override
    public Map<String, Object> getAllProducts(int page, int size, String sortBy, String orderBy,
            Map<String, Object> filters) {

        log.debug("Buscando productos con filtros: {}", filters);
        Page<Product> products;

        // Si no hay filtros, simplemente obtenemos todos los productos
        if (filters == null || filters.isEmpty()) {
            products = productRepository.getAllProducts(page, size, sortBy, orderBy, null);
        } else {
            // Preparamos parámetros para la consulta critería
            Map<String, Object> criteriaParams = new HashMap<>();

            // Procesamos filtro de nombre si existe
            if (filters.containsKey("name")) {
                criteriaParams.put("name", filters.get("name"));
            }

            // Procesamos filtro de categoría si existe (ahora como lista de IDs)
            if (filters.containsKey("category")) {
                criteriaParams.put("category", filters.get("category"));
            }

            // Procesamos filtro de proveedor si existe (ahora como lista de IDs)
            if (filters.containsKey("provider")) {
                criteriaParams.put("provider", filters.get("provider"));
            }

            // Procesamos filtro de precio si existen min y max
            if (filters.containsKey("priceMin") && filters.containsKey("priceMax")) {
                criteriaParams.put("priceMin", filters.get("priceMin"));
                criteriaParams.put("priceMax", filters.get("priceMax"));
            }

            // Llamamos al repositorio con el mapa de criterios
            products = productRepository.getAllProducts(page, size, sortBy, orderBy,
                    criteriaParams);
        }

        // Construimos la respuesta
        Map<String, Object> response = new HashMap<>();
        response.put("totalPages", products.getTotalPages());
        response.put("totalElements", products.getTotalElements());
        response.put("currentPage", page);
        response.put("pageSize", size);
        response.put("sortBy", sortBy);
        response.put("orderBy", orderBy);
        response.put("filters", filters != null ? filters : new HashMap<>());
        response.put("hasNext", products.hasNext());
        response.put("hasPrevious", products.hasPrevious());
        response.put("products", products);

        return response;
    }

    @Override
    public List<Map<String, String>> getAllNames() {
        return productRepository.findAllNames().stream()
                .map(name -> {
                    Map<String, String> map = new HashMap<>();
                    map.put("id", name.get("id").toString());
                    map.put("name", name.get("name").toString());
                    return map;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> getRandomProducts(int quantity) {
        return productRepository.getRandom(quantity);
    }

    @Override
    public Product getProductById(UUID id, String userId) {
        // Enviar evento de visualización de producto
        metricsService.sendFunnelEvent("product_viewed", userId, Map.of("product_id", id.toString()));

        // Intentar obtener el producto desde la caché
        String key = id.toString();
        Optional<Product> cachedProduct = cacheService.getFromCache(key);

        if (cachedProduct.isPresent()) {
            log.info("Producto obtenido de la caché: {}", key);
            return cachedProduct.get();
        }

        // Si no está en caché, obtenerlo de la base de datos
        Product product = productRepository.findById(id);

        // Guardar en caché para futuras consultas
        try {
            cacheService.saveInCache(key, product);
            log.debug("Producto guardado en caché: {}", key);
        } catch (Exception e) {
            log.warn("Error al guardar en la caché: {}", e.getMessage());
        }

        return product;
    }

    @Override
    public Product createProduct(String name, double price, String image, int stock, String description,
            UUID categoryId, UUID providerId) {

        // Obtener la categoría y validar existencia
        Category category;
        try {
            category = categoryRepository.findById(categoryId);
        } catch (Exception e) {
            log.error("Error al buscar la categoría con ID {}: {}", categoryId, e.getMessage());
            throw new IllegalArgumentException("La categoría especificada no existe");
        }

        // Obtener el proveedor y validar existencia
        Provider provider;
        try {
            provider = providerRepository.findById(providerId);
        } catch (Exception e) {
            log.error("Error al buscar el proveedor con ID {}: {}", providerId, e.getMessage());
            throw new IllegalArgumentException("El proveedor especificado no existe");
        }

        Product product = Product.builder()
                .name(name)
                .price(price)
                .image(image)
                .stock(stock)
                .description(description)
                .category(category)
                .provider(provider)
                .build();

        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(UUID id, String name, double price, String image, int stock,
            String description, UUID categoryId, UUID providerId) {

        // Verificar que el producto existe
        productRepository.findById(id);

        // Obtener la categoría y validar existencia
        Category category = categoryRepository.findById(categoryId);

        // Obtener el proveedor y validar existencia
        Provider provider = providerRepository.findById(providerId);

        Product updatedProduct = Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .image(image)
                .stock(stock)
                .description(description)
                .category(category)
                .provider(provider)
                .build();

        Product savedProduct = productRepository.save(updatedProduct);

        // Actualizar en caché
        try {
            cacheService.saveInCache(id.toString(), savedProduct);
        } catch (Exception e) {
            log.warn("Error al actualizar la caché: {}", e.getMessage());
        }

        return savedProduct;
    }

    @Override
    public Product deleteProduct(UUID id) {
        Product product = productRepository.findById(id);

        productRepository.deleteById(id);

        // Eliminar de la caché
        try {
            cacheService.removeFromCache(id.toString());
        } catch (Exception e) {
            log.warn("Error al eliminar de la caché: {}", e.getMessage());
        }

        return product;
    }
}