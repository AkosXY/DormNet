package com.dormnet.reservationservice.service;

import com.dormnet.reservationservice.client.ResourceClient;
import com.dormnet.reservationservice.model.Reservation;
import com.dormnet.reservationservice.model.ReservationRequest;
import com.dormnet.reservationservice.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    private final ResourceClient resourceClient;

    @Transactional
    public String placeReservation(ReservationRequest reservationRequest, String email) {
        boolean isAvailable = resourceClient.isAvailable(reservationRequest.resourceId());

        if (!isAvailable) {
            return "Resource with ID " + reservationRequest.resourceId() + " is not available for rental.";
        }

        List<Reservation> overlappingReservations = reservationRepository
                .findByResourceIdAndTimeOverlap(
                        reservationRequest.resourceId(),
                        reservationRequest.startDate(),
                        reservationRequest.stopDate()
                );

        if (!overlappingReservations.isEmpty()) {
            return "Resource is already reserved during the requested time.";
        }

        Reservation reservation = new Reservation();
        reservation.setReservationNumber(UUID.randomUUID().toString());
        reservation.setResourceId(reservationRequest.resourceId());
        reservation.setResourceName(reservationRequest.resourceName());
        reservation.setEmail(email);
        reservation.setStartDate(reservationRequest.startDate());
        reservation.setStopDate(reservationRequest.stopDate());
        reservationRepository.save(reservation);

        return "Reservation placed. Reservation number: " + reservation.getReservationNumber();
    }


    @Transactional
    public void dropReservation(Long reservationId) {
        Optional<Reservation> optionalReservation = reservationRepository.findById(reservationId);

        if (optionalReservation.isPresent()) {
            reservationRepository.delete(optionalReservation.get());
        } else {
            throw new EntityNotFoundException("Reservation with ID " + reservationId + " not found");
        }
    }

    @Transactional(readOnly = true)
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Reservation> getActiveReservations() {
        return reservationRepository.findAllActiveReservations();
    }


    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<Reservation> getActiveReservationsByEmail(String email) {
        return reservationRepository.findActiveByEmail(email);
    }


    @Transactional(readOnly = true)
    public List<LocalTime> getAvailableTimeSlots(Long resourceId, LocalDate date, int durationMinutes) {
        LocalDateTime dayStart = date.atTime(8, 0);
        LocalDateTime dayEnd = date.atTime(20, 0);

        List<Reservation> reservations = reservationRepository.findByResourceIdAndTimeOverlap(
                resourceId,
                Timestamp.valueOf(dayStart),
                Timestamp.valueOf(dayEnd)
        );

        reservations.sort(Comparator.comparing(Reservation::getStartDate));

        List<LocalTime> freeSlots = new ArrayList<>();

        LocalDateTime current = dayStart;

        for (Reservation res : reservations) {
            LocalDateTime reservedFrom = res.getStartDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            LocalDateTime reservedTo = res.getStopDate().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            while (current.plusMinutes(durationMinutes).isBefore(reservedFrom)
                    || current.plusMinutes(durationMinutes).isEqual(reservedFrom)) {
                freeSlots.add(current.toLocalTime());
                current = current.plusMinutes(15);
            }

            current = reservedTo.isAfter(current) ? reservedTo : current;
        }

        while (current.plusMinutes(durationMinutes).isBefore(dayEnd) || current.plusMinutes(durationMinutes).isEqual(dayEnd)) {
            freeSlots.add(current.toLocalTime());
            current = current.plusMinutes(15);
        }

        return freeSlots;
    }

    @Transactional
    public void deleteReservationsByResourceId(Long resourceId) {
        List<Reservation> reservations = reservationRepository.findByResourceId(resourceId);
        reservationRepository.deleteAll(reservations);
    }



}
