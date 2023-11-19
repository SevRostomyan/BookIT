package com.bookit.bookit.controller.bokning;

import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.BokningDTO;
import com.bookit.bookit.dto.BookingIdRequest;
import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.dto.UserIdRequest;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.service.bokning.BokningService;
import com.bookit.bookit.service.kund.KundService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/bokning")
public class BokningController {
    private static final Logger logger = LoggerFactory.getLogger(BokningController.class);

    private final KundService kundService;
    private final BokningService bokningService;
    private JwtService jwtService;

    public BokningController(KundService kundService, BokningService bokningService, JwtService jwtService) {
        this.kundService = kundService;
        this.bokningService = bokningService;
        this.jwtService = jwtService;
    }

    //OBS: Servicemetoden för denna finns i KundService klassen då det avser kundens bokning.
    @PostMapping("/bookCleaning")
    public ResponseEntity<String> bookCleaning(@RequestBody CleaningBookingRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Assuming Bearer token
            Integer userId = jwtService.extractUserId(token);
            return ResponseEntity.ok(kundService.bookCleaning(request, userId));
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to book cleaning: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }



    //Kunden eller städaren eller admin åt kund och städare kan använda nedan metod för att hämta bokningar kopplade till en specifik id
    //Endpointen tar in info via body och token via header
    @PostMapping("/fetchBookingsByUserId")
    public ResponseEntity<?> fetchBookingsByUserId(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Using Bearer token
            Integer userId = jwtService.extractUserId(token);

            List<BokningDTO> bookings = bokningService.getBookingsByUserId(userId);
            return ResponseEntity.ok(bookings);
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to fetch bookings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/cancelBooking")
    public ResponseEntity<String> cancelBooking(@RequestBody BookingIdRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            bokningService.updateBookingStatus(request.getBookingId(), BookingStatus.CANCELLED, userId);
            return ResponseEntity.ok("Booking cancelled successfully.");
        } catch (SecurityException e) {
            // Log the exception for internal monitoring
            logger.warn("Unauthorized attempt to cancel booking: " + e.getMessage());

            // Return a 403 Forbidden response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }





    //Från kundens perspektiv (alltså det är bara kunden som använder BookingStatus enumet borträknad admin)
    @PostMapping("/fetchCompletedBookingsByUserId")
    public ResponseEntity<?> fetchCompletedBookingsByUserId(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            List<BokningDTO> completedBookings = bokningService.fetchCompletedBookingsByUserId(userId);
            if (completedBookings != null && !completedBookings.isEmpty()) {
                return ResponseEntity.ok(completedBookings);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (SecurityException e) {
            // Log the exception for internal monitoring
            logger.warn("Unauthorized attempt to fetch completed bookings: " + e.getMessage());

            // Return a 403 Forbidden response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }





    //Från städarens perspektiv (alltså det är bara städaren som använder CleaningReportStatus enumet borträknad admin)
    @PostMapping("/fetchReportedCompletedBookingsByUserId")
    public ResponseEntity<List<BokningDTO>> fetchReportedCompletedBookingsByUserId(@RequestBody UserIdRequest request) {
        List<BokningDTO> reportedCompletedBookings = bokningService.fetchCompletedCleaningsByUserId(request.getUserId());
        if (reportedCompletedBookings != null && !reportedCompletedBookings.isEmpty()) {
            return ResponseEntity.ok(reportedCompletedBookings);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

}
