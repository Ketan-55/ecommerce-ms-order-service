package com.ecommerce.order_service.service;

import com.ecommerce.order_service.config.RestTemplateConfig;
import com.ecommerce.order_service.dto.APIResponse;
import com.ecommerce.order_service.dto.ProductResponseDTO;
import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.event.OrderEvent;
import com.ecommerce.order_service.kafka.KafkaProducer;
import com.ecommerce.order_service.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class OrderService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient webClient;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    KafkaProducer kafkaProducer;

    ObjectMapper objectMapper = new ObjectMapper();

    public Order createOrder(Order order) {
        // Call Product Service
        String url ="http://localhost:8084/products/"+ order.getProductId();
       //USING REST TEMPLATE
         //APIResponse apiresponse = restTemplate.getForObject(url, APIResponse.class);

        //USING WEBCLIENT
        APIResponse apiresponse = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(APIResponse.class)
                .block();

        ProductResponseDTO product = objectMapper.convertValue(apiresponse.getData(), ProductResponseDTO.class);



        // Calculate total price
        Double totalPrice = product.getPrice() * order.getQuantity();
        order.setTotalPrice(totalPrice);

        //set a
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setCreatedBy("SYSTEM");

        Order savedOrder = orderRepository.save(order);
        //create order event
        OrderEvent orderEvent = new OrderEvent();
        orderEvent.setOrderId(savedOrder.getId());
        orderEvent.setProductId(savedOrder.getProductId());
        orderEvent.setTotalPrice(savedOrder.getTotalPrice());
        orderEvent.setUserId(savedOrder.getUserId());

        //send event to kafka topic
        kafkaProducer.sendOrderEvent(orderEvent);

        return savedOrder;
    }
}
