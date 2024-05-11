package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.ProductDTO;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.Product;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public ResponseObject addProduct(ProductDTO productDTO) {
        if (productDTO.getName() == null) {
            return new ResponseObject("400", "Name is blank", null);
        } else if (productRepository.findByName(productDTO.getName()).isPresent()) {
            return new ResponseObject("400", "Product with name " + productDTO.getName() + " already exists", null);
        }
        Optional<Category> category = categoryRepository.getCategoryById(productDTO.getCategoryId());
        if (category.isEmpty()) {
            return new ResponseObject("400", "Category is invalid", null);
        }
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setQuantity(productDTO.getQuantity());
        product.setCountry(productDTO.getCountry());
        product.setReceivedDate(productDTO.getReceivedDate());
        product.setCategory(category.get());
        productRepository.save(product);
        return new ResponseObject("200", "Product added successfully", productDTO);
    }

    public ResponseObject getAllProducts() {
        List<Product> list = new ArrayList<>(productRepository.findAll());
        return new ResponseObject("200", "Get all products successfully", list);
    }

    public ResponseObject getProductById(int id) {
        Optional<ProductDTO> category = productRepository.getProductDTOById(id);
        return category.map(
                        value -> new ResponseObject("200", "Get product successfully", value))
                .orElseGet(() -> new ResponseObject("500", "Not found", null));
    }

    public ResponseObject updateProduct(int id, ProductDTO productDTO) {
        Optional<Product> product = productRepository.getProductById(id);
        if (product.isPresent()) {
            Product product1 = product.get();
            product1.setName(productDTO.getName());
            product1.setDescription(productDTO.getDescription());
            product1.setQuantity(productDTO.getQuantity());
            product1.setCountry(productDTO.getCountry());
            product1.setReceivedDate(productDTO.getReceivedDate());
            return new ResponseObject("200", "Updated product successfully", productRepository.save(product1));
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteProductById(int id) {
        Optional<Product> product = productRepository.getProductById(id);
        if (product.isPresent()) {
            productRepository.delete(product.get());
            return new ResponseObject("200", "Deleted product successfully", product.get());
        } else {
            return new ResponseObject("500", "Not found", null);
        }
    }

    public ResponseObject deleteAllProducts() {
        List<Product> list = new ArrayList<>(productRepository.findAll());
        if (!list.isEmpty()) {
            productRepository.deleteAll();
            return new ResponseObject("200", "Deleted product successfully", null);
        } else {
            return new ResponseObject("500", "No product in db", null);
        }

    }

}
