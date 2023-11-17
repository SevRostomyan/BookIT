package com.bookit.bookit.service.städare;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.dto.StädareDTO;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.städare.StädareRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import com.bookit.bookit.utils.BokningMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
//@RequiredArgsConstructor
@AllArgsConstructor
public class StädareService {
    private final BokningRepository bokningRepository;
    private final StädareRepository städareRepository;
    private final NotificationsService notificationsService;
    private final BokningMapper bokningMapper;

    //Dena ska användas med nedan metod för att fetcha en lista av tillgängliga städare för att kunna assigna till bokningar.
    public List<StädareDTO> getAvailableCleanersForTime(LocalDateTime bookingTime) {
        LocalDateTime startTime = bookingTime.truncatedTo(ChronoUnit.HOURS);
        if (startTime.getHour() % 2 != 0) {
            startTime = startTime.plusHours(1); // Adjust to the next even hour
        }
        LocalDateTime endTime = startTime.plusHours(2); // 2-hour slot

        List<Städare> availableCleaners = städareRepository.findAvailableCleaners(startTime, endTime);
        return availableCleaners.stream()
                .map(bokningMapper::mapToStädareDTO) // Convert entities to DTOs
                .collect(Collectors.toList());
    }


    //////Nedan tre metoder arbetar ihop för att tilldela städning till städare och informera städaren via mejl

    // StädareService.java
    @Transactional
    public String assignCleaning(Integer bookingId, Integer städareId) {
        // Fetch the booking and cleaner details
        Bokning booking = bokningRepository.findById(bookingId).orElse(null);
        Städare städare = städareRepository.findById(städareId).orElse(null);

        if (booking == null || städare == null) {
            return "Invalid booking or cleaner ID.";
        }

        // Use the booking's time slot for checking overlapping bookings
        LocalDateTime bookingTime = booking.getBookingTime();
        LocalDateTime endTime = booking.getEndTime(); // Use the endTime from the booking

        // Check if the cleaner is already booked within the time slot
        List<Bokning> existingBookings = bokningRepository.findAllByStädareIdAndBookingTimeLessThanEqualAndEndTimeGreaterThanEqual(
                städareId, bookingTime, endTime);
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

        // Prepare and send the assignment email
        prepareAndSendAssignmentEmail(städare, booking);

        return "Success";
    }


    // This method should be outside the transactional context
    private void prepareAndSendAssignmentEmail(Städare städare, Bokning booking) {
        // Fetch the service type from the booking
        StädningsAlternativ serviceType = booking.getTjänst().getStädningsAlternativ();

        // Calculate the end time based on the 2-hour slot
        LocalDateTime startTime = booking.getBookingTime();
        LocalDateTime endTime = startTime.plusHours(2); // Assuming a 2-hour slot

        // Prepare email details
        String email = städare.getEmail();
        String subject = "New Cleaning Task Assigned";
        String body = "You have been assigned a new cleaning task. Time slot: "
                + startTime.toString() + " to " + endTime.toString();

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
