package org.myassignment.dto;

import org.myassignment.entity.PackageStatus;
import java.math.BigDecimal;

public record PackageSummaryResponse(
        Long id,
        String title,
        String description,
        BigDecimal basePrice,
        Integer durationDays,
        PackageStatus status,
        Long themeId,
        String themeName,
        Long tierId,
        String tierName
) {}
