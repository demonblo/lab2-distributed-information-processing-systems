package ru.bmstu.loyaltyapp.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.bmstu.loyaltyapp.dto.LogInfoDTO;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducer {
    @Value(value = "${spring.cloud.stream.bindings.producer-out-0.destination}")
    private String topic;

    private final StreamBridge streamBridge;

    @Async
    public void send(LogInfoDTO data) {
        try {
            streamBridge.send(topic, data);
        } catch (Exception ex) {
            log.error("[LOYALTY]: {}: Kafka producer error: {}.", data, ex.getMessage());
        }
    }
}
