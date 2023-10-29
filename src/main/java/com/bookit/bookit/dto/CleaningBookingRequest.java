package com.bookit.bookit.dto;

import com.bookit.bookit.enums.StädningsAlternativ;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CleaningBookingRequest {
    private Integer userId;
    private StädningsAlternativ städningsAlternativ;
    private LocalDateTime bookingTime;
    private String messageAtBooking;
}
