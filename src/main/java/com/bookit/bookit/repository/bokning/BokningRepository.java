package com.bookit.bookit.repository.bokning;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.StädningsAlternativ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BokningRepository extends JpaRepository<Bokning, Integer> {
   // void save(Bokning newBooking);

    boolean existsByKundAndTjänst_StädningsAlternativ(Kund kund, StädningsAlternativ städningsAlternativ);

    boolean existsByKundAndTjänst_StädningsAlternativAndBookingTime(
            Kund kund, StädningsAlternativ städningsAlternativ, LocalDateTime bookingTime);

    List<Bokning> findAllByKundId(Integer kundId);

    List<Bokning> findAllByStädareId(Integer städareId);


    @Modifying
    @Query("UPDATE Bokning b SET b.status = :status WHERE b.id = :id")
    void updateBookingStatus(@Param("id") Integer id, @Param("status") BookingStatus status);

    List<Bokning> findAllByKundIdAndStatus(Integer kundId, BookingStatus status);
    List<Bokning> findAllByStädareIdAndStatus(Integer städareId, BookingStatus status);

    Optional<Bokning> findById(Integer id);

    List<Bokning> findAllByStädareIdAndBookingTimeBetween(Integer städareId, LocalDateTime start, LocalDateTime end);
}
