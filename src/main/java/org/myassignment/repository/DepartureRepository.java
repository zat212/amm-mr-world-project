package org.myassignment.repository;

import jakarta.persistence.LockModeType;
import org.myassignment.entity.Departure;
import org.myassignment.entity.DepartureStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DepartureRepository extends JpaRepository<Departure, Long> {

    List<Departure> findByTravelPackageId(Long packageId);

    List<Departure> findByTravelPackageIdAndStatus(Long packageId, DepartureStatus status);

    List<Departure> findByStatusAndDepartureDateAfter(DepartureStatus status, LocalDate date);

    // Lock the row while booking, so two customers booking at the same time
    // cannot both skip the "3+ applicants" check.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from Departure d where d.id = :id")
    Optional<Departure> findByIdForUpdate(@Param("id") Long id);
}
