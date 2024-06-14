package com.wha.warehousemanagement.dtos.requests;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wha.warehousemanagement.dtos.responses.CustomerResponse;
import com.wha.warehousemanagement.dtos.responses.WarehouseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportByAdminReqRequest {
    private String description;

    private String status;

    private Date exportDate;

    private String exportType;

    private Integer warehouseFromId;

    private Integer warehouseToId;

    // Map<productId, quantity>
    private Map<Integer, Integer> productsRequested;

    // which admin requested this export
    //private UserRequest requestedBy;
}
