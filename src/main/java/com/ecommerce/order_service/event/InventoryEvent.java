package com.ecommerce.order_service.event;

import lombok.Data;

@Data
public class InventoryEvent {

    private Long orderId;

    private String productId;

    private String status;

    private String message;
}
