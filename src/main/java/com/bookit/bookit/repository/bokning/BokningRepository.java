package com.bookit.bookit.repository.bokning;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.enums.StädningsAlternativ;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface BokningRepository {
    void save(Bokning newBooking);

    boolean existsByKundAndTjänst_StädningsAlternativ(Kund kund, StädningsAlternativ städningsAlternativ);

    boolean existsByKundAndTjänst_StädningsAlternativAndBookingTime(
            Kund kund, StädningsAlternativ städningsAlternativ, LocalDateTime bookingTime);

}
