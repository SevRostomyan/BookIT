package com.bookit.bookit.service.kund;
import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.tjänst.Tjänst;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import com.bookit.bookit.repository.tjänst.TjänstRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


@Service
//@RequiredArgsConstructor
@AllArgsConstructor
public class KundService {
    private final BokningRepository bokningRepository;
    private final KundRepository kundRepository;
    private final TjänstRepository tjänstRepository;

    @Transactional
    public String bookCleaning(CleaningBookingRequest request, Integer userId) {

        // Fetch the customer based on userId
        Kund kund = kundRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Kund not found"));

        // Align booking time to the nearest 2-hour slot
        LocalDateTime bookingTime = request.getBookingTime().truncatedTo(ChronoUnit.HOURS);
        if (bookingTime.getHour() % 2 != 0) {
            bookingTime = bookingTime.plusHours(1); // Adjust to the next even hour
        }
        LocalDateTime endTime = bookingTime.plusHours(2); // 2-hour duration

        // Check for duplicate bookings based on time
        if (bokningRepository.existsByKundAndTjänst_StädningsAlternativAndBookingTimeAndEndTime(
                kund, request.getStädningsAlternativ(), bookingTime, endTime)) {
            return "You have already booked this cleaning service at this time.";
        }

        // Create a new Tjänst for each booking
        Tjänst tjänst = new Tjänst();
        tjänst.setStädningsAlternativ(request.getStädningsAlternativ());
        tjänstRepository.save(tjänst); // Save the Tjänst entity

        // Save booking information to the database
        Bokning newBooking = new Bokning();
        newBooking.setKund(kund);
        newBooking.setTjänst(tjänst);
        newBooking.setBookingTime(bookingTime); // Use the aligned bookingTime
        newBooking.setEndTime(endTime); // Set the endTime
        newBooking.setAdress(request.getAdress());
        newBooking.setMessageAtBooking(request.getMessageAtBooking());
        newBooking.setBookingStatus(BookingStatus.PENDING);

        bokningRepository.save(newBooking);

        return "Booking successful.";
    }



}
