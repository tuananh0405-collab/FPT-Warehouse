package com.wha.warehousemanagement.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportUpdateRequest {

        private String description;

        private String status;

        private String exportDate;

        private Integer warehouseIdTo;

        private Integer customerId;

        private String customerName;

        private String customerAddress;

        private String customerPhone;

        private String customerEmail;
}