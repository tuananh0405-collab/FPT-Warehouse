package com.wha.warehousemanagement.dtos.responses;

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
public class ImportDetailResponse {
    private Integer id;
    private ProductResponse product;
    private Integer quantity;
    private String expiredAt;
    private String zoneName;

    public ImportDetailResponse(ProductResponse product, Integer quantity, String expiredAt, String zoneName) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.expiredAt = expiredAt;
        this.zoneName = zoneName;
    }
}
