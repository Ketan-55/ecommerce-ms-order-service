package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.event.OrderEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class KafkaConsumer {

    @Autowired
    WebClient webClient;

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(OrderEvent event){

        System.out.println("🔥 Processing order: " + event.getOrderId());

        // Later:
        // update inventory
        // send notification

        String url ="http://localhost:8084/products/"+ event.getProductId()+"/reduce-stock?quantity="+ event.getQuantity();


            webClient.put().
                    uri(url).
                    retrieve().
                    bodyToMono(String.class).
                    block();

            System.out.println("✅ Stock reduced for product: " + event.getProductId());


        }
}

