package org.myassignment.dto;

public record PackageItemResponse(
        Long itemId,
        String name,
        Integer quantity
) {}
