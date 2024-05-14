package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.ProductDTO;
import com.wha.warehousemanagement.models.Product;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ResponseObject<ProductDTO>> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.addProduct(productDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject<List<Product>>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject<ProductDTO>> getAllProducts(@PathVariable("id") int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject<Product>> updateProduct(@PathVariable("id") int id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject<Object>> deleteProduct(@PathVariable("id") int id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject<Object>> deleteAllProducts() {
        return ResponseEntity.ok(productService.deleteAllProducts());
    }

}
