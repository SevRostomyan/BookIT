package com.bookit.bookit.controller.bokning;

import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.entity.bokning.Bokning;
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

    @GetMapping("/getBookingsByRole")
    public ResponseEntity<List<Bokning>> getBookingsByRole(@RequestParam String role, @RequestParam Integer userId) {
        List<Bokning> bookings = bokningService.getBookingsByRole(role, userId);
        if (bookings != null) {
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


}
