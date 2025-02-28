package com.dormnet.accommodationservice.modell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RoomDTO {
    private Long id;
    private int capacity;
    private int numOfResidents;
    private List<ResidentDTO> residents;
}
