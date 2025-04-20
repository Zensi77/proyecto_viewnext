package com.juanma.proyecto_vn.Controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.Dtos.Product.CreateProductDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;
import com.juanma.proyecto_vn.Service.ProductServiceImpl;
import com.juanma.proyecto_vn.interfaces.IProductService;
import com.juanma.proyecto_vn.models.Product;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController // Indica que esta clase es un controlador REST
@RequestMapping("/api/v1/products") // Indica la ruta base para las peticiones a este controlador
public class ProductController {
    @Autowired
    private IProductService productService;

    @GetMapping
    public ResponseEntity<List<GetProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String orderBy,
            @RequestParam(defaultValue = "") String filterBy,
            @RequestParam(defaultValue = "") String filterValue) {
        return ResponseEntity.ok(productService.getAllProducts(page, size, sortBy, orderBy, filterBy, filterValue));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public GetProductDto createProduct(@RequestBody @Valid CreateProductDto product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public GetProductDto editProduct(@PathVariable UUID id, @RequestBody @Valid CreateProductDto product) {
        return productService.updateProduct(product, id);
    }

}
