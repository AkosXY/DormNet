package com.dormnet.sportservice.controller;

import com.dormnet.sportservice.dto.SportEventRequest;
import com.dormnet.sportservice.dto.SportEventResponse;
import com.dormnet.sportservice.model.Entry;
import com.dormnet.sportservice.service.SportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/sport")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public SportEventResponse createSportEvent(@RequestBody SportEventRequest sportEventRequest){
        return sportService.createSportEvent(sportEventRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<SportEventResponse> getAllSportEvents(){
        return sportService.getAllSportEvents();
    }

    @PostMapping("/{eventId}/add_entries")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public SportEventResponse addEntryToSportEvent(@PathVariable String eventId, @RequestBody Entry entryRequest) {
        return sportService.addEntryToSportEvent(eventId, entryRequest);
    }

}
