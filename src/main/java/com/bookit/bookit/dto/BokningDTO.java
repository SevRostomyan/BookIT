package com.bookit.bookit.dto;

import com.bookit.bookit.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BokningDTO {
    private Integer id;
    private KundDTO kund;
    private StädareDTO städare;
    private LocalDateTime bookingTime;
    private String adress;
    private String messageAtBooking;
    private String customerFeedback;
    private BookingStatus status;
}
