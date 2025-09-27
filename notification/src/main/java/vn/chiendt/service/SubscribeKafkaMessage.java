package vn.chiendt.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j(topic = "SUBSCRIBE-KAFKA-MESSAGE")
public class SubscribeKafkaMessage {
    @KafkaListener(topics = "send-invoice-topic", groupId = "push-notification-group")
    public void handleEventSendNotification(String message) {
        log.info("handleEventSendNotification called, message: {}", message);

    }
}
