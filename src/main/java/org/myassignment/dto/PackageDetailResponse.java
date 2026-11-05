package org.myassignment.dto;
import org.myassignment.entity.PackageStatus;

import java.math.BigDecimal;
import java.util.List;

public record PackageDetailResponse(
        Long id,
        String title,
        String description,
        BigDecimal basePrice,
        Integer durationDays,
        PackageStatus status,
        Long themeId,
        String themeName,
        Long tierId,
        String tierName,
        List<OptionResponse> options,
        List<DepartureResponse> departures,
        List<PackageItemResponse> items
) {}
