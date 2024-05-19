package com.dormnet.reservationservice.model;

import java.util.Date;

public record ReservationRequest(
        Long id,
        String reservationNumber,
        Long resourceId,
        String email,
        Date startDate,
        Date stopDate) {
}
