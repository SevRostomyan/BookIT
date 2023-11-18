package com.bookit.bookit.controller.kund;

import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.CleaningStatusRequest;
import com.bookit.bookit.dto.FeedbackRequest;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.service.bokning.BokningService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kund")
public class KundController {

    private final BokningService bokningService;
    private JwtService jwtService;

    public KundController(BokningService bokningService, JwtService jwtService) {
        this.bokningService = bokningService;
        this.jwtService = jwtService;
    }

    //Endpoint-en kan användas för att markera städningen som godkänd/avklarad enligt
    /*@PutMapping("/updateCleaningStatustoGodkand")
    public ResponseEntity<String> updateCleaningStatusToGodkand(@RequestBody CleaningStatusRequest request) {
        bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.COMPLETED);
        return ResponseEntity.ok("Städning godkänd");
    }*/

    @PutMapping("/updateCleaningStatustoGodkand")
    public ResponseEntity<String> updateCleaningStatusToGodkand(@RequestBody CleaningStatusRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
        Integer userId = jwtService.extractUserId(token); // Extract userId from the token

        bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.COMPLETED, userId);
        return ResponseEntity.ok("Städning godkänd");
    }


    /*@PutMapping("/updateCleaningStatusToUnderkand")
    public ResponseEntity<String> updateCleaningStatusToUnderkand(@RequestBody CleaningStatusRequest request) {
        bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.UNDERKAND);
        return ResponseEntity.ok("Städning underkänd");
    }*/
    @PutMapping("/updateCleaningStatusToUnderkand")
    public ResponseEntity<String> updateCleaningStatusToUnderkand(@RequestBody CleaningStatusRequest request, HttpServletRequest httpRequest) {
        String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
        Integer userId = jwtService.extractUserId(token); // Extract userId from the token

        bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.UNDERKAND, userId);
        return ResponseEntity.ok("Städning underkänd");
    }



    @PostMapping("/saveCustomerFeedback")
    public ResponseEntity<String> saveCustomerFeedback(@RequestBody FeedbackRequest feedbackRequest) {
        bokningService.saveCustomerFeedback(feedbackRequest.getBookingId(), feedbackRequest.getFeedback());
        return ResponseEntity.ok("Tack för din feedback! ");
    }

}
