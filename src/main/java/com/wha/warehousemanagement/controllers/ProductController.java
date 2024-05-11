package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.OrderDTO;
import com.wha.warehousemanagement.dtos.ProductDTO;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.services.OrderService;
import com.wha.warehousemanagement.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("product")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ResponseObject> addProduct(@Valid @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.addProduct(productDTO));
    }

    @GetMapping
    public ResponseEntity<ResponseObject> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getAllProducts(@PathVariable("id") int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateProduct(@PathVariable("id") int id, @RequestBody ProductDTO productDTO) {
        return ResponseEntity.ok(productService.updateProduct(id, productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteProduct(@PathVariable("id") int id) {
        return ResponseEntity.ok(productService.deleteProductById(id));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject> deleteAllProducts() {
        return ResponseEntity.ok(productService.deleteAllProducts());
    }

}
