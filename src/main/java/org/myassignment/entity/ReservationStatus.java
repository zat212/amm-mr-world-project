package org.myassignment.entity;

public enum ReservationStatus {

    PENDING,    // customer applied, waiting for departure confirmation
    CONFIRMED,  // departure confirmed (3+ applicants)
    CANCELLED,  // cancelled by customer or staff
    COMPLETED
}
