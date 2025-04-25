package com.juanma.proyecto_vn.Controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juanma.proyecto_vn.Dtos.Product.CreateProductDto;
import com.juanma.proyecto_vn.Dtos.Product.GetProductDto;
import com.juanma.proyecto_vn.interfaces.IProductService;

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
    public ResponseEntity<Object> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "16") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String orderBy,
            @RequestParam(defaultValue = "") String filterBy,
            @RequestParam(defaultValue = "") String filterValue) {
        Map<String, String> products = productService.getAllProducts(page, size, sortBy, orderBy, filterBy,
                filterValue);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/random")
    public ResponseEntity<Object> getRandomProducts(@RequestParam int quantity) {
        return ResponseEntity.ok().body(productService.getRandomProducts(quantity));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GetProductDto> getProductById(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<?> createProduct(@RequestBody @Valid CreateProductDto product, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        GetProductDto createdProduct = productService.createProduct(product);

        return ResponseEntity.status(201).body(createdProduct);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> editProduct(@PathVariable UUID id,
            @RequestBody @Valid CreateProductDto product, BindingResult result) {
        if (result.hasErrors()) {
            return validation(result);
        }

        GetProductDto updatedProduct = productService.updateProduct(product, id);
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<?> validation(BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(err -> {
            errors.put("message", "Error en campo " + err.getField() + ": " + err.getDefaultMessage());
        });

        return ResponseEntity.badRequest().body(errors);
    }

}
