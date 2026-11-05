package org.myassignment.dto;

public record StockRequest(
        int delta // +10 adds stock, -3 removes stock
) {}
