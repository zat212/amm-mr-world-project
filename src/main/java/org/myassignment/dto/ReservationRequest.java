package org.myassignment.dto;

import java.util.List;

public record ReservationRequest(
        Long departureId,
        Integer applicantsCount,   // optional, defaults to 1
        List<Long> optionIds       // chosen hotel / transportation / dining options
) {}
