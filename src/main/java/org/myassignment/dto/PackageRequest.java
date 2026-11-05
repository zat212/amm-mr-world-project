package org.myassignment.dto;

import org.myassignment.entity.PackageStatus;

import java.math.BigDecimal;

public record PackageRequest(
        Long themeId,
        Long tierId,
        String title,
        String description,
        BigDecimal basePrice,
        Integer durationDays,
        PackageStatus status   // optional, defaults to ACTIVE on create
) {}
