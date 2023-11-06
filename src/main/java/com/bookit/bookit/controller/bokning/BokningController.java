package com.bookit.bookit.controller.bokning;

import com.bookit.bookit.dto.BokningDTO;
import com.bookit.bookit.dto.BookingIdRequest;
import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.dto.UserIdRequest;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.service.bokning.BokningService;
import com.bookit.bookit.service.kund.KundService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bokning")
public class BokningController {

    private final KundService kundService;
    private final BokningService bokningService;

    public BokningController(KundService kundService, BokningService bokningService) {
        this.kundService = kundService;
        this.bokningService = bokningService;
    }

    //OBS: Servicemetoden för denna finns i KundService classen då det avser kundens bokning
    @PostMapping("/bookCleaning")
    public ResponseEntity<String> bookCleaning(@RequestBody CleaningBookingRequest request) {
        return ResponseEntity.ok(kundService.bookCleaning(request));
    }

    //Kunden eller städaren eller admin åt kund och städare kan använda nedan metod för att hämta bokningar kopplade till en specifik id
    @PostMapping("/fetchBookingsByUserId")
    public ResponseEntity<?> fetchBookingsByUserId(@RequestBody UserIdRequest request) {
        try {
            List<BokningDTO> bookings = bokningService.getBookingsByUserId(request.getUserId());
            return ResponseEntity.ok(bookings);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/cancelBooking")
    public ResponseEntity<String> cancelBooking(@RequestBody BookingIdRequest request) {
        bokningService.updateBookingStatus(request.getBookingId(), BookingStatus.CANCELLED);
        return ResponseEntity.ok("Booking cancelled successfully.");
    }




    @PostMapping("/fetchCompletedBookingsByUserId")
    public ResponseEntity<List<BokningDTO>> fetchCompletedBookingsByUserId(@RequestBody UserIdRequest request) {
        List<BokningDTO> completedBookings = bokningService.getCompletedBookingsByUserId(request.getUserId());
        if (completedBookings != null && !completedBookings.isEmpty()) {
            return ResponseEntity.ok(completedBookings);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
