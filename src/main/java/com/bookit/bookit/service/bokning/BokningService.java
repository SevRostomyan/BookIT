package com.bookit.bookit.service.bokning;

import com.bookit.bookit.dto.BokningDTO;
import com.bookit.bookit.entity.admin.Admin;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.repository.admin.AdminRepository;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.user.UserRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import com.bookit.bookit.utils.BokningMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BokningService {

    private final BokningRepository bokningRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final BokningMapper bokningMapper;
    private final NotificationsService notificationsService;


    public String getUserRoleById(Integer userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getRole().name();
        }
        return null;
    }

    public List<BokningDTO> getBookingsByUserId(Integer userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        List<Bokning> bookings;
        if (UserRole.KUND == user.getRole()) {
            bookings = bokningRepository.findAllByKundId(userId);
        } else if (UserRole.STÄDARE == user.getRole()) {
            bookings = bokningRepository.findAllByStädareId(userId);
        } else {
            return Collections.emptyList();
        }

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No bookings found for the user");
        }

        // Filter by desired statuses
        List<BookingStatus> desiredStatuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.UNDERKAND);
        bookings = bookings.stream()
                .filter(b -> desiredStatuses.contains(b.getBookingStatus()))
                .collect(Collectors.toList());

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }



    //////Nedan två metoder arbetar ihop för att KUNDEN ska kunna markera städningen som godkänd eller underkänd och
    ///// det ska skickas mejl om arbetsstatus till både admin och städaren
    @Transactional
    public void updateBookingStatus(Integer bookingId, BookingStatus newStatus) {
        Bokning booking = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Bokning inte funnen med id: " + bookingId));

        // Uppdatera bokningens status
        booking.setBookingStatus(newStatus);

        // Bestäm motsvarande CleaningReportStatus
        CleaningReportStatus cleaningReportStatus;
        if (newStatus == BookingStatus.COMPLETED) {
            cleaningReportStatus = CleaningReportStatus.REVIEW_APPROVED;
        } else if (newStatus == BookingStatus.UNDERKAND) {
            cleaningReportStatus = CleaningReportStatus.REVIEW_FAILED;
        } else {
            // Hantera andra statusar om nödvändigt
            cleaningReportStatus = booking.getCleaningReportStatus();
        }

        // Uppdatera städrapportens status
        booking.setCleaningReportStatus(cleaningReportStatus);

        // Spara den uppdaterade bokningen
        bokningRepository.save(booking);

        // Skicka e-postmeddelanden om statusuppdatering
        sendStatusUpdateEmails(booking, newStatus, cleaningReportStatus);
    }

    // Denna metod bör vara utanför den transaktionella kontexten
    private void sendStatusUpdateEmails(Bokning booking, BookingStatus bookingStatus, CleaningReportStatus cleaningReportStatus) {
        // Hämta admin-detaljer (antag att det bara finns en admin eller att du hämtar en specifik)
        UserEntity admin = adminRepository.findAdminByEmail("sevrostomyan@gmail.com")
                .orElseThrow(() -> new RuntimeException("Admin hittades inte"));

        // Förbered detaljerna för e-postmeddelandet
        String cleanerEmail = booking.getStädare().getEmail();
        String adminEmail = admin.getEmail(); // Hämta adminens e-post från Admin-entiteten
        String subject = "Statusuppdatering för städning ID: " + booking.getId();
        String body = "Städningen har markerats som " + bookingStatus + ". Granskningsstatusen är " + cleaningReportStatus + ".";
        StädningsAlternativ serviceType = booking.getTjänst().getStädningsAlternativ(); // Antag att du har en 'getTjänst()' metod i 'Bokning'

        // Skicka e-postmeddelandet till städaren
        notificationsService.sendEmail(cleanerEmail, subject, body, serviceType, booking.getStädare(), booking);

        // Skicka e-postmeddelandet till admin
        notificationsService.sendEmail(adminEmail, subject, body, serviceType, admin, booking);
    }

