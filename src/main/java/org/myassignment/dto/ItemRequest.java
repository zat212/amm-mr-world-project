package org.myassignment.dto;

import java.math.BigDecimal;

public record ItemRequest(
        String name,
        String category,
        BigDecimal price,
        Integer stockQty
) {}
