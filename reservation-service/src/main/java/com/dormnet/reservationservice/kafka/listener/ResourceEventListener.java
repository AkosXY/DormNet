package com.dormnet.reservationservice.kafka.listener;

import com.dormnet.reservationservice.kafka.dto.ResourceUnavailableEvent;
import com.dormnet.reservationservice.repository.ReservationRepository;
import com.dormnet.reservationservice.service.ReservationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ResourceEventListener {

    private final ReservationService reservationService;

    @KafkaListener(topics = "resource-unavailable", groupId = "reservation-group")
    public void listen(@Payload ResourceUnavailableEvent event) {
        reservationService.deleteReservationsByResourceId(event.resourceId());
    }
}
