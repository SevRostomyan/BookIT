package com.bookit.bookit.controller.städare;

import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.CleaningStatusRequest;
import com.bookit.bookit.service.bokning.BokningService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/städare")
public class StädareController {
    private static final Logger logger = LoggerFactory.getLogger(StädareController.class);

    private BokningService bokningService;
    private JwtService jwtService;

    public StädareController(BokningService bokningService, JwtService jwtService) {
        this.bokningService = bokningService;
        this.jwtService = jwtService;
    }

    //Servicemetod för nedan finns i BokningService
    //Städaren sätter CleaningReportStatus till IN_PROGRESS
    @PutMapping("/startCleaning")
    public ResponseEntity<?> reportCleaningCleaningStarted(@RequestBody CleaningStatusRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            bokningService.startCleaning(request.getCleaningId(), userId);
            return ResponseEntity.ok("Städning påbörjad");
        } catch (SecurityException e) {
            // Log the unauthorized access attempt
            logger.warn("Unauthorized attempt to start cleaning: " + e.getMessage());

            // Return a 403 Forbidden response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }


    //Städaren sätter CleaningReportStatus till REPORTED_COMPLETED
    @PutMapping("/reportCleaningCompleted")
    public ResponseEntity<String> reportCleaningCompleted(@RequestBody CleaningStatusRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            // Call the new service method to report cleaning as completed
            bokningService.reportCleaningAsCompleted(request.getCleaningId(), userId);
            return ResponseEntity.ok("Städning rapporterad som genomförd och redo för kundens granskning.");
        } catch (SecurityException e) {
            // Log the exception for internal monitoring
            logger.warn("Unauthorized attempt to report cleaning as completed: " + e.getMessage());

            // Return a 403 Forbidden response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        } catch (EntityNotFoundException e) {
            // Handle the case where the booking is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions that may occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ett internt fel inträffade när städningen rapporterades som genomförd.");
        }
    }


}
