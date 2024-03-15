package com.bookit.bookit.entity.bokning;

import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.tjänst.Tjänst;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bokning {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "kund_id")
    @JsonBackReference
    private Kund kund;

    @ManyToOne
    @JoinColumn(name = "städare_id")
    @JsonBackReference
    private Städare städare;

    @ManyToOne
    @JoinColumn(name = "tjänst_id")
    @JsonBackReference
    private Tjänst tjänst;

    private LocalDateTime bookingTime;
    private LocalDateTime endTime;
    private LocalDateTime cleaningReportedTime;

    private String adress;

    private String messageAtBooking;

    private String customerFeedback;

    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @Enumerated(EnumType.STRING)
    private CleaningReportStatus cleaningReportStatus; // New field for cleaner's report status
}
