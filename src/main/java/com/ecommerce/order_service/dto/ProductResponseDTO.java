package com.ecommerce.order_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponseDTO {

    private String id;
    private String name;
    private Double price;
}
