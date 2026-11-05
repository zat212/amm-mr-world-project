package org.myassignment.dto;

import java.time.LocalDate;

public record DepartureRequest(
        LocalDate departureDate,
        Integer minApplicants   // optional, defaults to 3
) {}
