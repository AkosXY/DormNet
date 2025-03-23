package com.dormnet.accommodationservice.controller;

import com.dormnet.accommodationservice.modell.Room;
import com.dormnet.accommodationservice.modell.dto.RoomDTO;
import com.dormnet.accommodationservice.service.RoomService;
import com.dormnet.accommodationservice.utils.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;


    @GetMapping
    @Cacheable(value = "rooms", key = "'allRooms'")
    public List<RoomDTO> getAllRooms() {
        return roomService.findAll().stream()
                .map(DTOConverter::convertToRoomDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    @CacheEvict(value = "rooms", allEntries = true)
    public ResponseEntity<RoomDTO> createRoom(@RequestBody Room room) {
        if (room.getResidents() == null) {
            room.setResidents(new ArrayList<>());
        }
        Room savedRoom = roomService.save(room);
        RoomDTO responseDTO = DTOConverter.convertToRoomDTO(savedRoom);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }



}
