package com.bookit.bookit.controller.kund;

import com.bookit.bookit.dto.CleaningStatusRequest;
import com.bookit.bookit.dto.FeedbackRequest;
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
    @PutMapping("/updateCleaningStatustoGodkand")
    public ResponseEntity<String> updateCleaningStatusToGodkand(@RequestBody CleaningStatusRequest request) {
        bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.COMPLETED);
        return ResponseEntity.ok("Städning godkänd");
    }

    @PutMapping("/updateCleaningStatusToUnderkand")
    public ResponseEntity<String> updateCleaningStatusToUnderkand(@RequestBody CleaningStatusRequest request) {
        bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.UNDERKAND);
        return ResponseEntity.ok("Städning underkänd");
    }


    @PostMapping("/saveCustomerFeedback")
    public ResponseEntity<String> saveCustomerFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        bokningService.saveCustomerFeedback(feedbackRequest.getBookingId(), feedbackRequest.getFeedback());
        return ResponseEntity.ok("Tack för din feedback! ");
    }

}
