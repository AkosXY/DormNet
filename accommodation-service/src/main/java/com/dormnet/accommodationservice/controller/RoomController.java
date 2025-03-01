package com.dormnet.accommodationservice.controller;

import com.dormnet.accommodationservice.modell.Room;
import com.dormnet.accommodationservice.modell.dto.RoomDTO;
import com.dormnet.accommodationservice.service.RoomService;
import com.dormnet.accommodationservice.utils.DTOConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    @Autowired
    private RoomService roomService;


    @GetMapping
    public List<RoomDTO> getAllRooms() {
        return roomService.findAll().stream()
                .map(DTOConverter::convertToRoomDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(@RequestBody Room room) {
        Room savedRoom = roomService.save(room);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedRoom);
    }



}
