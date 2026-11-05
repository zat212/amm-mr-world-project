package org.myassignment.controller;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.ReservationResponse;
import org.myassignment.dto.StatusRequest;
import org.myassignment.entity.ReservationStatus;
import org.myassignment.service.NotificationService;
import org.myassignment.service.ReservationService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffReservationController {

    private final ReservationService reservationService;
    private final NotificationService notificationService;
    private final ResponseMapper mapper;

    /** ?status=PENDING. Without status, every reservation is returned. */
    @GetMapping("/reservations")
    public List<ReservationResponse> list(@RequestParam(required = false) ReservationStatus status) {
        var statuses = status == null ? Arrays.asList(ReservationStatus.values()) : List.of(status);
        return statuses.stream()
                .flatMap(s -> reservationService.getByStatus(s).stream())
                .map(mapper::reservation)
                .toList();
    }

    @GetMapping("/departures/{departureId}/reservations")
    public List<ReservationResponse> byDeparture(@PathVariable Long departureId) {
        return reservationService.getByDeparture(departureId).stream().map(mapper::reservation).toList();
    }

    @PatchMapping("/reservations/{id}/status")
    public ReservationResponse updateStatus(@PathVariable Long id, @RequestBody StatusRequest request) {
        return mapper.reservation(reservationService.updateStatus(id, request.status()));
    }

    /** Puts FAILED Gmail notifications back in the queue. */
    @PostMapping("/notifications/retry-failed")
    public Map<String, Integer> retryFailedNotifications() {
        return Map.of("requeued", notificationService.retryFailed());
    }
}

