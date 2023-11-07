package com.bookit.bookit.service.städare;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.städare.StädareRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import jakarta.persistence.EntityNotFoundException;
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


    //////Nedan tre metoder arbetar ihop för att tilldela städning till städare och informera städaren via mejl
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
        booking.setBookingStatus(BookingStatus.CONFIRMED);

        // Set the cleaning report status to NOT_STARTED
        booking.setCleaningReportStatus(CleaningReportStatus.NOT_STARTED);

        bokningRepository.save(booking);

        // Prepare the details for the email to be sent after the transaction
        prepareAndSendAssignmentEmail(städare, booking);

        return "Success";
    }

    // This method should be outside the transactional context
    private void prepareAndSendAssignmentEmail(Städare städare, Bokning booking) {
        // Fetch the service type from the booking
        StädningsAlternativ serviceType = booking.getTjänst().getStädningsAlternativ();

        // Prepare email details
        String email = städare.getEmail();
        String subject = "New Cleaning Task Assigned";
        String body = "You have been assigned a new cleaning task for " + booking.getBookingTime().toString();

        // Send the email
        sendAssignmentEmail(email, subject, body, serviceType, städare, booking);
    }

    // This method actually sends the email and should handle any exceptions internally
    private void sendAssignmentEmail(String email, String subject, String body, StädningsAlternativ serviceType, Städare städare, Bokning booking) {
        try {
            notificationsService.sendEmail(email, subject, body, serviceType, städare, booking);
        } catch (Exception e) {
            // Log the exception and handle it appropriately
            // This ensures that email sending failure does not affect the transaction
            // You could also implement a retry mechanism or queue the email for later retry
        }
    }



}
