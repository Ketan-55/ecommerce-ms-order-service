package com.ecommerce.order_service.dto;

import lombok.Data;

@Data
public class APIResponse <T>{

    private String status;
    private String message;
    private T data;


}
