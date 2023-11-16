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
    //private Integer userId;  //Extraheras från JWT token istället för body, därmed kan det kommenteras bort i DTOn.
    private StädningsAlternativ städningsAlternativ;
    // Represents the start time of the booking. Each booking is for a fixed 2-hour duration which is sett in the bookingService.
    private LocalDateTime bookingTime;
    private String adress;
    private String messageAtBooking;
}
