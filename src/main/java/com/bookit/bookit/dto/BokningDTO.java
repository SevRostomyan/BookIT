package com.bookit.bookit.dto;

import com.bookit.bookit.entity.tjänst.Tjänst;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.enums.StädningsAlternativ;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BokningDTO {
    private Integer id;
    private BasicKundDTO kund;
    private StädareDTO städare;
    private StädningsAlternativ tjänst;
    private LocalDateTime bookingTime;
    private LocalDateTime endTime;
    private String adress;
    private String messageAtBooking;
    private LocalDateTime cleaningReportedTime;
    private String customerFeedback;
    private BookingStatus status;
    private CleaningReportStatus cleaningReportStatus;
}
