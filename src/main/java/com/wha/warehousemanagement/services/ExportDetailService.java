package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ExportDetailRequest;
import com.wha.warehousemanagement.dtos.requests.ExportDetailUpdateRequest;
import com.wha.warehousemanagement.dtos.requests.SuggestedExportProductsRequest;
import com.wha.warehousemanagement.dtos.responses.*;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ExportDetailMapper;
import com.wha.warehousemanagement.mappers.ExportMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.mappers.ZoneMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final ZoneRepository zoneRepository;
    private final ZoneMapper zoneMapper;

    public ResponseObject<?> getAllExportDetails() {
        try {
            List<ExportDetailResponse> responses = exportDetailRepository.findAll()
                    .stream()
                    .map(imp -> {
                        ExportDetailResponse response = exportDetailMapper.toDto(imp);
                        response.setExport(exportMapper.toDto(imp.getExport()));
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        response.setZone(zoneMapper.toDto(imp.getZone()));
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

    public ResponseObject<?> checkInventoryQuantityForUpdate(ExportDetailRequest request) {
        try {
            ExportDetail exportDetail = exportDetailRepository.findById(request.getExportId())
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_DETAIL_NOT_FOUND));
            int originalQuantity = exportDetail.getQuantity();
            int newQuantity = request.getQuantity();
            int quantityDifference = originalQuantity - newQuantity;

            Inventory inventory;
            if (request.getExpiredAt() == null) {
                inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAtIsNull(
                                request.getProductId(), request.getZoneId())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            } else {
                inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                                request.getProductId(), request.getZoneId(), request.getExpiredAt())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
            }

            System.out.println(inventory);

            if (inventory.getQuantity() + quantityDifference < 0) {
                throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
            }

            return new ResponseObject<>(HttpStatus.OK.value(), "Sufficient inventory", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to check inventory quantity", null);
        }
    }


    public ResponseObject<?> getExportDetailById(Integer id) {
        try {
            ExportDetailResponse response = exportDetailRepository.findById(id)
                    .map(imp -> {
                        ExportDetailResponse exportDetailResponse = exportDetailMapper.toDto(imp);
                        exportDetailResponse.setExport(exportMapper.toDto(imp.getExport()));
                        exportDetailResponse.setProduct(productMapper.toDto(imp.getProduct()));
                        exportDetailResponse.setZone(zoneMapper.toDto(imp.getZone()));
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

    @Transactional
    public ResponseObject<?> createExportDetail(List<ExportDetailRequest> requests) {
        try {
            List<ExportDetail> exportDetails = new ArrayList<>();
            for (ExportDetailRequest request : requests) {
                // Create and save export detail
                ExportDetail exportDetail = new ExportDetail();
                exportDetail = update(exportDetail, request);
                exportDetails.add(exportDetail);

                // Update inventory
                Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                                request.getProductId(), request.getZoneId(), request.getExpiredAt())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                // Update available quantity
                inventory.setQuantity(inventory.getQuantity() - request.getQuantity());

                inventoryRepository.save(inventory);
            }

            exportDetailRepository.saveAll(exportDetails);
            List<ExportDetailResponse> responses = new ArrayList<>();
            for (ExportDetail x : exportDetails) {
                responses.add(exportDetailMapper.toDto(x));
            }
            return new ResponseObject<>(HttpStatus.OK.value(), "Export details created and inventory updated successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to create export details and update inventory", null);
        }
    }

    @Transactional
    public ResponseObject<?> updateExportDetail(List<ExportDetailUpdateRequest> request) {
        try {
            for (ExportDetailUpdateRequest req : request) {
                ExportDetail exportDetail = exportDetailRepository.findById(req.getExportDetailId())
                        .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_DETAIL_NOT_FOUND));
                int originalQuantity = exportDetail.getQuantity();
                int newQuantity = req.getQuantity();
                int quantityDifference = originalQuantity - newQuantity;

                // Adjust inventory
                Inventory inventory;
                if (exportDetail.getExpiredAt() == null) {
                    inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAtIsNull(
                            exportDetail.getProduct().getId(),
                            exportDetail.getZone().getId()
                    ).orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
                } else {
                    inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                            exportDetail.getProduct().getId(),
                            exportDetail.getZone().getId(),
                            exportDetail.getExpiredAt()
                    ).orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
                }

                // Check if the inventory quantity is sufficient
                if (inventory.getQuantity() + quantityDifference < 0) {
                    throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
                }

                inventory.setQuantity(inventory.getQuantity() + quantityDifference);
                inventoryRepository.save(inventory);

                exportDetail.setQuantity(newQuantity);
                exportDetailRepository.save(exportDetail);
            }
            return new ResponseObject<>(HttpStatus.OK.value(), "Export detail updated successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export detail", null);
        }
    }

    @Transactional
    public ResponseObject<?> deleteExportDetail(List<Integer> exportDetailIds) {
        try {
            for (Integer exportDetailId : exportDetailIds) {
                ExportDetail exportDetail = exportDetailRepository.findById(exportDetailId)
                        .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_DETAIL_NOT_FOUND));

                Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                        exportDetail.getProduct().getId(),
                        exportDetail.getZone().getId(),
                        exportDetail.getExpiredAt()
                ).orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                inventory.setQuantity(inventory.getQuantity() + exportDetail.getQuantity());
                inventoryRepository.save(inventory);

                // Delete export detail
                exportDetailRepository.delete(exportDetail);
            }
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
        exportDetail.setExpiredAt(request.getExpiredAt());
        exportDetail.setZone(zoneRepository.findById(request.getZoneId())
                .orElseThrow(() -> new CustomException(ErrorCode.ZONE_NOT_FOUND)));
        return exportDetail;
    }

    public List<ExportDetailWithExportIdResponse> getExportDetailWithExportIdByExportId(Integer exportId) {
        try {
            return exportDetailRepository.findByExportId(exportId)
                    .stream()
                    .map(imp -> {
                        return new ExportDetailWithExportIdResponse(
                                null,
                                imp.getExport().getId(),
                                productMapper.toDto(imp.getProduct()),
                                imp.getQuantity(),
                                imp.getExpiredAt(),
                                zoneMapper.toDto(imp.getZone())
                        );
                    })
                    .toList();
        } catch (Exception e) {
            return null;
        }
    }

    public ResponseObject<List<SuggestedExportProductsResponse>> suggestExportInventory(List<SuggestedExportProductsRequest> requests) {
        // request se la nhan vao 1 list cac product kem theo quantity.
        // request co warehouseId de xac dinh kho hang can lay hang
        try {
            // Map lưu trữ các inventory của mỗi product trong warehouse cụ thể
            Map<Integer, List<Inventory>> inventoryMap = new HashMap<>();

            // Vòng lặp để lấy ra các inventory của mỗi product theo warehouseId và sắp xếp theo expiredAt
            requests.forEach(request -> {
                // Chỉ lấy ra hàng còn hạn sử dụng
                List<Inventory> inventories = inventoryRepository.findByProductIdAndWarehouseIdOrderByExpiredAtAsc(
                        request.getProductId(), request.getWarehouseId());
                inventoryMap.put(request.getProductId(), inventories);
            });


            // Kết quả gợi ý sẽ được lưu vào danh sách suggestedDetails
            List<SuggestedExportProductsResponse> suggestedDetails = new ArrayList<>();

            // Vòng lặp để xử lý từng request
            for (SuggestedExportProductsRequest request : requests) {
                List<Inventory> inventories = inventoryMap.get(request.getProductId());
                // int totalQuantityInWarehouse = inventories.stream().mapToInt(Inventory::getQuantity).sum();
                int totalQuantityInWarehouse = inventoryRepository.countTotalQuantityByProductIdAndWarehouseId(request.getProductId(), request.getWarehouseId());
                if (totalQuantityInWarehouse < request.getQuantity()) {
                    // Nếu số lượng tồn kho không đủ để xuất thì trả về thông báo
                    suggestedDetails.add(new SuggestedExportProductsResponse(
                            null,
                            "Không đủ hàng",
                            null,
                            totalQuantityInWarehouse,
                            null,
                            null
                    ));
                    continue;
                }

                if (inventories == null || inventories.isEmpty()) {
                    throw new CustomException(ErrorCode.INVENTORY_NOT_FOUND);
                }

                // Yêu cầu số lượng cần xuất
                int quantityToExport = request.getQuantity();

                // Sắp xếp inventory theo expiredAt tăng dần và quanity giảm dần nếu expiredAt bằng nhau
                // Nếu số lượng hàng của inventory bằng với yêu cầu, ưu tiên inventory đó trước
                int finalQuantityToExport = quantityToExport;
                inventories.sort((i1, i2) -> {
                    int dateComparison = i1.getExpiredAt().compareTo(i2.getExpiredAt());
                    if (dateComparison != 0) {
                        return dateComparison;
                    } else {
                        if (i1.getQuantity() >= finalQuantityToExport && i2.getQuantity() < finalQuantityToExport) {
                            return -1;
                        } else if (i1.getQuantity() < finalQuantityToExport && i2.getQuantity() >= finalQuantityToExport) {
                            return 1;
                        } else {
                            return i2.getQuantity() - i1.getQuantity();
                        }
                    }
                });

                for (Inventory inventory : inventories) {
                    ProductResponse product = productMapper.toDto(productRepository.findById(request.getProductId())
                            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));

                    // Nếu số lượng inventory đủ để xuất thì thêm vào suggestedDetails
                    if (inventory.getQuantity() >= quantityToExport) {
                        suggestedDetails.add(new SuggestedExportProductsResponse(
                                inventory.getId(),
                                "",
                                product,
                                quantityToExport,
                                inventory.getExpiredAt(),
                                inventory.getZone().getId()
                        ));
                        quantityToExport = 0;
                        break;
                    } else {
                        // Nếu số lượng inventory không đủ để xuất thì lấy hết số lượng inventory đó -> giảm số lượng cần xuất -> lấy inventory tiếp theo
                        suggestedDetails.add(new SuggestedExportProductsResponse(
                                inventory.getId(),
                                "",
                                product,
                                inventory.getQuantity(),
                                inventory.getExpiredAt(),
                                inventory.getZone().getId()
                        ));
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
                        response.setExport(exportMapper.toDto(imp.getExport()));
                        response.setProduct(productMapper.toDto(imp.getProduct()));
                        response.setZone(zoneMapper.toDto(imp.getZone()));
                        return response;
                    })
                    .toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Export details retrieved successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get export details", null);
        }
    }

    public ResponseObject<List<ProductsInExportResponse>> getProductsInExportByExportId(Integer exportId) {
        try {
            List<ExportDetail> exportDetails = exportDetailRepository.findByExportId(exportId);
            List<ProductsInExportResponse> responses = exportDetails.stream()
                    .map(imp -> {
                        return ProductsInExportResponse.builder()
                                .id(imp.getId())
                                .product(productMapper.toDto(imp.getProduct()))
                                .quantity(imp.getQuantity())
                                .expiredAt(imp.getExpiredAt().toString())
                                .zone(zoneMapper.toDto(imp.getZone()))
                                .build();
                    })
                    .toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Products in export retrieved successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get export details", null);
        }
    }

