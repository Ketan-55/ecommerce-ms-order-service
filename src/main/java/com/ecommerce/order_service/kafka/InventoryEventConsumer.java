package com.ecommerce.order_service.kafka;

import com.ecommerce.order_service.entity.Order;
import com.ecommerce.order_service.event.InventoryEvent;
import com.ecommerce.order_service.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryEventConsumer {

    @Autowired
    private OrderRepository orderRepository;

    @KafkaListener(topics = "inventory-topic", groupId = "order-group")
    public void consume(InventoryEvent event) {

        System.out.println("📥 Inventory event received");

        Order order = orderRepository
                .findById(event.getOrderId())
                .orElse(null);

        if (order == null) {

            System.out.println("❌ Order not found");

            return;
        }

        if ("SUCCESS".equals(event.getStatus())) {

            order.setStatus("CONFIRMED");

        } else {

            order.setStatus("FAILED");
        }

        orderRepository.save(order);

        System.out.println("✅ Order status updated to: "
                + order.getStatus());
    }
}
