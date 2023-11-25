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
import com.bookit.bookit.service.tjänst.TjänstService;
import com.bookit.bookit.utils.BokningMapper;
import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BokningService {

    private final BokningRepository bokningRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final BokningMapper bokningMapper;
    private final NotificationsService notificationsService;
    private final TjänstService tjänstService;


    //Behövs ej
    /* public String getUserRoleById(Integer userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getRole().name();
        }
        return null;
    }*/

    public List<BokningDTO> getBookingsByUserId(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Bokning> bookings;
        if (UserRole.KUND == user.getRole()) {
            bookings = bokningRepository.findAllByKundId(userId);
            // Filter by desired BookingStatus for KUND
            List<BookingStatus> desiredStatusesForKund = Arrays.asList(
                    BookingStatus.PENDING,
                    BookingStatus.CONFIRMED,
                    BookingStatus.CANCELLED);
            bookings = bookings.stream()
                    .filter(b -> desiredStatusesForKund.contains(b.getBookingStatus()))
                    .collect(Collectors.toList());
        } else if (UserRole.STÄDARE == user.getRole()) {
            bookings = bokningRepository.findAllByStädareId(userId);
            // Filter by desired CleaningReportStatus for STÄDARE
            List<CleaningReportStatus> desiredStatusesForStädare = Arrays.asList(
                    CleaningReportStatus.NOT_STARTED,
                    CleaningReportStatus.IN_PROGRESS);
            bookings = bookings.stream()
                    .filter(b -> desiredStatusesForStädare.contains(b.getCleaningReportStatus()))
                    .collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No bookings found for the user");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }





    //////Nedan två metoder arbetar ihop för att KUNDEN ska kunna markera städningen som godkänd eller underkänd och
    ///// det ska skickas mejl om arbetsstatus till både admin och städaren
    /*@Transactional
    public void updateBookingStatus(Integer bookingId, BookingStatus newStatus) {
        Bokning booking = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Bokning inte funnen med id: " + bookingId));*/

    @Transactional
    public void updateBookingStatus(Integer bookingId, BookingStatus newStatus, Integer userId) {
        Bokning booking = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Bokning inte funnen med id: " + bookingId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check if the user is ADMIN or the KUND of the booking
        if (!user.getRole().equals(UserRole.ADMIN)) {
            if (!user.getRole().equals(UserRole.KUND) || !booking.getKund().getId().equals(userId)) {
                throw new SecurityException("Unauthorized access to booking status update.");
            }
        }

        // Update booking status
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

        // Send status update emails only if a cleaner is assigned
        if (booking.getStädare() != null) {
            sendStatusUpdateEmails(booking, newStatus, cleaningReportStatus);
        } else {
            // Handle the case where no cleaner is assigned, if necessary
            // For example, log this situation or send a different notification
        }
    }



    // Denna metod bör vara utanför den transaktionella kontexten
    //Den används till updateBookingStatus metoden ovan
    private void sendStatusUpdateEmails(Bokning booking, BookingStatus bookingStatus, CleaningReportStatus cleaningReportStatus) {
        // Fetch all admins
        List<UserEntity> admins = adminRepository.findAllByRole(UserRole.ADMIN);

        // Prepare email details
        String subject = "Statusuppdatering för städning ID: " + booking.getId();
        String body = "Städningen har markerats som " + bookingStatus + ". Granskningsstatusen är " + cleaningReportStatus + ".";
        StädningsAlternativ serviceType = booking.getTjänst().getStädningsAlternativ();

        // Check if a cleaner is assigned
        if (booking.getStädare() != null) {
            String cleanerEmail = booking.getStädare().getEmail();
            // Send email to cleaner
            notificationsService.sendEmail(cleanerEmail, subject, body, serviceType, booking.getStädare(), booking);
        }

        // Send email to all admins
        for (UserEntity admin : admins) {
            String adminEmail = admin.getEmail();
            notificationsService.sendEmail(adminEmail, subject, body, serviceType, admin, booking);
        }
    }



// Notera: User-parametern i sendEmail-metoden är nu en gemensam typ för både Admin och Städare.







    //////Nedan två metoder arbetar ihop för att städaren ska kunna informera kunden om att städningen är påbörjat
    @Transactional
    public void startCleaning(Integer bookingId, Integer userId) {
        Bokning booking = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check if the user is ADMIN or the assigned städare of the booking
        if (!user.getRole().equals(UserRole.ADMIN)) {
            if (!user.getRole().equals(UserRole.STÄDARE) || !booking.getStädare().getId().equals(userId)) {
                throw new SecurityException("Unauthorized access to start cleaning.");
            }
        }

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
    public void reportCleaningAsCompleted(Integer bookingId, Integer userId) {
        Bokning booking = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found with id: " + bookingId));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check if the user is ADMIN or the assigned städare of the booking
        if (!user.getRole().equals(UserRole.ADMIN)) {
            if (!user.getRole().equals(UserRole.STÄDARE) || !booking.getStädare().getId().equals(userId)) {
                throw new SecurityException("Unauthorized access to report cleaning as completed.");
            }
        }

        // Cleaner reports the cleaning as completed and ready for customer review
        booking.setCleaningReportStatus(CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        booking.setCleaningReportedTime(LocalDateTime.now());
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
    public void saveCustomerFeedback(Integer bookingId, String feedback, Integer userId) {
        Bokning bokning = bokningRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Bokning not found"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        // Check if the user is the KUND of the booking
        if (!user.getRole().equals(UserRole.KUND) || !bokning.getKund().getId().equals(userId)) {
            throw new SecurityException("Unauthorized access to provide feedback.");
        }

        bokning.setCustomerFeedback(feedback);
        bokningRepository.save(bokning);
    }



    public List<BokningDTO> fetchNotStartedBookingsByUserId(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Bokning> bookings;
        if (user.getRole().equals(UserRole.KUND)) {
            bookings = bokningRepository.findAllByKundIdAndCleaningReportStatus(userId, CleaningReportStatus.NOT_STARTED);
        } else if (user.getRole().equals(UserRole.STÄDARE)) {
            bookings = bokningRepository.findAllByStädareIdAndCleaningReportStatus(userId, CleaningReportStatus.NOT_STARTED);
        } else {
            throw new SecurityException("Unauthorized access to fetch not started bookings.");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<BokningDTO> fetchInProgressBookingsByUserId(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Bokning> bookings;
        if (user.getRole().equals(UserRole.KUND)) {
            bookings = bokningRepository.findAllByKundIdAndCleaningReportStatus(userId, CleaningReportStatus.IN_PROGRESS);
        } else if (user.getRole().equals(UserRole.STÄDARE)) {
            bookings = bokningRepository.findAllByStädareIdAndCleaningReportStatus(userId, CleaningReportStatus.IN_PROGRESS);
        } else {
            throw new SecurityException("Unauthorized access to fetch in-progress bookings.");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }


    //Returnerar en lista med bokningar som kunden har markerat som avklarade. Kan anropas av bara KUND eller STÄDARE.
    // Admin har en separat method för det i Admin controller och service
    public List<BokningDTO> fetchCompletedBookingsByUserId(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Bokning> bookings;
        if (user.getRole().equals(UserRole.KUND)) {
            bookings = bokningRepository.findAllByKundIdAndBookingStatus(userId, BookingStatus.COMPLETED);
        } else if (user.getRole().equals(UserRole.STÄDARE)) {
            bookings = bokningRepository.findAllByStädareIdAndBookingStatus(userId, BookingStatus.COMPLETED);
        } else {
            throw new SecurityException("Unauthorized access to fetch completed bookings.");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }



    //Returnerar en lista med bokningar som städaren har markerat som avklarade. Kan anropas av bara KUND eller STÄDARE.
    //Admin har en separat method för det i Admin controller och service
    public List<BokningDTO> fetchCompletedCleaningsByUserId(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        List<Bokning> bookings;
        if (user.getRole().equals(UserRole.KUND)) {
            bookings = bokningRepository.findAllByKundIdAndCleaningReportStatus(userId, CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        } else if (user.getRole().equals(UserRole.STÄDARE)) {
            bookings = bokningRepository.findAllByStädareIdAndCleaningReportStatus(userId, CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        } else {
            throw new SecurityException("Unauthorized access to fetch reported completed bookings.");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }





    public Map<YearMonth, Integer> calculateMonthlyIncomeFromCompletedBookings(Integer userId) {
        List<Bokning> completedBookings = fetchCompletedBookings(userId, BookingStatus.COMPLETED);

        return completedBookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> YearMonth.from(booking.getBookingTime()),
                        Collectors.summingInt(booking -> tjänstService.getPriceForCleaningType(booking.getTjänst().getStädningsAlternativ()))
                ));
    }

    private List<Bokning> fetchCompletedBookings(Integer userId, BookingStatus status) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        if (user.getRole().equals(UserRole.KUND)) {
            return bokningRepository.findAllByKundIdAndBookingStatus(userId, status);
        } else if (user.getRole().equals(UserRole.STÄDARE)) {
            return bokningRepository.findAllByStädareIdAndBookingStatus(userId, status);
        } else {
            throw new SecurityException("Unauthorized access to fetch bookings.");
        }
    }





}








