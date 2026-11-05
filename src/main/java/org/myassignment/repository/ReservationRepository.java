package org.myassignment.repository;

import org.myassignment.entity.Reservation;
import org.myassignment.entity.ReservationStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // View Travel History (departure + package loaded together)
    @EntityGraph(attributePaths = {"departure", "departure.travelPackage"})
    List<Reservation> findByUserIdOrderByReservedAtDesc(Long userId);

    // Manage Reservation Status (staff)
    @EntityGraph(attributePaths = {"user", "departure", "departure.travelPackage"})
    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByDepartureId(Long departureId);

    // Used for "Confirm Departure Status (3+ Applicants)"
    @Query("""
           select coalesce(sum(r.applicantsCount), 0)
           from Reservation r
           where r.departure.id = :departureId
             and r.status <> org.myassignment.entity.ReservationStatus.CANCELLED
           """)
    int sumApplicantsByDepartureId(@Param("departureId") Long departureId);

    boolean existsByUserIdAndDepartureIdAndStatusNot(Long userId, Long departureId, ReservationStatus status);
}
