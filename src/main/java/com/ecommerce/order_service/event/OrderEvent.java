package com.ecommerce.order_service.event;

import lombok.Data;

@Data
public class OrderEvent {

    private Long orderId;
    private Long userId;
    private String productId;
    private Double totalPrice;
    private Integer quantity;
}
