package com.bookit.bookit.controller.kund;

import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.service.bokning.BokningService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kund")
public class KundController {

    private final BokningService bokningService;

    public KundController(BokningService bokningService) {
        this.bokningService = bokningService;
    }

    //Endpoint-en kan användas för att markera städningen som godkänd/avklarad enligt
    @PutMapping("/updateCleaningStatus")
    public ResponseEntity<String> updateCleaningStatusToGodkand(@RequestParam Integer cleaningId) {
        bokningService.updateBookingStatus(cleaningId, BookingStatus.COMPLETED);
        return ResponseEntity.ok("Städning godkänd");
    }

    @PutMapping("/updateCleaningStatusToUnderkand")
    public ResponseEntity<String> updateCleaningStatusToUnderkand(@RequestParam Integer cleaningId) {
        bokningService.updateBookingStatus(cleaningId, BookingStatus.UNDERKAND);  // Assuming UNDERKAND is an enum value
        return ResponseEntity.ok("Städning underkänd");
    }

    @PostMapping("/saveCustomerFeedback")
    public ResponseEntity<String> saveCustomerFeedback(@RequestParam Integer bookingId, @RequestParam String feedback) {
        bokningService.saveCustomerFeedback(bookingId, feedback);
        return ResponseEntity.ok("Feedback saved successfully.");
    }


}
