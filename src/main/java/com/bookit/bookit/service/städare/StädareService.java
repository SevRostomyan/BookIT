package com.bookit.bookit.service.städare;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.städare.StädareRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
//@RequiredArgsConstructor
@AllArgsConstructor
public class StädareService {
    private final BokningRepository bokningRepository;
    private final StädareRepository städareRepository;
    private final NotificationsService notificationsService;

    @Transactional
    // StädareService.java
    public String assignCleaning(Integer bookingId, Integer städareId) {
        // Fetch the booking and cleaner details
        Bokning booking = bokningRepository.findById(bookingId).orElse(null);
        Städare städare = städareRepository.findById(städareId).orElse(null);

        if (booking == null || städare == null) {
            return "Invalid booking or cleaner ID.";
        }

        // Define a time range to check for overlapping bookings
        LocalDateTime start = booking.getBookingTime().minusMinutes(15);
        LocalDateTime end = booking.getBookingTime().plusMinutes(15);

        // Check if the cleaner is already booked within the time range
        List<Bokning> existingBookings = bokningRepository.findAllByStädareIdAndBookingTimeBetween(städareId, start, end);
        if (!existingBookings.isEmpty()) {
            return "Cleaner is already booked within this time range.";
        }

        // Assign the cleaner to the booking
        booking.setStädare(städare);

        // Set the booking status to CONFIRMED
        booking.setStatus(BookingStatus.CONFIRMED);

        bokningRepository.save(booking);

        // Fetch the service type from the booking after saving the booking
        StädningsAlternativ serviceType = booking.getTjänst().getStädningsAlternativ();

        // Send notification
        String email = städare.getEmail();
        String subject = "New Cleaning Task Assigned";
        String body = "You have been assigned a new cleaning task for " + booking.getBookingTime().toString();
        notificationsService.sendEmail(email, subject, body,serviceType, städare, booking );

        return "Success";
    }

}
