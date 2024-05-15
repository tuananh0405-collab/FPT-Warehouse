package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.ProductDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.Product;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.CategoryRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final CategoryRepository categoryRepository;

    public ResponseObject<ProductDTO> addProduct(ProductDTO productDTO) {
        try {
            if (productDTO.getName() == null) {
                throw new CustomException(ErrorCode.PRODUCT_NAME_BLANK);
            } else if (productRepository.findByName(productDTO.getName()).isPresent()) {
                throw new CustomException(ErrorCode.PRODUCT_ALREADY_EXISTS);
            }
            Optional<Category> category = categoryRepository.getCategoryById(productDTO.getCategoryId());
            if (category.isEmpty()) {
                throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
            }
            Product product = new Product(
                    productDTO.getName(),
                    productDTO.getDescription(),
                    productDTO.getQuantity(),
                    productDTO.getCountry(),
                    productDTO.getReceivedDate(),
                    category.get()
            );
            productRepository.save(product);
            return new ResponseObject<>(HttpStatus.OK.value(), "Product added successfully", productDTO);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
        }
    }

    public ResponseObject<List<Product>> getAllProducts() {
        try {
            List<Product> list = new ArrayList<>(productRepository.findAll());
            if (!list.isEmpty()) {
                return new ResponseObject<>(HttpStatus.OK.value(), "Get all products successfully", list);
            } else {
                throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
            }
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
        }
    }

    public ResponseObject<ProductDTO> getProductById(int id) {
        try {
            ProductDTO productDTO = productRepository.getProductDTOById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Get product by id successfully", productDTO);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
        }
    }

    public ResponseObject<Product> updateProduct(int id, ProductDTO productDTO) {
        try {
            Product product = productRepository.getProductById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            if (productDTO.getName() != null && !productDTO.getName().equals(product.getName())) {
                product.setName(productDTO.getName());
            }
            if (productDTO.getDescription() != null && !productDTO.getDescription().equals(product.getDescription())) {
                product.setDescription(productDTO.getDescription());
            }
            if (productDTO.getQuantity() != null) {
                product.setQuantity(productDTO.getQuantity());
            }
            if (productDTO.getCountry() != null && !productDTO.getCountry().equals(product.getCountry())) {
                product.setCountry(productDTO.getCountry());
            }
            if (productDTO.getReceivedDate() != null && !productDTO.getReceivedDate().equals(product.getReceivedDate())) {
                product.setReceivedDate(productDTO.getReceivedDate());
            }
            productRepository.save(product);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated product successfully", product);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
        }
    }

    public ResponseObject<Object> deleteProductById(int id) {
        try {
            Product product = productRepository.getProductById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
            productRepository.delete(product);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted product successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete product", null);
        }
    }

    public ResponseObject<Object> deleteAllProducts() {
        try {
            List<Product> list = new ArrayList<>(productRepository.findAll());
            if (list.isEmpty()) {
                throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
            }
            productRepository.deleteAll();
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all products successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete product", null);
        }
    }
}