//    @Transactional
//    public ResponseObject<?> updateAndAddExportDetails(List<ExportDetailRequest> requests, Integer exportId) {
//        try {
//            List<ExportDetail> exportDetails = new ArrayList<>();
//
//            // Fetch existing export details
//            List<ExportDetail> existingExportDetails = exportDetailRepository.findByExportId(exportId);
//            Map<Integer, ExportDetail> existingExportDetailsMap = existingExportDetails.stream()
//                    .collect(Collectors.toMap(ExportDetail::getId, ed -> ed));
//
//            // Identify which export details are updated or added
//            for (ExportDetailRequest request : requests) {
//                ExportDetail exportDetail = null;
//                for (ExportDetail ed : existingExportDetails) {
//                    if (ed.getProduct().getId().equals(request.getProductId()) &&
//                            ed.getZone().getId().equals(request.getZoneId()) &&
//                            ed.getExpiredAt().equals(request.getExpiredAt())) {
//                        exportDetail = ed;
//                        break;
//                    }
//                }
//
//                Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
//                                request.getProductId(), request.getZoneId(), request.getExpiredAt())
//                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
//
//                if (exportDetail != null) {
//                    // Update existing export detail
//                    int oldQuantity = exportDetail.getQuantity();
//                    int newQuantity = request.getQuantity();
//                    int quantityDifference = newQuantity - oldQuantity;
//
//                    // Hoàn lại số lượng cũ vào kho trước khi cập nhật
//                    inventory.setQuantity(inventory.getQuantity() + oldQuantity);
//
//                    if (inventory.getQuantity() < newQuantity) {
//                        throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
//                    }
//
//                    // Trừ đi số lượng mới từ kho
//                    inventory.setQuantity(inventory.getQuantity() - newQuantity);
//
//                    exportDetail.setQuantity(newQuantity);
//                    exportDetails.add(exportDetail);
//                    existingExportDetailsMap.remove(exportDetail.getId());
//                } else {
//                    // Create new export detail
//                    exportDetail = new ExportDetail();
//                    exportDetail = update(exportDetail, request);
//                    exportDetails.add(exportDetail);
//
//                    // Trừ đi số lượng từ kho
//                    if (inventory.getQuantity() < request.getQuantity()) {
//                        throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
//                    }
//                    inventory.setQuantity(inventory.getQuantity() - request.getQuantity());
//                }
//
//                // Save adjusted inventory
//                inventoryRepository.save(inventory);
//            }
//
//            // Identify and process deletions
//            for (ExportDetail exportDetail : existingExportDetailsMap.values()) {
//                Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
//                                exportDetail.getProduct().getId(), exportDetail.getZone().getId(), exportDetail.getExpiredAt())
//                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));
//
//                // Hoàn lại số lượng vào kho khi xóa
//                inventory.setQuantity(inventory.getQuantity() + exportDetail.getQuantity());
//                inventoryRepository.save(inventory);
//                exportDetailRepository.delete(exportDetail);
//            }
//
//            exportDetailRepository.saveAll(exportDetails);
//            List<ExportDetailResponse> responses = exportDetails.stream()
//                    .map(exportDetailMapper::toDto)
//                    .collect(Collectors.toList());
//            return new ResponseObject<>(HttpStatus.OK.value(), "Export details updated, added, and inventory adjusted successfully", responses);
//        } catch (CustomException e) {
//            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export details, add new ones, and adjust inventory", null);
//        }
//    }

    @Transactional
    public ResponseObject<?> updateAndAddExportDetails(List<ExportDetailRequest> newRequests, Integer exportId) {
        try {
            // Fetch existing export details
            List<ExportDetail> existingExportDetails = exportDetailRepository.findByExportId(exportId);

            // Map for quick lookup of existing export details by key
            Map<String, ExportDetail> existingExportDetailsMap = existingExportDetails.stream()
                    .collect(Collectors.toMap(
                            ed -> ed.getProduct().getId() + "-" + ed.getZone().getId() + "-" + ed.getExpiredAt().getTime(),
                            ed -> ed
                    ));

            // Map for quick lookup of new export details by key
            Map<String, ExportDetailRequest> newRequestsMap = newRequests.stream()
                    .collect(Collectors.toMap(
                            req -> req.getProductId() + "-" + req.getZoneId() + "-" + req.getExpiredAt().getTime(),
                            req -> req
                    ));

            List<ExportDetail> detailsToUpdate = new ArrayList<>();
            List<ExportDetail> detailsToAdd = new ArrayList<>();
            List<ExportDetail> detailsToDelete = new ArrayList<>();

            // Identify updates and deletions
            for (ExportDetail existingDetail : existingExportDetails) {
                String key = existingDetail.getProduct().getId() + "-" + existingDetail.getZone().getId() + "-" + existingDetail.getExpiredAt().getTime();
                ExportDetailRequest newRequest = newRequestsMap.get(key);

                if (newRequest != null) {
                    // Update if quantity has changed
                    if (!existingDetail.getQuantity().equals(newRequest.getQuantity())) {
                        Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                                        existingDetail.getProduct().getId(), existingDetail.getZone().getId(), existingDetail.getExpiredAt())
                                .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                        System.out.println("Inventory for Update: " + inventory);

                        int oldQuantity = existingDetail.getQuantity();
                        int newQuantity = newRequest.getQuantity();
                        inventory.setQuantity(inventory.getQuantity() + oldQuantity - newQuantity);

                        if (inventory.getQuantity() < 0) {
                            throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
                        }

                        existingDetail.setQuantity(newQuantity);
                        inventoryRepository.save(inventory);
                        detailsToUpdate.add(existingDetail);
                    }
                    newRequestsMap.remove(key);
                } else {
                    // Mark for deletion
                    Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                                    existingDetail.getProduct().getId(), existingDetail.getZone().getId(), existingDetail.getExpiredAt())
                            .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                    System.out.println("Inventory for Delete: " + inventory);

                    inventory.setQuantity(inventory.getQuantity() + existingDetail.getQuantity());
                    inventoryRepository.save(inventory);
                    detailsToDelete.add(existingDetail);
                }
            }

            // Identify additions
            for (ExportDetailRequest newRequest : newRequestsMap.values()) {
                Inventory inventory = inventoryRepository.findByProductIdAndZoneIdAndExpiredAt(
                                newRequest.getProductId(), newRequest.getZoneId(), newRequest.getExpiredAt())
                        .orElseThrow(() -> new CustomException(ErrorCode.INVENTORY_NOT_FOUND));

                System.out.println("Inventory for Adding: " + inventory);

                if (inventory.getQuantity() < newRequest.getQuantity()) {
                    throw new CustomException(ErrorCode.INSUFFICIENT_INVENTORY);
                }

                inventory.setQuantity(inventory.getQuantity() - newRequest.getQuantity());
                inventoryRepository.save(inventory);

                ExportDetail newDetail = new ExportDetail();
                newDetail = update(newDetail, newRequest);
                detailsToAdd.add(newDetail);
            }

            // Perform updates, additions, and deletions
            exportDetailRepository.saveAll(detailsToUpdate);
            exportDetailRepository.saveAll(detailsToAdd);
            exportDetailRepository.deleteAll(detailsToDelete);

            List<ExportDetailResponse> responses = new ArrayList<>();
            for (ExportDetail ed : detailsToUpdate) {
                responses.add(exportDetailMapper.toDto(ed));
            }
            for (ExportDetail ed : detailsToAdd) {
                responses.add(exportDetailMapper.toDto(ed));
            }

            return new ResponseObject<>(HttpStatus.OK.value(), "Export details updated, added, and inventory adjusted successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export details, add new ones, and adjust inventory", null);
        }
    }


}
