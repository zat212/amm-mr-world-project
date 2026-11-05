package org.myassignment.dto;

import org.myassignment.entity.OptionCategory;
import java.math.BigDecimal;

public record OptionResponse(
        Long id,
        OptionCategory category,
        String name,
        BigDecimal extraPrice
) {}
