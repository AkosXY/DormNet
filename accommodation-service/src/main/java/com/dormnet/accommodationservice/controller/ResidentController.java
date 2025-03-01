package com.dormnet.accommodationservice.controller;

import com.dormnet.accommodationservice.modell.Resident;
import com.dormnet.accommodationservice.modell.dto.ResidentDTO;
import com.dormnet.accommodationservice.service.ResidentService;
import com.dormnet.accommodationservice.utils.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Resident> createResident(@RequestBody Resident resident) {
        resident.setRoom(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(residentService.save(resident));
    }


    @PostMapping("/{id}/unassign")
    public ResponseEntity<Void> unassignResidentFromRoom(@PathVariable Long id) {
        residentService.unAssignResidentFromRoom(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @PostMapping("/{residentId}/assign/{roomId}")
    public ResponseEntity<Void> assignResidentToRoom(@PathVariable Long residentId, @PathVariable Long roomId) {
        residentService.assignResidentToRoom(residentId, roomId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }



}
