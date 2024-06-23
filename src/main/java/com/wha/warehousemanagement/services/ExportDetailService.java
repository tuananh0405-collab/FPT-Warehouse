package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ExportDetailRequest;
import com.wha.warehousemanagement.dtos.requests.SuggestedExportProductsRequest;
import com.wha.warehousemanagement.dtos.responses.*;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ExportDetailMapper;
import com.wha.warehousemanagement.mappers.ExportMapper;
import com.wha.warehousemanagement.mappers.ProductMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.*;
import jakarta.transaction.Transactional;
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
    private final InventoryRepository inventoryRepository;

    public ResponseObject<?> getAllExportDetails() {
        try {
            List<ExportDetailResponse> responses = exportDetailRepository.findAll()
                    .stream()
                    .map(imp -> {
                        ExportDetailResponse response = exportDetailMapper.toDto(imp);
                        response.setExport(exportMapper.toDto(imp.getExport()));
                        response.setProduct(productMapper.toDto(imp.getProduct()));
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
                        exportDetailResponse.setExport(exportMapper.toDto(imp.getExport()));
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

                // Update held quantity and available quantity
                inventory.setHeldQuantity((inventory.getHeldQuantity() == null ? 0 : inventory.getHeldQuantity()) + request.getQuantity());
                inventory.setQuantity(inventory.getQuantity() - request.getQuantity());

                inventoryRepository.save(inventory);
            }

            exportDetailRepository.saveAll(exportDetails);
            return new ResponseObject<>(HttpStatus.OK.value(), "Export details created and inventory updated successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to create export details and update inventory", null);
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

    private ExportDetail update(ExportDetail exportDetail, ExportDetailRequest request) {
        exportDetail.setExport(exportRepository.findById(request.getExportId())
                .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND)));
        exportDetail.setProduct(productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));
        exportDetail.setQuantity(request.getQuantity());
        exportDetail.setExpiredAt(request.getExpiredAt());
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
                                imp.getExpiredAt()
                        );
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
                if(totalQuantityInWarehouse < request.getQuantity()){
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
                List<Inventory> tempInventories = new ArrayList<>(inventories);

                // Sắp xếp inventory theo quantity giảm dần và expiredAt tăng dần
                tempInventories.sort((i1, i2) -> {
                    // Nếu quantity khác nhau thì sắp xếp theo quantity giảm dần
                    if (!i2.getQuantity().equals(i1.getQuantity())) {
                        return i2.getQuantity() - i1.getQuantity();
                    } else {
                        // Nếu quantity bằng nhau thì sắp xếp theo expiredAt tăng dần
                        return i1.getExpiredAt().compareTo(i2.getExpiredAt());
                    }
                });

                for (Inventory inventory : tempInventories) {
                    ProductResponse product = productMapper.toDto(productRepository.findById(request.getProductId())
                            .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND)));

                    // Nếu số lượng inventory đủ để xuất thì thêm vào suggestedDetails
                    if (inventory.getQuantity() >= quantityToExport) {
                        suggestedDetails.add(new SuggestedExportProductsResponse(
                                null,
                                "",
                                product,
                                quantityToExport,
                                inventory.getExpiredAt(),
                                inventory.getZone().getName()
                        ));
                        // Giảm số lượng inventory cần xuất
                        //inventory.setQuantity(inventory.getQuantity() - quantityToExport);
                        break;
                    } else {
                        // Nếu số lượng inventory không đủ để xuất thì lấy hết số lượng inventory đó -> giảm số lượng cần xuất -> lấy inventory tiếp theo
                        suggestedDetails.add(new SuggestedExportProductsResponse(
                                null,
                                "",
                                product,
                                inventory.getQuantity(),
                                inventory.getExpiredAt(),
                                inventory.getZone().getName()
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

}
