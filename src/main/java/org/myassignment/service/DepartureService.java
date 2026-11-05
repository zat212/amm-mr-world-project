package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.DepartureRequest;
import org.myassignment.entity.Departure;
import org.myassignment.entity.DepartureStatus;
import org.myassignment.entity.Reservation;
import org.myassignment.entity.ReservationStatus;
import org.myassignment.entity.TravelPackage;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.DepartureRepository;
import org.myassignment.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DepartureService {

    private final DepartureRepository departureRepository;
    private final ReservationRepository reservationRepository;
    private final TravelPackageService packageService;
    private final NotificationService notificationService;

    public Departure getById(Long id) {
        return departureRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Departure", id));
    }

    public List<Departure> getByPackage(Long packageId) {
        return departureRepository.findByTravelPackageId(packageId);
    }

    /** Departures a customer can still apply for. */
    public List<Departure> getBookableByPackage(Long packageId) {
        return departureRepository.findByTravelPackageId(packageId).stream()
                .filter(d -> d.getStatus() != DepartureStatus.CANCELLED)
                .filter(d -> d.getDepartureDate().isAfter(LocalDate.now()))
                .toList();
    }

    @Transactional
    public Departure create(Long packageId, DepartureRequest request) {
        if (request == null || request.departureDate() == null) {
            throw new BadRequestException("Departure date is required.");
        }
        if (!request.departureDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("Departure date must be in the future.");
        }
        int min = request.minApplicants() == null ? 3 : request.minApplicants();
        if (min < 1) {
            throw new BadRequestException("Minimum applicants must be at least 1.");
        }

        TravelPackage pkg = packageService.getById(packageId);
        Departure departure = Departure.builder()
                .departureDate(request.departureDate())
                .minApplicants(min)
                .build();
        pkg.addDeparture(departure);
        return departureRepository.save(departure);
    }

    /** Cancels the departure, cancels its reservations and queues an email for each customer. */
    @Transactional
    public Departure cancel(Long departureId) {
        Departure departure = getById(departureId);
        if (departure.getStatus() == DepartureStatus.CANCELLED) {
            return departure;
        }
        departure.setStatus(DepartureStatus.CANCELLED);

        for (Reservation r : reservationRepository.findByDepartureId(departureId)) {
            if (r.getStatus() == ReservationStatus.PENDING || r.getStatus() == ReservationStatus.CONFIRMED) {
                r.setStatus(ReservationStatus.CANCELLED);
                notificationService.queue(r, NotificationService.DEPARTURE_CANCELLED);
            }
        }
        return departure;
    }
}
