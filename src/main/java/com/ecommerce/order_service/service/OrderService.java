package com.ecommerce.order_service.service;

import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    public Order createOrder(Order order) {
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());

        order.setCreatedBy("SYSTEM");
        return orderRepository.save(order);
    }
}
