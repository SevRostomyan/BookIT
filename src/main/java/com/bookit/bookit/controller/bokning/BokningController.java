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

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

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



    //Kunden eller städaren kan använda nedan metod för att hämta aktuella bokningar kopplade till deras id.
    // Admin har en annan method för att hämta bådas data
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


    //Kan användas av både städare och kund
    @PostMapping("/fetchNotStartedBookingsByUserId")
    public ResponseEntity<?> fetchNotStartedBookingsByUserId(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token after "Bearer "
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            List<BokningDTO> notStartedBookings = bokningService.fetchNotStartedBookingsByUserId(userId);
            if (notStartedBookings != null && !notStartedBookings.isEmpty()) {
                return ResponseEntity.ok(notStartedBookings);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to fetch not started bookings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/fetchInProgressBookingsByUserId")
    public ResponseEntity<?> fetchInProgressBookingsByUserId(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token after "Bearer "
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            List<BokningDTO> inProgressBookings = bokningService.fetchInProgressBookingsByUserId(userId);
            if (inProgressBookings != null && !inProgressBookings.isEmpty()) {
                return ResponseEntity.ok(inProgressBookings);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to fetch in-progress bookings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    //Från kundens perspektiv
    //Returnerar en lista med bokningar som kunden har markerat som avklarade. Kan anropas av bara KUND eller STÄDARE.
    // Admin har en separat method för det i Admin controller och service

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
    public ResponseEntity<?> fetchReportedCompletedBookingsByUserId(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token after "Bearer "
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            List<BokningDTO> reportedCompletedBookings = bokningService.fetchCompletedCleaningsByUserId(userId);
            if (reportedCompletedBookings != null && !reportedCompletedBookings.isEmpty()) {
                return ResponseEntity.ok(reportedCompletedBookings);
            } else {
                return ResponseEntity.noContent().build();
            }
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to fetch reported completed bookings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }



    //On the front end, when you open the list of completed bookings, you can also call the /calculateTotalIncome endpoint
    // ...to fetch the total income and display it alongside the list.
    @PostMapping("/calculateMonthlyIncome")
    public ResponseEntity<?> calculateMonthlyIncome(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Integer userId = jwtService.extractUserId(token);

            Map<YearMonth, Integer> monthlyIncome = bokningService.calculateMonthlyIncomeFromCompletedBookings(userId);
            return ResponseEntity.ok(monthlyIncome);
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to calculate monthly income: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }


}
