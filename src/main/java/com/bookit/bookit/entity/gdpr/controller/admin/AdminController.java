package com.bookit.bookit.entity.gdpr.controller.admin;


import com.bookit.bookit.dto.AssignCleaningRequest;
import com.bookit.bookit.dto.StädareDTO;
import com.bookit.bookit.service.städare.StädareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.bookit.bookit.service.admin.AdminService;
import org.springframework.http.HttpStatus;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    private final StädareService städareService;
    @Autowired
    public AdminController(AdminService adminService, StädareService städareService) {
        this.adminService = adminService;
        this.städareService = städareService;
    }



    @GetMapping("/dashboard")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAdminDashboard() {
        Map<String, Object> response = new HashMap<>();
        response.put("role", "ADMIN");

      /*  // Fetch various metrics and data from services (this is just a skeleton)
        Integer totalCustomers = customerService.getTotalCustomers();
        Integer totalCleaners = cleanerService.getTotalCleaners();
        List<Booking> upcomingBookings = bookingService.getUpcomingBookings();
        List<String> systemAlerts = alertService.getUnresolvedAlerts();

        // Add them to response
        response.put("totalCustomers", totalCustomers);
        response.put("totalCleaners", totalCleaners);
        response.put("upcomingBookings", upcomingBookings);
        response.put("systemAlerts", systemAlerts);
        // ... and so on*/

        return new ResponseEntity<>(response, HttpStatus.OK);
    }



    //Som en Admin tilldela ett städningsuppdrag till en städare
    //OBS: servicemetoden finns i StädareService
    //OBS2: Denna metod ska användas enbart av en Admin som ska hämta StädareId i frontenden och överföra till request body av denna endpoint.
    @PostMapping("/assignCleaning")
    public ResponseEntity<String> assignCleaning(@RequestBody AssignCleaningRequest request) {
        String result = städareService.assignCleaning(request.getBookingId(), request.getStädareId());
        if ("Success".equals(result)) {
            return ResponseEntity.ok("Uppdraget är tilldelat!");
        } else {
            return ResponseEntity.badRequest().body(result);
        }
    }

    //Denna ska anropas i samband med ovan metod för att kunna fetcha en lista med lediga städare under bokningens period och tilldela.
    // Se bild i kommentarerna i Jira under uppgift 61.
    @GetMapping("/available-cleaners")
    public ResponseEntity<List<StädareDTO>> getAvailableCleaners(@RequestParam("bookingTime") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime bookingTime) {
        List<StädareDTO> availableCleaners = städareService.getAvailableCleanersForTime(bookingTime);
        return ResponseEntity.ok(availableCleaners);
    }

}
