package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    @KafkaListener(topics = "order-topic", groupId = "order-group")
    public void consume(OrderEvent event){
        System.out.println("🔥 Event received: " + event.getOrderId());

        // Later:
        // update inventory
        // send notification
    }
}
