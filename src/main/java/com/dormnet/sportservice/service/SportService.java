package com.dormnet.sportservice.service;

import com.dormnet.sportservice.dto.SportEventRequest;
import com.dormnet.sportservice.dto.SportEventResponse;
import com.dormnet.sportservice.model.Entry;
import com.dormnet.sportservice.model.SportEvent;
import com.dormnet.sportservice.repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;


import java.util.List;

@Service
@RequiredArgsConstructor
public class SportService {
    private final SportRepository sportRepository;

    public SportEventResponse createSportEvent(SportEventRequest sportEventRequest) {
        SportEvent sportEvent = SportEvent.builder()
                .name(sportEventRequest.name())
                .date(sportEventRequest.date())
                .entries(sportEventRequest.entries())
                .build();
        sportRepository.save(sportEvent);
        return mapToSportEventResponse(sportEvent);
    }

    public List<SportEventResponse> getAllSportEvents() {
        return sportRepository.findAll()
                .stream()
                .map(this::mapToSportEventResponse)
                .toList();
    }


    public SportEventResponse addEntryToSportEvent(String eventId, Entry entryRequest) {
        SportEvent sportEvent = sportRepository.findById(eventId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sport event not found with ID: " + eventId));

        Entry entry = new Entry(entryRequest.getParticipantName(), entryRequest.getScore());
        sportEvent.getEntries().add(entry);
        SportEvent savedSportEvent = sportRepository.save(sportEvent);
        return mapToSportEventResponse(savedSportEvent);
    }

    private SportEventResponse mapToSportEventResponse(SportEvent sportEvent) {
        return new SportEventResponse(sportEvent.getId(), sportEvent.getName(), sportEvent.getDate(), sportEvent.getEntries());
    }
}
