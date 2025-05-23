package com.dormnet.reservationservice.controller;

import com.dormnet.reservationservice.model.Reservation;
import com.dormnet.reservationservice.model.ReservationRequest;
import com.dormnet.reservationservice.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> placeReservation(
            @RequestBody ReservationRequest reservationRequest,
            @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaim("email");
        String message = reservationService.placeReservation(reservationRequest, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/drop")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public void dropReservation(@RequestParam Long id) {
        reservationService.dropReservation(id);
    }


    @GetMapping("/all")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<Reservation>> getAllReservations() {
        List<Reservation> reservations = reservationService.getAllReservations();
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/allActive")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<List<Reservation>> getActiveReservations() {
        List<Reservation> activeReservations = reservationService.getActiveReservations();
        return ResponseEntity.ok(activeReservations);
    }

    @GetMapping("/reservations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reservation>> getMyReservations(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaim("email");
        List<Reservation> reservations = reservationService.getReservationsByEmail(userEmail);
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/activeReservations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Reservation>> getMyActiveReservations(@AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaim("email");
        List<Reservation> activeReservations = reservationService.getActiveReservationsByEmail(userEmail);
        return ResponseEntity.ok(activeReservations);
    }

    @GetMapping("/availability")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<LocalTime>> getAvailableSlots(
            @RequestParam Long resourceId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam int durationMinutes
    ) {
        List<LocalTime> availableSlots = reservationService.getAvailableTimeSlots(resourceId, date, durationMinutes);
        return ResponseEntity.ok(availableSlots);
    }



}
