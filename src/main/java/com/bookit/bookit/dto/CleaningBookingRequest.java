package com.bookit.bookit.dto;

import com.bookit.bookit.enums.StädningsAlternativ;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CleaningBookingRequest {
    private Integer userId;
    private StädningsAlternativ städningsAlternativ;
    private LocalDateTime bookingTime;
    private String adress;
    private String messageAtBooking;
}
