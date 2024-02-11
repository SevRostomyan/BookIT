package com.bookit.bookit.enums;

//Bokningsstatus allmänt och från kundens perspektiv
public enum BookingStatus {
    PENDING, // The booking is pending, waiting for the Admin to assign a cleaner
    CONFIRMED,  // The booking is confirmed, the cleaner is assigned
    CANCELLED,  // The booking is cancelled by the customer or admin
    COMPLETED,  // The booking is marked as completed by the customer
    UNDERKAND,  // The booking is marked as underkand by the customer
    NOT_PAID,  // The booking is not paid by the customer
    PAID    // The booking is paid by the customer
}
