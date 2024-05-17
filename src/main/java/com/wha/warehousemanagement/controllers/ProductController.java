package com.wha.warehousemanagement.controllers;

import com.wha.warehousemanagement.dtos.ProductDTO;
import com.wha.warehousemanagement.dtos.requests.ProductRequest;
import com.wha.warehousemanagement.dtos.responses.ProductResponse;
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

//    @PostMapping
//    public ResponseEntity<ResponseObject<ProductResponse>> addProduct(@RequestBody ProductRequest request) {
//        return ResponseEntity.ok(productService.addProduct(request));
//    }
//
//    @GetMapping
//    public ResponseEntity<ResponseObject<List<ProductResponse>>> getAllProducts() {
//        return ResponseEntity.ok(productService.getAllProducts());
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<ResponseObject<ProductResponse>> getAllProducts(@PathVariable("id") int id) {
//        return ResponseEntity.ok(productService.getProductById(id));
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<ResponseObject<ProductResponse>> updateProduct(@PathVariable("id") int id, @RequestBody ProductRequest request) {
//        return ResponseEntity.ok(productService.updateProduct(id, request));
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<ResponseObject<Object>> deleteProduct(@PathVariable("id") int id) {
//        return ResponseEntity.ok(productService.deleteProductById(id));
//    }
//
//    @DeleteMapping
//    public ResponseEntity<ResponseObject<Object>> deleteAllProducts() {
//        return ResponseEntity.ok(productService.deleteAllProducts());
//    }

}
