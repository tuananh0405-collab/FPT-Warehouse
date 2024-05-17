package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.ProductDTO;
import com.wha.warehousemanagement.dtos.requests.ProductRequest;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.dtos.responses.ProductResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.CategoryMapper;
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

    private final CategoryMapper categoryMapper;

//    public ResponseObject<ProductResponse> addProduct(ProductRequest request) {
//        try {
//            if (request.getName() == null) {
//                throw new CustomException(ErrorCode.PRODUCT_NAME_BLANK);
//            } else if (productRepository.findByName(request.getName()).isPresent()) {
//                throw new CustomException(ErrorCode.PRODUCT_ALREADY_EXISTS);
//            }
//            Optional<Category> category = categoryRepository.getCategoryById(request.getCategoryId());
//            if (category.isEmpty()) {
//                throw new CustomException(ErrorCode.CATEGORY_NOT_FOUND);
//            }
//            Product product = new Product();
//            product.setName(request.getName());
//            product.setDescription(request.getDescription());
//            product.setQuantity(request.getQuantity());
//            product.setCountry(request.getCountry());
//            product.setReceivedDate(request.getReceivedDate());
//            product.setCategory(category.get());
//            productRepository.save(product);
//            Product pRepo = productRepository.findAll().get(productRepository.findAll().size() - 1);
//            ProductResponse response = new ProductResponse(
//                    pRepo.getId(),
//                    product.getName(),
//                    product.getDescription(),
//                    pRepo.getQuantity(),
//                    pRepo.getCountry(),
//                    pRepo.getReceivedDate(),
//                    categoryMapper.toDto(category.get())
//            );
//            return new ResponseObject<>(HttpStatus.OK.value(), "Product added successfully", response);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
//        }
//    }
//
//    public ResponseObject<List<ProductResponse>> getAllProducts() {
//        List<ProductResponse> list = new ArrayList<>();
//        productRepository.findAll().forEach(product -> {
//            CategoryResponse category = categoryMapper.toDto(product.getCategory());
//            ProductResponse response = new ProductResponse(
//                    product.getId(),
//                    product.getName(),
//                    product.getDescription(),
//                    product.getQuantity(),
//                    product.getCountry(),
//                    product.getReceivedDate(),
//                    category
//            );
//            list.add(response);
//        });
//        return new ResponseObject<>(HttpStatus.OK.value(), "Get all categories successfully", list);
//    }
//
//    public ResponseObject<ProductResponse> getProductById(int id) {
//        try {
//            Product product = productRepository.getProductById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
//            ProductResponse response = new ProductResponse(
//                    product.getId(),
//                    product.getName(),
//                    product.getDescription(),
//                    product.getQuantity(),
//                    product.getCountry(),
//                    product.getReceivedDate(),
//                    categoryMapper.toDto(product.getCategory())
//            );
//            return new ResponseObject<>(HttpStatus.OK.value(), "Get product by id successfully", response);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
//        }
//    }
//
//    public ResponseObject<ProductResponse> updateProduct(int id, ProductRequest request) {
//        try {
//            Product product = productRepository.getProductById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
//            if (request.getName() != null && !request.getName().equals(product.getName())) {
//                product.setName(request.getName());
//            }
//            if (request.getDescription() != null && !request.getDescription().equals(product.getDescription())) {
//                product.setDescription(request.getDescription());
//            }
//            if (request.getQuantity() != null) {
//                product.setQuantity(request.getQuantity());
//            }
//            if (request.getCountry() != null && !request.getCountry().equals(product.getCountry())) {
//                product.setCountry(request.getCountry());
//            }
//            if (request.getReceivedDate() != null && !request.getReceivedDate().equals(product.getReceivedDate())) {
//                product.setReceivedDate(request.getReceivedDate());
//            }
//            if (request.getCategoryId()!=null && !request.getCategoryId().equals(product.getCategory().getId())) {
//                Category cate = categoryRepository.findById(request.getCategoryId())
//                        .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
//                product.setCategory(cate);
//            }
//            productRepository.save(product);
//            ProductResponse response = new ProductResponse(
//                    product.getId(),
//                    product.getName(),
//                    product.getDescription(),
//                    product.getQuantity(),
//                    product.getCountry(),
//                    product.getReceivedDate(),
//                    categoryMapper.toDto(product.getCategory())
//            );
//            return new ResponseObject<>(HttpStatus.OK.value(), "Updated product successfully", response);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add product", null);
//        }
//    }
//
//    public ResponseObject<Object> deleteProductById(int id) {
//        try {
//            Product product = productRepository.getProductById(id).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
//            productRepository.delete(product);
//            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted product successfully", null);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete product", null);
//        }
//    }
//
//    public ResponseObject<Object> deleteAllProducts() {
//        try {
//            List<Product> list = new ArrayList<>(productRepository.findAll());
//            if (list.isEmpty()) {
//                throw new CustomException(ErrorCode.PRODUCT_NOT_FOUND);
//            }
//            productRepository.deleteAll();
//            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all products successfully", null);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete product", null);
//        }
//    }
}
