package org.myassignment.dto;

import java.math.BigDecimal;

public record ItemResponse(
        Long id,
        String name,
        String category,
        BigDecimal price,
        Integer stockQty
) {}
