package org.myassignment.controller;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.ReservationRequest;
import org.myassignment.dto.ReservationResponse;
import org.myassignment.service.ReservationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final CurrentUserResolver currentUser;
    private final ResponseMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse book(Authentication auth, @RequestBody ReservationRequest request) {
        var user = currentUser.get(auth);
        return mapper.reservation(reservationService.book(user.getId(), request));
    }

    @GetMapping("/history")
    public List<ReservationResponse> history(Authentication auth) {
        var user = currentUser.get(auth);
        return reservationService.getHistory(user.getId()).stream().map(mapper::reservation).toList();
    }

    @PatchMapping("/{id}/cancel")
    public ReservationResponse cancel(Authentication auth, @PathVariable Long id) {
        var user = currentUser.get(auth);
        return mapper.reservation(reservationService.cancel(id, user.getId()));
    }
}

