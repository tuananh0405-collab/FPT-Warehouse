package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ImportRequest;
import com.wha.warehousemanagement.dtos.responses.ImportDetailResponse;
import com.wha.warehousemanagement.dtos.responses.ImportResponse;
import com.wha.warehousemanagement.dtos.responses.ProviderResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ImportDetailMapper;
import com.wha.warehousemanagement.mappers.ImportMapper;
import com.wha.warehousemanagement.mappers.ProviderMapper;
import com.wha.warehousemanagement.models.*;
import com.wha.warehousemanagement.repositories.ImportDetailRepository;
import com.wha.warehousemanagement.repositories.ImportRepository;
import com.wha.warehousemanagement.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ImportService {

    private final ImportRepository importRepository;
    private final ImportMapper importMapper;
    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;
    private final ImportDetailRepository importDetailRepository;
    private final ImportDetailMapper importDetailMapper;

    public ResponseObject<?> addImport(ImportRequest request) {
        try {
            Provider provider = providerRepository.findById(request.getProviderId())
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
            Import anImport = new Import();
            anImport.setDescription(request.getDescription());
            anImport.setStatus(Status.valueOf(request.getStatus()));
            anImport.setReceivedDate(request.getReceivedDate());
            anImport.setProvider(provider);
            importRepository.save(anImport);
            ImportResponse response = importMapper.toDto(anImport);
            return new ResponseObject<>(HttpStatus.OK.value(), "Import added successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        }
        catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add import", null);
        }
    }

    public ResponseObject<?> getAllImports() {
        try {
            List<ImportResponse> responses = importRepository.findAll()
                    .stream().map(anImport -> {
                                ImportResponse response = importMapper.toDto(anImport);

                                List<ImportDetailResponse> importDetailResponses = importDetailRepository
                                        .findAllByAnImport_Id(anImport.getId())
                                        .stream().map(importDetailMapper::toDto).collect(Collectors.toList());

                                ProviderResponse providerResponse = providerMapper.toDto(anImport.getProvider());
                                response.setImportDetails(importDetailResponses);
                                response.setProvider(providerResponse);
                                return response;
                            }
                    )
                    .collect(Collectors.toList());
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all imports successfully", responses);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all imports", null);
        }
    }

    public ResponseObject<?> getImportById(int id) {
        try {
            ImportResponse response = importRepository.findById(id)
                    .map(anImport -> {
                        ImportResponse importResponse = importMapper.toDto(anImport);

                        List<ImportDetailResponse> importDetailResponses = importDetailRepository
                                .findAllByAnImport_Id(anImport.getId())
                                .stream().map(importDetailMapper::toDto).collect(Collectors.toList());

                        ProviderResponse providerResponse = providerMapper.toDto(anImport.getProvider());
                        importResponse.setImportDetails(importDetailResponses);
                        importResponse.setProvider(providerResponse);
                        return importResponse;
                    })
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
            return new ResponseObject<>(HttpStatus.OK.value(), "Get import by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get import by id", null);
        }
    }

    public ResponseObject<?> updateImport(int id, ImportRequest request) {
        try {
            Import anImport = importRepository.getImportById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                anImport.setDescription(request.getDescription());
            }
            if (request.getStatus() != null) {
                anImport.setStatus(Status.valueOf(request.getStatus()));
            }
            if (request.getReceivedDate() != null) {
                anImport.setDescription(request.getDescription());
            }
            if (request.getProviderId() != null) {
                Provider provider = providerRepository.findById(request.getProviderId())
                        .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
                anImport.setProvider(provider);
            }
            importRepository.save(anImport);
            ImportResponse response = importMapper.toDto(anImport);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated import successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update import", null);
        }
    }

    public ResponseObject<?> deleteImportById(int id) {
        try {
            Import anImport = importRepository.getImportById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.IMPORT_NOT_FOUND));
            importRepository.delete(anImport);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted import successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete import", null);
        }
    }

    public ResponseObject<?> deleteAllImports() {
        try {
            List<Import> list = importRepository.findAll();
            if (!list.isEmpty()) {
                importRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all imports successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No imports to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete imports", null);
        }
    }
}
