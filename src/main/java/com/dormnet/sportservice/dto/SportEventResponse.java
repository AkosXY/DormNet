package com.dormnet.sportservice.dto;

import com.dormnet.sportservice.model.Entry;
import java.util.List;

public record SportEventResponse (String id, String name, String date, List<Entry> entries){
}
