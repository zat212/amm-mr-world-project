package org.myassignment.dto;

import org.myassignment.entity.ReservationStatus;

public record StatusRequest(
        ReservationStatus status
) {}
