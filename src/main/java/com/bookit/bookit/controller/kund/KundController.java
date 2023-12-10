package com.bookit.bookit.controller.kund;

import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.CleaningStatusRequest;
import com.bookit.bookit.dto.FeedbackRequest;
import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.service.bokning.BokningService;
import com.bookit.bookit.service.faktura.FakturaService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kund")
public class KundController {
    private static final Logger logger = LoggerFactory.getLogger(KundController.class);

    private final BokningService bokningService;
    private final JwtService jwtService;
    private final FakturaService fakturaService;

    public KundController(BokningService bokningService, JwtService jwtService, FakturaService fakturaService) {
        this.bokningService = bokningService;
        this.jwtService = jwtService;
        this.fakturaService = fakturaService;
    }



    @PutMapping("/updateCleaningStatustoGodkand")
    public ResponseEntity<String> updateCleaningStatusToGodkand(@RequestBody CleaningStatusRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.COMPLETED, userId);
            return ResponseEntity.ok("Städning godkänd");
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to update cleaning status to Godkänd: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }




    @PutMapping("/updateCleaningStatusToUnderkand")
    public ResponseEntity<String> updateCleaningStatusToUnderkand(@RequestBody CleaningStatusRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            bokningService.updateBookingStatus(request.getCleaningId(), BookingStatus.UNDERKAND, userId);
            return ResponseEntity.ok("Städning underkänd");
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to update cleaning status to Underkänd: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }




    @PostMapping("/saveCustomerFeedback")
    public ResponseEntity<String> saveCustomerFeedback(@RequestBody FeedbackRequest feedbackRequest, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            bokningService.saveCustomerFeedback(feedbackRequest.getBookingId(), feedbackRequest.getFeedback(), userId);
            return ResponseEntity.ok("Tack för din feedback!");
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to save customer feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }


    @PostMapping("/customer/invoices")
    public ResponseEntity<?> getCustomerInvoices(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Integer customerId = jwtService.extractUserId(token);

            // Fetch invoices for the logged-in customer
            List<Faktura> invoices = fakturaService.getInvoicesForCustomer(customerId);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error retrieving invoices.");
        }
    }

    //The above method can be used together with the downloadInvoice endpoint from the FakturaController for fetching
    // the InvoiceId and downloading the PDF file that is stored on the local machine

}
