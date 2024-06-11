package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ExportDetailRequest;
import com.wha.warehousemanagement.dtos.responses.ExportDetailResponse;
import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.dtos.responses.ProductResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ExportDetailMapper;
import com.wha.warehousemanagement.mappers.ExportMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    private final InventoryRepository inventoryRepository;

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
            return new ResponseObject<>(HttpStatus.OK.value(), "Export details retrieved successfully", responses);
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

    public List<ExportDetailResponse> getExportDetailByExportId(Integer exportId) {
        try {
            return exportDetailRepository.findByExportId(exportId)
                    .stream()
                    .map(imp -> {
                        ExportDetailResponse response = exportDetailMapper.toDto(imp);
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        response.setZoneName(imp.getZone().getName());
                        return response;
                    })
                    .toList();
        } catch (Exception e) {
            return null;
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

    public ResponseObject<?> suggestExportInventory(List<ExportDetailRequest> requests) {
        try {
            Map<Integer, List<Inventory>> inventoryMap = new HashMap<>();

            // find all product inventories and put it in the map
            requests.forEach(request -> {
                List<Inventory> inventories = inventoryRepository.findByProductIdOrderByExpiredAtAsc(request.getProductId());
                inventoryMap.put(request.getProductId(), inventories);
            });

            List<ExportDetailResponse> suggestedDetails = new ArrayList<>();

            // loop through the requests and find the suggested details
            for (ExportDetailRequest request : requests) {
                List<Inventory> inventories = inventoryMap.get(request.getProductId());

                if (inventories == null || inventories.isEmpty()) {
                    throw new CustomException(ErrorCode.INVENTORY_NOT_FOUND);
                }

                int quantityToExport = request.getQuantity();

                // loop through the inventories and find the suggested details
                for (Inventory inventory : inventories) {

                    ProductResponse product = productMapper.toDto(productRepository.findById(request.getProductId())
                            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));

                    ExportResponse export = exportMapper.toDto(exportRepository.findById(request.getExportId())
                            .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND)));

                    // if the quantity to export is less than the quantity in the inventory
                    if (inventory.getQuantity() >= quantityToExport) {
                        suggestedDetails.add(new ExportDetailResponse(null, export,
                                product,
                                quantityToExport,
                                inventory.getZone() == null ? null : inventory.getZone().getName(),
                                inventory.getExpiredAt())); // thêm expiredAt
                        break;
                    } else {
                        // if the quantity to export is greater than the quantity in the inventory
                        suggestedDetails.add(new ExportDetailResponse(null, export,
                                product,
                                inventory.getQuantity(),
                                inventory.getZone() == null ? null : inventory.getZone().getName(),
                                inventory.getExpiredAt())); // thêm expiredAt
                        quantityToExport -= inventory.getQuantity();
                    }

                }
            }

            return new ResponseObject<>(HttpStatus.OK.value(), "Suggested export details", suggestedDetails);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to suggest export details", null);
        }
    }

    public ResponseObject<?> getExportDetailsByExportId(Integer exportId) {
        try {
            List<ExportDetailResponse> responses = exportDetailRepository.findByExportId(exportId)
                    .stream()
                    .map(imp -> {
                        ExportDetailResponse response = exportDetailMapper.toDto(imp);
                        response.setExportBill(exportMapper.toDto(imp.getExport()));
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        response.setZoneName(imp.getZone().getName());

                        // Lấy thông tin expiredAt từ inventory
                        Inventory inventory = inventoryRepository.findByProductIdAndZoneId(imp.getProduct().getId(), imp.getZone().getId());
                        if (inventory != null) {
                            response.setExpiredAt(inventory.getExpiredAt());
                        }

                        return response;
                    })
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Export details retrieved successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get export details", null);
        }
    }

}
