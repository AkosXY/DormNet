package com.dormnet.accommodationservice.controller;

import com.dormnet.accommodationservice.modell.Resident;
import com.dormnet.accommodationservice.modell.dto.ResidentDTO;
import com.dormnet.accommodationservice.service.ResidentService;
import com.dormnet.accommodationservice.utils.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/resident")
public class ResidentController {
    @Autowired
    private ResidentService residentService;

    @GetMapping
    public List<ResidentDTO> getAllResidents() {
        List<Resident> residents = residentService.findAll();
        return residents.stream()
                .map(DTOConverter::convertToResidentDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/unassigned")
    public List<ResidentDTO> getResidentsWithoutRoom() {
        List<Resident> residents = residentService.findAll();
        return residents.stream()
                .filter(resident -> resident.getRoom() == null)
                .map(DTOConverter::convertToResidentDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Resident> createResident(@RequestBody Resident resident) {
        resident.setRoom(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(residentService.save(resident));
    }


    @PostMapping("/{id}/unassign")
    @PreAuthorize("hasRole('admin')")
    @CacheEvict(value = "rooms", allEntries = true)
    public ResponseEntity<Void> unassignResidentFromRoom(@PathVariable Long id) {
        residentService.unAssignResidentFromRoom(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping("/{residentId}/assign/{roomId}")
    @PreAuthorize("hasRole('admin')")
    @CacheEvict(value = "rooms", allEntries = true)
    public ResponseEntity<Void> assignResidentToRoom(@PathVariable Long residentId, @PathVariable Long roomId) {
        residentService.assignResidentToRoom(residentId, roomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
