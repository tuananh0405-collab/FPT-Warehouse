package com.wha.warehousemanagement.services;

import com.wha.warehousemanagement.dtos.ProviderDTO;
import com.wha.warehousemanagement.exceptions.CustomException;
import com.wha.warehousemanagement.exceptions.ErrorCode;
import com.wha.warehousemanagement.models.Provider;
import com.wha.warehousemanagement.models.ResponseObject;
import com.wha.warehousemanagement.repositories.ProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderRepository providerRepository;

    public ResponseObject<ProviderDTO> addProvider(ProviderDTO providerDTO) {
        if (providerDTO.getName() == null || providerDTO.getName().trim().isEmpty()) {
            throw new CustomException(ErrorCode.PROVIDER_NAME_BLANK);
        } else if (providerRepository.findByName(providerDTO.getName()).isPresent()) {
            throw new CustomException(ErrorCode.PROVIDER_ALREADY_EXISTS);
        }
        try {
            Provider provider = new Provider();
            provider.setName(providerDTO.getName());
            provider.setEmail(providerDTO.getEmail());
            provider.setPhone(providerDTO.getPhone());
            provider.setAddress(providerDTO.getAddress());
            providerRepository.save(provider);
            return new ResponseObject<>(HttpStatus.OK.value(), "Provider added successfully", providerDTO);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.PROVIDER_ADD_FAILED);
        }
    }

    public ResponseObject<List<ProviderDTO>> getAllProviders() {
        List<ProviderDTO> list = new ArrayList<>();
        providerRepository.findAll().forEach(provider -> {
            ProviderDTO providerDTO = new ProviderDTO(
                    provider.getId(),
                    provider.getName(),
                    provider.getEmail(),
                    provider.getPhone(),
                    provider.getAddress()
            );
            list.add(providerDTO);
        });
        return new ResponseObject<>(HttpStatus.OK.value(), "Get all providers successfully", list);
    }

    public ResponseObject<ProviderDTO> getProviderById(int id) {
        Provider provider = providerRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
        ProviderDTO providerDTO = new ProviderDTO(
                provider.getId(),
                provider.getName(),
                provider.getEmail(),
                provider.getPhone(),
                provider.getAddress()
        );
        return new ResponseObject<>(HttpStatus.OK.value(), "Get provider by id successfully", providerDTO);
    }

    public ResponseObject<ProviderDTO> updateProvider(int id, ProviderDTO providerDTO) {
        try {
            Provider provider = providerRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.PROVIDER_NOT_FOUND));
            if (providerDTO.getName() != null &&
                    !providerDTO.getName().trim().isEmpty() &&
                    !providerDTO.getName().equals(provider.getName()) &&
                    providerRepository.existsByName(providerDTO.getName())) {
                throw new CustomException(ErrorCode.PROVIDER_ALREADY_EXISTS);
            }
            if (providerDTO.getName() != null && !providerDTO.getName().trim().isEmpty()) {
                provider.setName(providerDTO.getName());
            }
            if (providerDTO.getEmail() != null && !providerDTO.getEmail().trim().isEmpty()) {
                provider.setEmail(providerDTO.getEmail());
            }
            if (providerDTO.getPhone() != null && !providerDTO.getPhone().trim().isEmpty()) {
                provider.setPhone(providerDTO.getPhone());
            }
            if (providerDTO.getAddress() != null && !providerDTO.getAddress().trim().isEmpty()) {
                provider.setAddress(providerDTO.getAddress());
            }
            providerRepository.save(provider);
            return new ResponseObject<>(HttpStatus.OK.value(), "Updated provider successfully", providerDTO);
        } catch (CustomException e) {
            return new ResponseObject<>(e.getErrorCode().getCode(), e.getMessage(), null);
        } catch (Exception e) {
            return new ResponseObject<>(HttpStatus.BAD_REQUEST.value(), "Failed to update provider", null);
        }
    }

    public ResponseObject<Object> deleteProviderById(int id) {
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

    public ResponseObject<Object> deleteAllProviders() {
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
