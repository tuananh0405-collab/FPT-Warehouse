package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.requests.ProviderRequest;
import com.wha.warehousemanagement.dtos.responses.CategoryResponse;
import com.wha.warehousemanagement.dtos.responses.ProductResponse;
import com.wha.warehousemanagement.dtos.responses.ProviderResponse;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.mappers.ProviderMapper;
import com.wha.warehousemanagement.models.Category;
import com.wha.warehousemanagement.models.Provider;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;
    private final ProviderMapper providerMapper;

    public ResponseObject<?> addProvider(ProviderRequest providerRequest) {
        if (providerRequest.getName() == null || providerRequest.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.PROVIDER_NAME_BLANK);
        } else if (providerRepository.findByName(providerRequest.getName()).isPresent()) {
            throw new CustomException(ErrorCode.PROVIDER_ALREADY_EXISTS);
        }
        try {
            Provider provider = new Provider();
            provider.setName(providerRequest.getName());
            provider.setEmail(providerRequest.getEmail());
            provider.setPhone(providerRequest.getPhone());
            provider.setAddress(providerRequest.getAddress());
            providerRepository.save(provider);
            Optional<ProviderResponse> response = getLastProviderResponse();
            if (response.isEmpty()) {
                throw new CustomException(ErrorCode.PROVIDER_ADD_FAILED);
            }
            return new ResponseObject<>(HttpStatus.OK.value(),
                    "Provider added successfully",
                    response.get());
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to add provider", null);
        }
    }

    private Optional<ProviderResponse> getLastProviderResponse() {
        List<ProviderResponse> responses = providerRepository.getAllProviderResponses();
        if (!responses.isEmpty()) {
            return Optional.of(responses.get(0));
        } else {
            return Optional.empty();
        }
    }

    public ResponseObject<?> getAllProviders() {
        try {
            List<ProviderResponse> list = providerRepository.findAll()
                    .stream()
                    .map(providerMapper::toDto)
                    .collect(Collectors.toList());
            if (list.isEmpty()) {
                throw new CustomException(ErrorCode.PROVIDER_NOT_FOUND);
            }
            return new ResponseObject<>(HttpStatus.OK.value(), "Get all providers successfully", list);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get all providers", null);
        }
    }

    public ResponseObject<?> getProviderById(int id) {
        try {
            Provider provider = providerRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
            ProviderResponse response = providerMapper.toDto(provider);
            return new ResponseObject<>(HttpStatus.OK.value(), "Get provider by id successfully", response);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to get provider by id", null);
        }
    }

    public ResponseObject<?> updateProvider(int id, ProviderRequest request) {
        try {
            Provider provider = providerRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
            if (request.getName() != null && !request.getName().trim().isEmpty()) {
                if (providerRepository.existsByName(request.getName())) {
                    throw new CustomException(ErrorCode.PROVIDER_ALREADY_EXISTS);
                }
                provider.setName(request.getName());
            }
            if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
                provider.setEmail(request.getEmail());
            }
            if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
                provider.setPhone(request.getPhone());
            }
            if (request.getAddress() != null && !request.getAddress().trim().isEmpty()) {
                provider.setAddress(request.getAddress());
            }
            providerRepository.save(provider);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated provider successfully", request);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update provider", null);
        }
    }

    public ResponseObject<?> deleteProviderById(int id) {
        try {
            Provider provider = providerRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
            providerRepository.delete(provider);
            return new ResponseObject<>(HttpStatus.OK.value(), "Deleted provider successfully", null);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete provider", null);
        }
    }

    public ResponseObject<?> deleteAllProviders() {
        try {
            List<Provider> list = providerRepository.findAll();
            if (!list.isEmpty()) {
                providerRepository.deleteAll();
                return new ResponseObject<>(HttpStatus.OK.value(), "Deleted all providers successfully", null);
            } else {
                return new ResponseObject<>(HttpStatus.NO_CONTENT.value(), "No providers to delete", null);
            }
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to delete providers", null);
        }
    }

}
