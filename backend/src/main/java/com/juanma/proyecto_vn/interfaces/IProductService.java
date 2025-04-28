package com.juanma.proyecto_vn.interfaces;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.juanma.proyecto_vn.Dtos.Product.CreateProductDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;

import jakarta.servlet.http.HttpServletRequest;

public interface IProductService {
    Map<String, Object> getAllProducts(int page, int size, String sortBy, String orderBy, String filterBy,
            String filterValue);

    GetProductDto getProductById(UUID id, HttpServletRequest request);

    GetProductDto createProduct(CreateProductDto product);

    List<GetProductDto> getRandomProducts(int quantity);

    GetProductDto updateProduct(CreateProductDto product, UUID id);

    GetProductDto deleteProduct(UUID id);
}
