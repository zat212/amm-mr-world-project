package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.ReservationRequest;
import org.myassignment.entity.CustomizeOption;
import org.myassignment.entity.Departure;
import org.myassignment.entity.DepartureStatus;
import org.myassignment.entity.PackageStatus;
import org.myassignment.entity.Reservation;
import org.myassignment.entity.ReservationStatus;
import org.myassignment.entity.TravelPackage;
import org.myassignment.entity.User;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.CustomizeOptionRepository;
import org.myassignment.repository.DepartureRepository;
import org.myassignment.repository.ReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final DepartureRepository departureRepository;
    private final CustomizeOptionRepository optionRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    // ---------- Customer ----------

    /** Book / Apply for Travel Package. */
    @Transactional
    public Reservation book(Long userId, ReservationRequest request) {
        if (request == null || request.departureId() == null) {
            throw new BadRequestException("Departure is required.");
        }
        int applicants = request.applicantsCount() == null ? 1 : request.applicantsCount();
        if (applicants < 1) {
            throw new BadRequestException("Applicants must be at least 1.");
        }

        User user = userService.getById(userId);

        // Row lock: concurrent bookings on the same departure run one after another,
        // so the "3+ applicants" check never sees a stale total.
        Departure departure = departureRepository.findByIdForUpdate(request.departureId())
                .orElseThrow(() -> ResourceNotFoundException.of("Departure", request.departureId()));
        TravelPackage pkg = departure.getTravelPackage();

        if (pkg.getStatus() != PackageStatus.ACTIVE) {
            throw new BadRequestException("This travel package is not available.");
        }
        if (departure.getStatus() == DepartureStatus.CANCELLED) {
            throw new BadRequestException("This departure has been cancelled.");
        }
        if (!departure.getDepartureDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("This departure date has already passed.");
        }
        if (reservationRepository.existsByUserIdAndDepartureIdAndStatusNot(
                userId, departure.getId(), ReservationStatus.CANCELLED)) {
            throw new BadRequestException("You have already applied for this departure.");
        }

        List<CustomizeOption> options = loadOptions(request.optionIds(), pkg.getId());

        BigDecimal perPerson = options.stream()
                .map(CustomizeOption::getExtraPrice)
                .reduce(pkg.getBasePrice(), BigDecimal::add);
        BigDecimal total = perPerson.multiply(BigDecimal.valueOf(applicants));

        boolean alreadyConfirmed = departure.getStatus() == DepartureStatus.CONFIRMED;

        Reservation reservation = Reservation.builder()
                .user(user)
                .departure(departure)
                .applicantsCount(applicants)
                .totalPrice(total)
                .status(alreadyConfirmed ? ReservationStatus.CONFIRMED : ReservationStatus.PENDING)
                .build();
        options.forEach(reservation::addOption);
        reservation = reservationRepository.save(reservation);

        if (alreadyConfirmed) {
            // The trip is already going: this customer gets the confirmation email right away.
            notificationService.queue(reservation, NotificationService.DEPARTURE_CONFIRMED);
        } else {
            confirmDepartureIfEligible(departure);
        }
        return reservation;
    }

    /** View Travel History. */
    public List<Reservation> getHistory(Long userId) {
        return reservationRepository.findByUserIdOrderByReservedAtDesc(userId);
    }

    /** Customer cancels their own reservation. */
    @Transactional
    public Reservation cancel(Long reservationId, Long userId) {
        Reservation reservation = getById(reservationId);
        if (!reservation.getUser().getId().equals(userId)) {
            throw new BadRequestException("You can only cancel your own reservation.");
        }
        if (reservation.getStatus() != ReservationStatus.PENDING
                && reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new BadRequestException("This reservation can no longer be cancelled.");
        }
        if (!reservation.getDeparture().getDepartureDate().isAfter(LocalDate.now())) {
            throw new BadRequestException("The departure date has already passed.");
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservation;
    }

    // ---------- Staff: Manage Reservation Status ----------

    public Reservation getById(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Reservation", id));
    }

    public List<Reservation> getByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }

    public List<Reservation> getByDeparture(Long departureId) {
        return reservationRepository.findByDepartureId(departureId);
    }

    @Transactional
    public Reservation updateStatus(Long reservationId, ReservationStatus newStatus) {
        if (newStatus == null) {
            throw new BadRequestException("Status is required.");
        }
        Reservation reservation = getById(reservationId);
        ReservationStatus current = reservation.getStatus();
        if (current == ReservationStatus.CANCELLED || current == ReservationStatus.COMPLETED) {
            throw new BadRequestException("A " + current + " reservation cannot be changed.");
        }
        reservation.setStatus(newStatus);
        return reservation;
    }

    // ---------- internals ----------

    /**
     * Confirm Departure Status (3+ applicants): when the total number of applicants
     * reaches the minimum, the departure and all its pending reservations become
     * CONFIRMED and one email per reservation is queued.
     */
    private void confirmDepartureIfEligible(Departure departure) {
        int totalApplicants = reservationRepository.sumApplicantsByDepartureId(departure.getId());
        if (totalApplicants < departure.getMinApplicants()) {
            return;
        }
        departure.setStatus(DepartureStatus.CONFIRMED);

        for (Reservation r : reservationRepository.findByDepartureId(departure.getId())) {
            if (r.getStatus() == ReservationStatus.PENDING) {
                r.setStatus(ReservationStatus.CONFIRMED);
                notificationService.queue(r, NotificationService.DEPARTURE_CONFIRMED);
            }
        }
    }

    private List<CustomizeOption> loadOptions(List<Long> optionIds, Long packageId) {
        if (optionIds == null || optionIds.isEmpty()) {
            return List.of();
        }
        List<Long> distinctIds = optionIds.stream().distinct().toList();
        List<CustomizeOption> options = optionRepository.findAllById(distinctIds);
        if (options.size() != distinctIds.size()) {
            throw new ResourceNotFoundException("One or more selected options do not exist.");
        }
        for (CustomizeOption option : options) {
            if (!option.getTravelPackage().getId().equals(packageId)) {
                throw new BadRequestException("Option '" + option.getName() + "' does not belong to this package.");
            }
        }
        return options;
    }
}
