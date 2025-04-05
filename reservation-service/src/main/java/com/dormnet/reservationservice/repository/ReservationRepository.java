package com.dormnet.reservationservice.repository;

import com.dormnet.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByEmail(String email);
}
