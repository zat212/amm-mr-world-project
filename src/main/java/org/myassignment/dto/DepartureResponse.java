package org.myassignment.dto;

import org.myassignment.entity.DepartureStatus;
import java.time.LocalDate;

public record DepartureResponse(
        Long id,
        Long packageId,
        LocalDate departureDate,
        Integer minApplicants,
        DepartureStatus status
        // current total, so the UI can show "2 / 3 needed"
) {}
