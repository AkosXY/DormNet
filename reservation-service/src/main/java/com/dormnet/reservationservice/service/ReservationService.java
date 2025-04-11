package com.dormnet.reservationservice.service;

import com.dormnet.reservationservice.client.ResourceClient;
import com.dormnet.reservationservice.model.Reservation;
import com.dormnet.reservationservice.model.ReservationRequest;
import com.dormnet.reservationservice.repository.ReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
    public List<Reservation> getReservationsByEmail(String email) {
        return reservationRepository.findByEmail(email);
    }
}
