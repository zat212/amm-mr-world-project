package org.myassignment.dto;

import org.myassignment.entity.OptionCategory;

import java.math.BigDecimal;

public record OptionRequest(
        OptionCategory category,
        String name,
        BigDecimal extraPrice   // optional, defaults to 0
) {}
