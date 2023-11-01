package com.bookit.bookit.controller.admin;

import com.bookit.bookit.service.admin.AdminService;
import com.bookit.bookit.service.städare.StädareService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/admin")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    private final StädareService städareService;
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
    @PostMapping("/assignCleaning")
    public ResponseEntity<String> assignCleaning(@RequestParam Integer bookingId, @RequestParam Integer städareId) {
        String result = städareService.assignCleaning(bookingId, städareId);
        if ("Success".equals(result)) {
            return ResponseEntity.ok("Cleaning job assigned successfully.");
        } else {
            return ResponseEntity.badRequest().body(result);
        }

    }
}
