package vn.chiendt.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import vn.chiendt.common.OrderStatus;
import vn.chiendt.model.Order;

import java.io.IOException;


@Component
@RequiredArgsConstructor
@Slf4j(topic = "TRANSACTION-LISTENER")
public class SubcribeKafkaMessage {

    private final OrderService orderService;
    private final KafkaTemplate<String ,String> kafkaTemplate;

    @KafkaListener(topics = "update-order-status-topic", groupId = "update-order-status-group")
    public void handleEventChangeOrderStatus(String message) throws IOException {
        log.info("Checkout order topic: {}", message);

        OrderMessage orderMessage = new ObjectMapper().readValue(message,OrderMessage.class);

        // change order status=PAID
        orderService.changeOrderStatus(orderMessage.getOrderId(), orderMessage.getStatus());

        // Synchronize inventory
        Order order = orderService.getOrderDetail(orderMessage.getOrderId());
        kafkaTemplate.send("update-inventory-service", new ObjectMapper().writeValueAsString(order));
        log.info("Send order to inventory-service successfully");
    }

    @Getter
    @Setter
    private static class OrderMessage {
        private String orderId;
        private OrderStatus status;
    }
}
