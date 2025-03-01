package com.dormnet.accommodationservice.service;

import com.dormnet.accommodationservice.modell.Room;
import com.dormnet.accommodationservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<Room> findAll() {
        return roomRepository.findAll();
    }

    public Room save(Room room) {
        roomRepository.save(room);
        return room;
    }

}
