package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ImportDetailRequest;
import com.wha.warehousemanagement.dtos.responses.ImportDetailResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ImportDetailMapper;
import com.wha.warehousemanagement.mappers.ImportMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.ImportDetailRepository;
import com.wha.warehousemanagement.repositories.ImportRepository;
import com.wha.warehousemanagement.repositories.ProductRepository;
import com.wha.warehousemanagement.repositories.ZoneRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportDetailService {
    private final ImportDetailRepository importDetailRepository;
    private final ImportDetailMapper importDetailMapper;
    private final ImportRepository importRepository;
    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;
    private final ProductMapper productMapper;

    public ResponseObject<?> getAllImportDetails() {
        try {
            List<ImportDetailResponse> responses = importDetailRepository.findAll()
                    .stream()
                    .map(imp -> {
                        ImportDetailResponse response = importDetailMapper.toDto(imp);
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        response.setZoneName(imp.getZone().getName());
                        return response;
                    })
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details retrieved successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all import details", null);
        }
    }

    public ResponseObject<?> getImportDetailById(Integer id) {
        try {
            ImportDetailResponse response = importDetailRepository.findById(id)
                    .map(imp -> {
                        ImportDetailResponse importDetailResponse = importDetailMapper.toDto(imp);
                        importDetailResponse.setProduct(productMapper.toDto(imp.getProduct()));
                        importDetailResponse.setZoneName(imp.getZone().getName());
                        return importDetailResponse;
                    })
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_DETAIL_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get import details by id", null);
        }
    }

    public ResponseObject<?> createImportDetail(List<ImportDetailRequest> requests) {
        try {
            List<ImportDetail> importDetails = new ArrayList<>();
            for (ImportDetailRequest request : requests) {
                ImportDetail importDetail = new ImportDetail();
                importDetails.add(update(importDetail, request));
            }
            importDetailRepository.saveAll(importDetails);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details created successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to create import details", null);
        }
    }

    public ResponseObject<?> updateImportDetail(int id, ImportDetailRequest request) {
        try {
            ImportDetail importDetail = importDetailRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_DETAIL_NOT_FOUND));
            importDetailRepository.save(update(importDetail, request));
            ImportDetailResponse response = importDetailMapper.toDto(importDetail);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details updated successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update import details", null);
        }
    }

    public ResponseObject<?> deleteImportDetail(int id) {
        try {
            ImportDetail importDetail = importDetailRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_DETAIL_NOT_FOUND));
            importDetailRepository.delete(importDetail);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import details deleted successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete import details", null);
        }
    }

    private ImportDetail update(ImportDetail importDetail, ImportDetailRequest request) {
        Import anImport = importRepository.findById(request.getImportId())
                .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        Zone zone = zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new CustomException(ErrorCode.ZONE_NOT_FOUND));
        importDetail.setAnImport(anImport);
        importDetail.setProduct(product);
        importDetail.setQuantity(request.getQuantity());
        importDetail.setExpiredAt(request.getExpiredAt());
        importDetail.setZone(zone);
        return importDetail;
    }

}
