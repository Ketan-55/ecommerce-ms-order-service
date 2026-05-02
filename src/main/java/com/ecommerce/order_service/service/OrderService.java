package com.ecommerce.order_service.service;

import com.ecommerce.order_service.config.RestTemplateConfig;
import com.ecommerce.order_service.dto.APIResponse;
import com.ecommerce.order_service.dto.ProductResponseDTO;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    OrderRepository orderRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public Order createOrder(Order order) {
        // Call Product Service
        String url ="http://localhost:8084/products/"+ order.getProductId();

        APIResponse<ProductResponseDTO> apiresponse = restTemplate.getForObject(url, APIResponse.class);

        ProductResponseDTO product = objectMapper.convertValue(apiresponse.getData(), ProductResponseDTO.class);

        // Calculate total price
        Double totalPrice = product.getPrice() * order.getQuantity();
        order.setTotalPrice(totalPrice);

        //
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setCreatedBy("SYSTEM");

        return orderRepository.save(order);
    }
}
