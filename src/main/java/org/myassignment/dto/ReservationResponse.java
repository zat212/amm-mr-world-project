package org.myassignment.dto;
import org.myassignment.entity.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ReservationResponse(
        Long id,
        Long userId,
        String userName,
        String userEmail,
        Long packageId,
        String packageTitle,
        Long departureId,
        LocalDate departureDate,
        Integer applicantsCount,
        BigDecimal totalPrice,
        ReservationStatus status,
        LocalDateTime reservedAt,
        List<OptionResponse> options
) {}
