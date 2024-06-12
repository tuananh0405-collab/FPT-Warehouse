package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ExportRequest;
import com.wha.warehousemanagement.dtos.responses.ExportResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ExportMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.CustomerRepository;
import com.wha.warehousemanagement.repositories.ExportRepository;
import com.wha.warehousemanagement.repositories.WarehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportService {

    private final ExportRepository exportRepository;
    private final ExportMapper exportMapper;
    private final ExportDetailService exportDetailService;
    private final WarehouseRepository warehouseRepository;
    private final CustomerRepository customerRepository;

    public ResponseObject<?> addExport(ExportRequest request) {
        try {
            Export export = new Export();
            export.setDescription(request.getDescription());
            export.setStatus(Status.valueOf(request.getStatus()));
            export.setExportDate(new Date());
            export.setExportType(ImportExportType.valueOf(request.getExportType()));
            export.setTransferKey(request.getTransferKey());
            export.setWarehouseFrom(warehouseRepository.findById(request.getWarehouseIdFrom()).orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            export.setWarehouseTo(warehouseRepository.findById(request.getWarehouseIdTo()).orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            export.setCustomer(customerRepository.findById(request.getCustomerId()).orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND)));
            exportRepository.save(export);
            ExportResponse response = exportMapper.toDto(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Export added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        }
    }

    public ResponseObject<?> getAllExports() {
        try {
            List<ExportResponse> responses = exportRepository.findAll()
                    .stream().map(exportMapper::toDto
                    )
                    .toList();
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all exports successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all exports", null);
        }
    }

    public ResponseObject<?> getExportById(int id) {
        try {
            Export export = exportRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));
            ExportResponse response = exportMapper.toDto(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get export by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get export by id", null);
        }
    }

    public ResponseObject<?> updateExport(int id, ExportRequest request) {
        try {
            Export export = exportRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.EXPORT_NOT_FOUND));
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                export.setDescription(request.getDescription());
            }
            if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
                export.setStatus(Status.valueOf(request.getStatus()));
            }
            if (request.getExportType() != null && !request.getExportType().trim().isEmpty()) {
                export.setExportType(ImportExportType.valueOf(request.getExportType()));
            }
            if (request.getTransferKey() != null && !request.getTransferKey().trim().isEmpty()) {
                export.setTransferKey(request.getTransferKey());
            }
            if (request.getWarehouseIdFrom() != null) {
                export.setWarehouseFrom(warehouseRepository.findById(request.getWarehouseIdFrom()).orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            }
            if (request.getWarehouseIdTo() != null) {
                export.setWarehouseTo(warehouseRepository.findById(request.getWarehouseIdTo()).orElseThrow(() -> new CustomException(ErrorCode.WAREHOUSE_NOT_FOUND)));
            }
            if (request.getCustomerId() != null) {
                export.setCustomer(customerRepository.findById(request.getCustomerId()).orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND)));
            }
            exportRepository.save(export);
            ExportResponse response = exportMapper.toDto(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated export successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update export", null);
        }
    }

    public ResponseObject<?> deleteExportById(int id) {
        try {
            Export export = exportRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
            exportRepository.delete(export);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted export successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete export", null);
        }
    }

    public ResponseObject<?> deleteAllExports() {
        try {
            List<Export> list = exportRepository.findAll();
            if (!list.isEmpty()) {
                exportRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all exports successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No exports to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete exports", null);
        }
    }

//    public ResponseObject<List<ExportResponse>> searchExportDetails(
//            int page, int limit, String sortBy,String direction, int warehouseId, String exportDate, String customerName,
//            String customerAddress, String status) {
//        try {
//            if (direction == "asc") {
//                direction = "ASC";
//            } else {
//                direction = "DESC";
//            }
//            Sort.Direction sortDirection = Sort.Direction.fromString(direction.equals("asc") ? "ASC" : "DESC");
//            Pageable pageable = PageRequest.of(page, limit, Sort.by(sortDirection, sortBy));
//            List<ExportResponse> exports = exportMapper.toDto(exportRepository.searchExportDetails(
//                    warehouseId, exportDate, customerName, customerAddress, status, pageable).getContent());
//            return new ResponseObject<>(HttpStatus.OK.value(), "Search export details successfully", exports);
//        } catch (Exception e) {
//            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to search export details", null);
//        }
//    }

}
