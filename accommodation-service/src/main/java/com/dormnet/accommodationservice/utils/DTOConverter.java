package com.dormnet.accommodationservice.utils;

import com.dormnet.accommodationservice.modell.Resident;
import com.dormnet.accommodationservice.modell.Room;
import com.dormnet.accommodationservice.modell.dto.ResidentDTO;
import com.dormnet.accommodationservice.modell.dto.RoomDTO;

import java.util.List;
import java.util.stream.Collectors;

public class DTOConverter {

    public static RoomDTO convertToRoomDTO(Room room) {
        List<ResidentDTO> residentDTOs = room.getResidents().stream()
                .map(DTOConverter::convertToResidentDTO)
                .collect(Collectors.toList());
        return new RoomDTO(room.getId(),room.getNumber(), room.getCapacity(), room.getResidents().size(), residentDTOs);
    }

    public static ResidentDTO convertToResidentDTO(Resident resident) {
        return new ResidentDTO(resident.getId(), resident.getName(), resident.getUsername(), resident.getEmail(), resident.getPhone());
    }
}