// Notera: User-parametern i sendEmail-metoden är nu en gemensam typ för både Admin och Städare.







    //////Nedan två metoder arbetar ihop för att städaren ska kunna informera kunden om att städningen är påbörjat
    @Transactional
    public void startCleaning(Integer bookingId) {
        Bokning booking = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        // Update the cleaning report status to IN_PROGRESS
        booking.setCleaningReportStatus(CleaningReportStatus.IN_PROGRESS);
        bokningRepository.save(booking);

        // Send an email to the customer to notify them that the cleaning has started
        sendJobStartEmail(booking);
    }

    // This method should be outside the transactional context
    private void sendJobStartEmail(Bokning booking) {
        // Fetch the service type from the booking
        StädningsAlternativ serviceType = booking.getTjänst().getStädningsAlternativ();

        // Prepare email details
        String email = booking.getKund().getEmail();
        String subject = "Cleaning Job Has Started";
        String body = "Your cleaning job scheduled for " + booking.getBookingTime().toString() + " has started.";

        // Send the email
        try {
            notificationsService.sendEmail(email, subject, body, serviceType, booking.getStädare(), booking);
        } catch (Exception e) {
            // Log the exception and handle it appropriately
            // This ensures that email sending failure does not affect the transaction
            // You could also implement a retry mechanism or queue the email for later retry
        }
    }



    //////Nedan två metoder arbetar ihop för att städare ska kunna informera kunden om att städningen är klar och kan granskas
    @Transactional
    public void reportCleaningAsCompleted(Integer bookingId) {
        Bokning booking = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        // Cleaner reports the cleaning as completed and ready for customer review
        booking.setCleaningReportStatus(CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        bokningRepository.save(booking);

        // After saving, send an email to the customer for review
        sendReviewEmail(booking);
    }

    // This method should be outside the transactional context
    private void sendReviewEmail(Bokning booking) {
        // Fetch the service type from the booking
        StädningsAlternativ serviceType = booking.getTjänst().getStädningsAlternativ();

        // Prepare email details
        String email = booking.getKund().getEmail();
        String subject = "Städningsarbetet kan nu granskas";
        String body = "Din städning bokad för " + booking.getBookingTime().toString() + " är nu redo att granskas.";

        // Send the email
        try {
            notificationsService.sendEmail(email, subject, body, serviceType, booking.getStädare(), booking);
        } catch (Exception e) {
            // Log the exception and handle it appropriately
            // This ensures that email sending failure does not affect the transaction
            // We could also implement a retry mechanism or queue the email for later retry
        }
    }


    @Transactional
    public void saveCustomerFeedback(Integer bookingId, String feedback) {
        Bokning bokning = bokningRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException ("Bokning not found"));
        bokning.setCustomerFeedback(feedback);
        bokningRepository.save(bokning);
    }

    //Returnerar en lista med bokningar som kunden har markerat som avklarade
    public List<BokningDTO>fetchCompletedBookingsByUserId(Integer userId) {
        List<Bokning> bookings;
        String role = getUserRoleById(userId);
        if ("KUND".equals(role)) {
            bookings = bokningRepository.findAllByKundIdAndBookingStatus(userId, BookingStatus.COMPLETED);
        } else if ("STÄDARE".equals(role)) {
            bookings = bokningRepository.findAllByStädareIdAndBookingStatus(userId, BookingStatus.COMPLETED);
        } else {
            return Collections.emptyList();
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }


    //Returnerar en lista med bokningar som städaren har markerat som avklarade
    public List<BokningDTO> fetchCompletedCleaningsByUserId(Integer userId) {
        List<Bokning> bookings;
        String role = getUserRoleById(userId);
        if ("KUND".equals(role)) {
            bookings = bokningRepository.findAllByKundIdAndCleaningReportStatus(userId, CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        } else if ("STÄDARE".equals(role)) {
            bookings = bokningRepository.findAllByStädareIdAndCleaningReportStatus(userId, CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        } else {
            return Collections.emptyList();
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}





