package com.bookit.bookit.controller.bokning;

import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.service.bokning.BokningService;
import com.bookit.bookit.service.kund.KundService;
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

    //Kunden eller städaren eller admin åt dessa kan använda nedan metod för att hämta bokningar kopplade till en specifik id
    @GetMapping("/getBookingsByUserId")
    public ResponseEntity<List<Bokning>> getBookingsByUserId(@RequestParam Integer userId) {
        List<Bokning> bookings = bokningService.getBookingsByUserId(userId);
        if (bookings != null) {
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PostMapping("/cancelBooking")
    public ResponseEntity<String> cancelBooking(@RequestParam Integer bookingId) {
        bokningService.updateBookingStatus(bookingId, BookingStatus.CANCELLED);
        return ResponseEntity.ok("Booking cancelled successfully.");
    }



    @GetMapping("/getCompletedBookingsByUserId")
    public ResponseEntity<List<Bokning>> getCompletedBookingsByUserId(@RequestParam Integer userId) {
        List<Bokning> completedBookings = bokningService.getCompletedBookingsByUserId(userId);
        if (completedBookings != null) {
            return ResponseEntity.ok(completedBookings);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }



}
