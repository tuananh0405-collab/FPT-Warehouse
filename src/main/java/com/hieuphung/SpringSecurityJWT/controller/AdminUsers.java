package com.hieuphung.SpringSecurityJWT.controller;

import com.hieuphung.SpringSecurityJWT.dto.ReqRes;
import com.hieuphung.SpringSecurityJWT.model.Product;
import com.hieuphung.SpringSecurityJWT.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminUsers {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/public/product")
    public ResponseEntity<Object> getAllProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @PostMapping("/admin/saveproduct")
    public ResponseEntity<Object> signUp(@RequestBody ReqRes productRequest) {
        Product productToSave = new Product();
        productToSave.setName(productRequest.getUsername());
        return ResponseEntity.ok(productRepository.save(productToSave));
    }

    @GetMapping("/staff/alone")
    public ResponseEntity<Object> userAlone() {
        return ResponseEntity.ok("Users alone can access this api only");
    }

    @GetMapping("/adminstaff/both")
    public ResponseEntity<Object> bothAdminAndUserApi() {
        return ResponseEntity.ok("Both Admin and Users can access this api");
    }

}
