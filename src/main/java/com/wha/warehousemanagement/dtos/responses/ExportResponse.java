package com.wha.warehousemanagement.dtos.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.wha.warehousemanagement.models.ExportDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExportResponse {

    private Integer id;

    private String customerName;

    private String description;

    private String status;

    private Date exportDate;

    private String customerAddress;

    private List<ExportDetailResponse> exportDetails;
}
