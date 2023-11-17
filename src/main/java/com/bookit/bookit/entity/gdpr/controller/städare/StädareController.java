package com.bookit.bookit.entity.gdpr.controller.städare;

import com.bookit.bookit.dto.CleaningStatusRequest;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.service.bokning.BokningService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/städare")
public class StädareController {

    private BokningService bokningService;

    //Servicemetod för nedan finns i BokningService
    //Städaren sätter CleaningReportStatus till IN_PROGRESS
    @PutMapping("/startCleaning")
    public ResponseEntity<String> reportCleaningCleaningStarted(@RequestBody CleaningStatusRequest request) {
        bokningService.startCleaning(request.getCleaningId());
        return ResponseEntity.ok("Städning påbörjad");
    }

    //Städaren sätter CleaningReportStatus till REPORTED_COMPLETED
    @PutMapping("/reportCleaningCompleted")
    public ResponseEntity<String> reportCleaningCompleted(@RequestBody CleaningStatusRequest request) {
        try {
            // Call the new service method to report cleaning as completed
            bokningService.reportCleaningAsCompleted(request.getCleaningId());
            return ResponseEntity.ok("Städning rapporterad som genomförd och redo för kundens granskning.");
        } catch (EntityNotFoundException e) {
            // Handle the case where the booking is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions that may occur
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ett internt fel inträffade när städningen rapporterades som genomförd.");
        }
    }


}
