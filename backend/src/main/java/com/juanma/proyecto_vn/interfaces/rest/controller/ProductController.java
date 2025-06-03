package com.juanma.proyecto_vn.interfaces.rest.controller;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.service.IProductService;
import com.juanma.proyecto_vn.domain.service.IUserService;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.CreateProductDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import com.juanma.proyecto_vn.interfaces.rest.mapper.ProductDtoMapper;
import com.juanma.proyecto_vn.interfaces.rest.advice.customExceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Adaptador primario que implementa la API REST para productos
 */
@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class ProductController {
    @Autowired
    private IProductService productService;

    @Autowired
    private ProductDtoMapper productDtoMapper;

    @Autowired
    private IUserService userService;

    @GetMapping("/")
    public ResponseEntity<Object> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String orderBy,
            @RequestParam(required = false) List<String> filterProvider,
            @RequestParam(required = false) Double filterPriceMin,
            @RequestParam(required = false) Double filterPriceMax,
            @RequestParam(required = false) String filterName,
            @RequestParam(required = false) List<String> filterCategory,
            HttpServletRequest request) {

        // Crear un mapa de filtros simplificado con todos los parámetros
        Map<String, Object> filters = new HashMap<>();
        if (filterName != null && !filterName.isEmpty()) {
            filters.put("name", filterName);
        }
        if (filterCategory != null && !filterCategory.isEmpty()) {
            filters.put("category", filterCategory);
        }
        if (filterProvider != null && !filterProvider.isEmpty()) {
            filters.put("provider", filterProvider);
        }
        if (filterPriceMin != null) {
            filters.put("priceMin", filterPriceMin);
        }
        if (filterPriceMax != null) {
            filters.put("priceMax", filterPriceMax);
        }

        Map<String, Object> result = productService.getAllProducts(page, size, sortBy, orderBy, filters);

        // Convertimos el objeto Page<Product> a una lista de DTOs
        if (result.containsKey("products")) {
            // Obtener el ID del usuario si está autenticado
            String userId = null;
            if (request.getAttribute("userId") != null) {
                userId = request.getAttribute("userId").toString();
            }

            final String finalUserId = userId; // Variable final para usar en lambda

            @SuppressWarnings("unchecked")
            Page<Product> productPage = (Page<Product>) result.get("products");
            List<GetProductDto> productDtos = productDtoMapper.toDtoList(productPage.getContent(), finalUserId);

            result.put("products", productDtos);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/get-names")
    public ResponseEntity<Object> getAllNames() {
        List<Map<String, String>> names = productService.getAllNames();
        return ResponseEntity.ok(names);
    }

    @GetMapping("/random")
    public ResponseEntity<Object> getRandomProducts(@RequestParam int quantity, HttpServletRequest request) {
        try {
            List<Product> products = productService.getRandomProducts(quantity);

            // Obtener el ID del usuario si está autenticado
            String userId = null;
            if (request.getAttribute("userId") != null) {
                userId = request.getAttribute("userId").toString();
            }

            final String finalUserId = userId;

            List<GetProductDto> dtos = products.stream()
                    .map(product -> {
                        try {
                            return productDtoMapper.toDto(product, finalUserId);
                        } catch (Exception e) {
                            log.error("Error al mapear producto aleatorio {}: {}", product.getId(), e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            log.error("Error al obtener productos aleatorios: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener productos aleatorios: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable UUID id, HttpServletRequest request) {
        String userId = null;
        if (request.getAttribute("userId") != null) {
            userId = request.getAttribute("userId").toString();
        }

        Product product = productService.getProductById(id, userId);
        if (product == null) {
            throw new ResourceNotFoundException("No se encontró el producto con ID: " + id);
        }

        GetProductDto dto = productDtoMapper.toDto(product, userId);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/")
    public ResponseEntity<Object> createProduct(@Valid @RequestBody CreateProductDto createProductDto,
            BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        try {
            Product createdProduct = productService.createProduct(
                    createProductDto.getName(),
                    createProductDto.getPrice(),
                    createProductDto.getImage(),
                    createProductDto.getStock(),
                    createProductDto.getDescription(),
                    UUID.fromString(createProductDto.getCategory()),
                    UUID.fromString(createProductDto.getProvider()));

            GetProductDto responseDto = productDtoMapper.toDto(createdProduct, null);
            return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.warn("Datos inválidos al crear producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            log.error("Error al crear producto: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al crear el producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateProduct(@PathVariable UUID id,
            @Valid @RequestBody CreateProductDto updateProductDto,
            BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> {
                errors.put(error.getField(), error.getDefaultMessage());
            });
            return ResponseEntity.badRequest().body(errors);
        }

        Product updatedProduct = productService.updateProduct(
                id,
                updateProductDto.getName(),
                updateProductDto.getPrice(),
                updateProductDto.getImage(),
                updateProductDto.getStock(),
                updateProductDto.getDescription(),
                UUID.fromString(updateProductDto.getCategory()),
                UUID.fromString(updateProductDto.getProvider()));

        GetProductDto responseDto = productDtoMapper.toDto(updatedProduct, null);
        return ResponseEntity.ok(responseDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable UUID id) {
        try {
            Product deletedProduct = productService.deleteProduct(id);
            GetProductDto responseDto = productDtoMapper.toDto(deletedProduct, null);
            return ResponseEntity.ok(responseDto);
        } catch (ResourceNotFoundException e) {
            log.warn("Producto no encontrado al eliminar: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception e) {
            log.error("Error al eliminar producto {}: {}", id, e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al eliminar el producto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Obtiene la lista de productos favoritos del usuario autenticado
     */
    @GetMapping("/wishlist")
    public ResponseEntity<?> getWishlist(HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuario no autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            List<Product> wishlist = userService.getUserWishlist(userId);

            // Usamos el nuevo método optimizado para convertir la lista completa
            List<GetProductDto> wishlistDtos = productDtoMapper.toDtoList(wishlist, userId.toString());

            return ResponseEntity.ok(wishlistDtos);
        } catch (Exception e) {
            log.error("Error al obtener wishlist: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al obtener la lista de favoritos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Añade o elimina un producto de la wishlist del usuario autenticado
     */
    @PostMapping("/wishlist/{productId}")
    public ResponseEntity<?> toggleWishlist(@PathVariable UUID productId, HttpServletRequest request) {
        UUID userId = (UUID) request.getAttribute("userId");
        if (userId == null) {
            Map<String, String> error = new HashMap<>();
            error.put("message", "Usuario no autenticado");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }

        try {
            // Obtenemos solo los IDs de la wishlist para una verificación más eficiente
            Set<UUID> wishlistIds = userService.getUserWishlistIds(userId);
            boolean isInWishlist = wishlistIds.contains(productId);
            Map<String, Object> response = new HashMap<>();

            // Si ya está en la wishlist, lo eliminamos
            if (isInWishlist) {
                boolean removed = userService.removeFromWishlist(userId, productId);
                response.put("action", "removed");
                response.put("success", removed);
                response.put("message", "Producto eliminado de favoritos");
            }
            // Si no está en la wishlist, lo añadimos
            else {
                boolean added = userService.addToWishlist(userId, productId);
                response.put("action", "added");
                response.put("success", added);
                response.put("message", "Producto añadido a favoritos");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error al modificar wishlist: {}", e.getMessage());
            Map<String, String> error = new HashMap<>();
            error.put("message", "Error al modificar la lista de favoritos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
