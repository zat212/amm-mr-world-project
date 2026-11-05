package org.myassignment.repository;

import org.myassignment.entity.ReservationOption;
import org.myassignment.entity.ReservationOptionId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationOptionRepository extends JpaRepository<ReservationOption, ReservationOptionId> {

    @EntityGraph(attributePaths = {"option"})
    List<ReservationOption> findByReservationId(Long reservationId);
}
