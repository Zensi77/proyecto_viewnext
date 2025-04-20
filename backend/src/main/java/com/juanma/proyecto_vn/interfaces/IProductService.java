package com.juanma.proyecto_vn.interfaces;

import java.util.List;
import java.util.UUID;

import com.juanma.proyecto_vn.Dtos.Product.CreateProductDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;
import com.juanma.proyecto_vn.models.Product;

public interface IProductService {
    List<GetProductDto> getAllProducts(int page, int size, String sortBy, String orderBy, String filterBy,
            String filterValue);

    GetProductDto getProductById(UUID id);

    GetProductDto createProduct(CreateProductDto product);

    GetProductDto updateProduct(CreateProductDto product, UUID id);

    GetProductDto deleteProduct(UUID id);
}
