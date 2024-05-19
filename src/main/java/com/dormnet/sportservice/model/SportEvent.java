package com.dormnet.sportservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document(value = "sport_event")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class SportEvent {

    @Id
    private String id;
    private String name;
    private String date;
    private List<Entry> entries;


}
