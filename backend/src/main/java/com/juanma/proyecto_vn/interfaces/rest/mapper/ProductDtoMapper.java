package com.juanma.proyecto_vn.interfaces.rest.mapper;

import com.juanma.proyecto_vn.domain.model.Category;
import com.juanma.proyecto_vn.domain.model.Product;
import com.juanma.proyecto_vn.domain.model.Provider;
import com.juanma.proyecto_vn.domain.model.User;
import com.juanma.proyecto_vn.domain.repository.UserRepository;
import com.juanma.proyecto_vn.interfaces.rest.dtos.category.CategoryResponseDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.product.GetProductDto;
import com.juanma.proyecto_vn.interfaces.rest.dtos.provider.ProviderDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Mapeador para convertir entre modelo de dominio y DTOs para la API REST
 */
@Component
public class ProductDtoMapper {
        @Autowired
        private UserRepository userRepository;

        /**
         * Convierte una entidad de dominio a un DTO para la API
         */
        public GetProductDto toDto(Product product) {
                return toDto(product, null);
        }

        /**
         * Convierte una entidad de dominio a un DTO para la API,
         * incluyendo si el producto está en la lista de deseos del usuario
         * 
         * @param product El producto a convertir
         * @param userId  El ID del usuario actual (puede ser null)
         * @return DTO con información del producto y si está marcado como favorito
         */
        public GetProductDto toDto(Product product, String userId) {
                if (product == null) {
                        return null;
                }

                // Por defecto, el producto no está en favoritos
                boolean isLiked = false;

                // Este método será reemplazado por una versión batch más eficiente
                if (userId != null && !userId.isEmpty()) {
                        try {
                                UUID userUuid = UUID.fromString(userId);
                                Optional<User> userOpt = userRepository.findById(userUuid);
                                if (userOpt.isPresent()) {
                                        User user = userOpt.get();
                                        if (user.getWishlists() != null) {
                                                isLiked = user.getWishlists().stream()
                                                                .anyMatch(p -> p.getId().equals(product.getId()));
                                        }
                                }
                        } catch (IllegalArgumentException e) {
                                // Ignorar errores de formato de UUID
                        }
                }

                GetProductDto productDto = GetProductDto.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .description(product.getDescription())
                                .category(product.getCategory() != null ? CategoryResponseDto.builder()
                                                .id(product.getCategory().getId())
                                                .name(product.getCategory().getName())
                                                .build() : null)
                                .provider(product.getProvider() != null ? ProviderDto.builder()
                                                .id(product.getProvider().getId())
                                                .name(product.getProvider().getName())
                                                .address(product.getProvider().getAddress())
                                                .build() : null)
                                .stock(product.getStock())
                                .isLiked(isLiked)
                                .build();

                // Manejo seguro de categorías
                Category category = product.getCategory();
                if (category != null) {
                        productDto = GetProductDto.builder()
                                        .id(productDto.getId())
                                        .name(productDto.getName())
                                        .price(productDto.getPrice())
                                        .image(productDto.getImage())
                                        .description(productDto.getDescription())
                                        .stock(productDto.getStock())
                                        .category(CategoryResponseDto.builder()
                                                        .id(category.getId())
                                                        .name(category.getName())
                                                        .build())
                                        .isLiked(isLiked)
                                        .build();
                }

                // Manejo seguro de proveedores
                Provider provider = product.getProvider();
                if (provider != null) {
                        productDto = GetProductDto.builder()
                                        .id(productDto.getId())
                                        .name(productDto.getName())
                                        .price(productDto.getPrice())
                                        .image(productDto.getImage())
                                        .description(productDto.getDescription())
                                        .stock(productDto.getStock())
                                        .category(productDto.getCategory())
                                        .provider(ProviderDto.builder()
                                                        .id(provider.getId())
                                                        .name(provider.getName())
                                                        .address(provider.getAddress())
                                                        .build())
                                        .isLiked(isLiked)
                                        .build();
                }

                return productDto;
        }

        public Product toDomain(GetProductDto productDto) {
                if (productDto == null) {
                        return null;
                }

                return Product.builder()
                                .id(productDto.getId())
                                .name(productDto.getName())
                                .price(productDto.getPrice())
                                .image(productDto.getImage())
                                .description(productDto.getDescription())
                                .stock(productDto.getStock())
                                .category(null) // Asignar la categoría según sea necesario
                                .provider(null) // Asignar el proveedor según sea necesario
                                .build();
        }

        /**
         * Convierte una lista de productos a DTOs para la API,
         * optimizando la verificación de favoritos para todos los productos a la vez
         * 
         * @param products Lista de productos a convertir
         * @param userId   ID del usuario actual (puede ser null)
         * @return Lista de DTOs con información de productos y si están marcados como
         *         favoritos
         */
        public List<GetProductDto> toDtoList(List<Product> products, String userId) {
                if (products == null || products.isEmpty()) {
                        return new ArrayList<>();
                }

                // Por defecto ningún producto está en favoritos
                final Set<UUID> likedProductIds = new HashSet<>();

                // Si hay un usuario, obtenemos su wishlist una sola vez
                if (userId != null && !userId.isEmpty()) {
                        try {
                                UUID userUuid = UUID.fromString(userId);
                                Optional<User> userOpt = userRepository.findById(userUuid);
                                if (userOpt.isPresent()) {
                                        User user = userOpt.get();
                                        if (user.getWishlists() != null) {
                                                // Guardamos solo los IDs de los productos favoritos para búsqueda
                                                // eficiente
                                                likedProductIds.addAll(user.getWishlists().stream()
                                                                .map(Product::getId)
                                                                .collect(Collectors.toSet()));
                                        }
                                }
                        } catch (IllegalArgumentException e) {
                                // Ignorar errores de formato de UUID
                        }
                }

                // Convertimos cada producto, verificando si está en favoritos usando el Set de
                // IDs
                return products.stream()
                                .map(product -> {
                                        boolean isLiked = likedProductIds.contains(product.getId());
                                        return toDto(product, isLiked);
                                })
                                .collect(Collectors.toList());
        }

        /**
         * Convierte una entidad de dominio a un DTO para la API,
         * con un valor explícito para isLiked
         * 
         * @param product El producto a convertir
         * @param isLiked Si el producto está marcado como favorito
         * @return DTO con información del producto y si está marcado como favorito
         */
        public GetProductDto toDto(Product product, boolean isLiked) {
                if (product == null) {
                        return null;
                }

                GetProductDto productDto = GetProductDto.builder()
                                .id(product.getId())
                                .name(product.getName())
                                .price(product.getPrice())
                                .image(product.getImage())
                                .description(product.getDescription())
                                .category(product.getCategory() != null ? CategoryResponseDto.builder()
                                                .id(product.getCategory().getId())
                                                .name(product.getCategory().getName())
                                                .build() : null)
                                .provider(product.getProvider() != null ? ProviderDto.builder()
                                                .id(product.getProvider().getId())
                                                .name(product.getProvider().getName())
                                                .address(product.getProvider().getAddress())
                                                .build() : null)
                                .stock(product.getStock())
                                .isLiked(isLiked)
                                .build();

                return productDto;
        }
}