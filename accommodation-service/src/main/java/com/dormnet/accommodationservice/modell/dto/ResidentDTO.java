package com.dormnet.accommodationservice.modell.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class ResidentDTO implements Serializable {
    private Long id;
    private String name;
}
