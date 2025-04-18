package com.dormnet.reservationservice.repository;

import com.dormnet.reservationservice.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByEmail(String email);

    @Query("SELECT r FROM Reservation r WHERE r.email = :email AND r.startDate >= CURRENT_DATE")
    List<Reservation> findActiveByEmail(@Param("email") String email);


    @Query("SELECT r FROM Reservation r WHERE r.startDate >= CURRENT_DATE")
    List<Reservation> findAllActiveReservations();


    @Query("SELECT r FROM Reservation r WHERE r.resourceId = :resourceId AND " +
            "r.startDate < :stopDate AND r.stopDate > :startDate")
    List<Reservation> findByResourceIdAndTimeOverlap(
            @Param("resourceId") Long resourceId,
            @Param("startDate") Date startDate,
            @Param("stopDate") Date stopDate
    );

}
