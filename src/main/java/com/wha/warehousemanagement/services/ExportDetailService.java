package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ExportDetailRequest;
import com.wha.warehousemanagement.dtos.responses.ExportDetailResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ExportDetailMapper;
import com.wha.warehousemanagement.mappers.ExportMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.ExportDetail;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.ExportDetailRepository;
import com.wha.warehousemanagement.repositories.ExportRepository;
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
public class ExportDetailService {
    private final ExportDetailRepository exportDetailRepository;
    private final ExportDetailMapper exportDetailMapper;
    private final ExportMapper exportMapper;
    private final ProductMapper productMapper;
    private final ExportRepository exportRepository;
    private final ProductRepository productRepository;
    private final ZoneRepository zoneRepository;

    public ResponseObject<?> getAllExportDetails() {
        try {
            List<ExportDetailResponse> responses = exportDetailRepository.findAll()
                    .stream()
                    .map(imp -> {
                        ExportDetailResponse response = exportDetailMapper.toDto(imp);
                        response.setExportBill(exportMapper.toDto(imp.getExport()));
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        response.setZoneName(imp.getZone().getName());
                        return response;
                    })
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "export details retrieved successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all export details", null);
        }
    }

    public ResponseObject<?> getExportDetailById(Integer id) {
        try {
            ExportDetailResponse response = exportDetailRepository.findById(id)
                    .map(imp -> {
                        ExportDetailResponse exportDetailResponse = exportDetailMapper.toDto(imp);
                        exportDetailResponse.setExportBill(exportMapper.toDto(imp.getExport()));
                        exportDetailResponse.setProduct(productMapper.toDto(imp.getProduct()));
                        return exportDetailResponse;
                    })
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_DETAIL_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Export detail retrieved successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get export detail", null);
        }
    }

    public ResponseObject<?> createExportDetail(List<ExportDetailRequest> requests) {
        try {
            List<ExportDetail> exportDetails = new ArrayList<>();
            requests.forEach(request -> {
                ExportDetail exportDetail = new ExportDetail();
                exportDetails.add(update(exportDetail, request));
            });
            exportDetailRepository.saveAll(exportDetails);
            return new ResponseObject<>(HttpStatus.OK.value(), "Export details created successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to create export details", null);
        }
    }

    public ResponseObject<?> updateExportDetail(Integer id, ExportDetailRequest request) {
        try {
            ExportDetail exportDetail = exportDetailRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_DETAIL_NOT_FOUND));
            update(exportDetail, request);
            exportDetailRepository.save(exportDetail);
            return new ResponseObject<>(HttpStatus.OK.value(), "Export detail updated successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export detail", null);
        }
    }

    public ResponseObject<?> deleteExportDetail(Integer id) {
        try {
            ExportDetail exportDetail = exportDetailRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_DETAIL_NOT_FOUND));
            exportDetailRepository.delete(exportDetail);
            return new ResponseObject<>(HttpStatus.OK.value(), "Export detail deleted successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete export detail", null);
        }
    }

    private ExportDetail update(ExportDetail exportDetail, ExportDetailRequest request) {
        exportDetail.setExport(exportRepository.findById(request.getExportId())
                .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND)));
        exportDetail.setProduct(productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
        exportDetail.setQuantity(request.getQuantity());
        exportDetail.setZone(zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new CustomException(ErrorCode.ZONE_NOT_FOUND)));
        return exportDetail;
    }

}
