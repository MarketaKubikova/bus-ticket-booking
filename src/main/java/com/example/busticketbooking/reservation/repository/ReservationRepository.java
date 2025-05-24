package com.example.busticketbooking.reservation.repository;

import com.example.busticketbooking.reservation.entity.Reservation;
import com.example.busticketbooking.reservation.model.ReservationStatus;
import com.example.busticketbooking.user.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findAllByUser(AppUser user);

    List<Reservation> findAllByStatusAndCreatedAtBefore(ReservationStatus reservationStatus, LocalDateTime threshold);
}
