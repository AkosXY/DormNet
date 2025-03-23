package com.dormnet.reservationservice.controller;

import com.dormnet.reservationservice.model.ReservationRequest;
import com.dormnet.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reserve")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> placeReservation(@RequestBody ReservationRequest reservationRequest) {
        String message = reservationService.placeReservation(reservationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }


    @PostMapping("/drop")
    @PreAuthorize("hasRole('admin')")
    @ResponseStatus(HttpStatus.CREATED)
    public void dropReservation(@RequestParam Long id) {
        reservationService.dropReservation(id);
    }

}
