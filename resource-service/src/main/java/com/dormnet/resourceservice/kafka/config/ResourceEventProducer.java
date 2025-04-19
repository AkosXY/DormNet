package com.dormnet.resourceservice.kafka.config;

import com.dormnet.resourceservice.kafka.dto.ResourceUnavailableEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendResourceUnavailableEvent(Long resourceId) {
        try {
            String message = objectMapper.writeValueAsString(new ResourceUnavailableEvent(resourceId));
            kafkaTemplate.send("resource-unavailable", message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event", e);
        }
    }
}
