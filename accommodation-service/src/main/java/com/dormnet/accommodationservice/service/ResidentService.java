package com.dormnet.accommodationservice.service;

import com.dormnet.accommodationservice.modell.Resident;
import com.dormnet.accommodationservice.modell.Room;
import com.dormnet.accommodationservice.repository.ResidentRepository;
import com.dormnet.accommodationservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ResidentService {

    @Autowired
    private final ResidentRepository residentRepository;

    @Autowired
    private RoomRepository roomRepository;

    public Resident save(Resident resident) {
        residentRepository.save(resident);
        return resident;
    }

    public List<Resident> findAll() {
        return residentRepository.findAll();
    }


    public void unAssignResidentFromRoom(Long residentId) {
        Optional<Resident> residentOptional = residentRepository.findById(residentId);
        if (residentOptional.isPresent()) {
            Resident resident = residentOptional.get();
            Room room = resident.getRoom();
            if (room != null) {
                room.setNumOfResidents(room.getNumOfResidents() - 1);
                roomRepository.save(room);
            }
            resident.setRoom(null);
            residentRepository.save(resident);
        } else {

        }
    }

    public void assignResidentToRoom(Long residentId, Long roomId) {
        Optional<Resident> residentOptional = residentRepository.findById(residentId);
        Optional<Room> roomOptional = roomRepository.findById(roomId);

        if (residentOptional.isPresent() && roomOptional.isPresent()) {
            Resident resident = residentOptional.get();
            Room room = roomOptional.get();

            if (room.getNumOfResidents() <= room.getCapacity()) {
                resident.setRoom(room);
                residentRepository.save(resident);

                room.setNumOfResidents(room.getNumOfResidents() + 1);
                roomRepository.save(room);
            } else {
                throw new IllegalStateException("Room capacity reached. Cannot assign resident to room.");
            }
        } else {
            throw new IllegalArgumentException("Resident or Room not found.");

        }
    }



}
