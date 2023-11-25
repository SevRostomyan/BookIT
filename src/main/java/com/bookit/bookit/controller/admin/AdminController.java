package com.bookit.bookit.controller.admin;


import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.*;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.repository.user.UserRepository;
import com.bookit.bookit.service.städare.StädareService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final AdminService adminService;
    private final JwtService jwtService;
    private final StädareService städareService;
    private final AdminService bokningService;
    private final UserRepository userRepository;

    @Autowired
    public AdminController(AdminService adminService, JwtService jwtService, StädareService städareService, AdminService bokningService, UserRepository userRepository) {
        this.adminService = adminService;
        this.jwtService = jwtService;
        this.städareService = städareService;
        this.bokningService = bokningService;
        this.userRepository = userRepository;
    }



    //Behövs ej
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

   /* @GetMapping("/kunder/all")
    public ResponseEntity<?> getAllKunder(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Integer adminUserId = jwtService.extractUserId(token);

            // Verify if the user is an admin
            if (!adminService.isAdmin(adminUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
            }

            List<KundDTO> allKunder = adminService.getAllUsersByRole(UserRole.KUND);
            return ResponseEntity.ok(allKunder);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }*/


    @GetMapping("/fetchNotAssignedBookings")
    public ResponseEntity<?> fetchNotAssignedBookings(HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Using Bearer token
            Integer adminUserId = jwtService.extractUserId(token);

            // Verify if the user is an admin
            if (!adminService.isAdmin(adminUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
            }

            List<BokningDTO> notAssignedBookings = bokningService.getNotAssignedBookings();
            return ResponseEntity.ok(notAssignedBookings);
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to fetch not assigned bookings: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //Denna ska anropas i samband med ovan metod för att kunna fetcha en lista med lediga städare under bokningens period och tilldela.
    // Se bild i kommentarerna i Jira under uppgift 61.
    @GetMapping("/available-cleaners")
    public ResponseEntity<?> getAvailableCleaners(@RequestBody BookingTimeDTO bookingTimeDTO, HttpServletRequest httpRequest) {

        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            LocalDateTime bookingTime = bookingTimeDTO.getBookingTime();
            List<StädareDTO> availableCleaners = städareService.getAvailableCleanersForTime(bookingTime, userId);
            return ResponseEntity.ok(availableCleaners);
        } catch (SecurityException e) {
            // Log the unauthorized access attempt
            logger.warn("Unauthorized attempt to access available cleaners: " + e.getMessage());

            // Return a 403 Forbidden response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }


    //Som en Admin tilldela ett städningsuppdrag till en städare
    //OBS: servicemetoden finns i StädareService
    //OBS2: Denna metod ska användas enbart av en Admin som ska hämta StädareId i frontenden och överföra till request body av denna endpoint.
    @PostMapping("/assignCleaning")
    public ResponseEntity<?> assignCleaning(@RequestBody AssignCleaningRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer userId = jwtService.extractUserId(token); // Extract userId from the token

            String result = städareService.assignCleaning(request.getBookingId(), request.getStädareId(), userId);
            if ("Success".equals(result)) {
                return ResponseEntity.ok("Uppdraget är tilldelat!");
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (SecurityException e) {
            // Log the unauthorized access attempt
            logger.warn("Unauthorized attempt to assign cleaning: " + e.getMessage());

            // Return a 403 Forbidden response
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }






    //Används av Admin för att fetcha samtliga bokningar åt en kund eller städare
    //inklusive aktiva och avslutade bokningar.

    @PostMapping("/fetchBookingsForUser")
    public ResponseEntity<?> fetchBookingsForUser(@RequestBody UserIdRequest userIdRequest, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Using Bearer token
            Integer adminUserId = jwtService.extractUserId(token);

            // Verify if the user is an admin
            if (!adminService.isAdmin(adminUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
            }

            List<BokningDTO> bookings = adminService.getBookingsForUserByAdmin(userIdRequest.getUserId());
            return ResponseEntity.ok(bookings);
        } catch (SecurityException e) {
            logger.warn("Unauthorized attempt to fetch bookings for user [" + userIdRequest.getUserId() + "]: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/fetchNotStartedBookingsForUser")
    public ResponseEntity<?> fetchNotStartedBookingsForUserByAdmin(@RequestBody UserIdRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer adminUserId = jwtService.extractUserId(token); // Extract admin user ID from the token

            // Verify if the user is an admin
            if (!adminService.isAdmin(adminUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
            }

            List<BokningDTO> notStartedBookings = bokningService.fetchNotStartedBookingsForUserByAdmin(request.getUserId());
            return ResponseEntity.ok(notStartedBookings);
        } catch (SecurityException e) {
            // Log the exception for internal monitoring
            logger.warn("Unauthorized attempt by admin to fetch not started bookings for user [" + request.getUserId() + "]: " + e.getMessage());

            // Return a 403 Forbidden response with an appropriate body
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }


    @PostMapping("/fetchInProgressBookingsForUser")
    public ResponseEntity<?> fetchInProgressBookingsForUserByAdmin(@RequestBody UserIdRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer adminUserId = jwtService.extractUserId(token); // Extract admin user ID from the token

            // Verify if the user is an admin
            if (!adminService.isAdmin(adminUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
            }

            List<BokningDTO> inProgressBookings = bokningService.fetchInProgressBookingsForUserByAdmin(request.getUserId());
            return ResponseEntity.ok(inProgressBookings);
        } catch (SecurityException e) {
            // Log the exception for internal monitoring
            logger.warn("Unauthorized attempt by admin to fetch in-progress bookings for user [" + request.getUserId() + "]: " + e.getMessage());

            // Return a 403 Forbidden response with an appropriate body
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }



    //Från kundens perspektiv (alltså det är bara kunden som använder BookingStatus enumet borträknad admin)
    @PostMapping("/fetchCompletedBookingsForUserByAdmin")
    public ResponseEntity<?> fetchCompletedBookingsForUserByAdmin(@RequestBody FetchCompletedBookingsRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer adminUserId = jwtService.extractUserId(token); // Extract admin user ID from the token

            List<BokningDTO> completedBookings = bokningService.fetchCompletedBookingsForUserByAdmin(request.getTargetUserId(), adminUserId);
            return ResponseEntity.ok(completedBookings);
        } catch (SecurityException e) {
            // Log the exception for internal monitoring
            logger.warn("Unauthorized attempt by user to fetch completed bookings for user [" + request.getTargetUserId() + "]: " + e.getMessage());

            // Return a 403 Forbidden response with an appropriate body
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonList("Unauthorized access."));
        }
    }




    //Från städarens perspektiv (alltså det är bara städaren som använder CleaningReportStatus enumet borträknad admin)
    @PostMapping("/fetchReportedCompletedBookingsForUser")
    public ResponseEntity<?> fetchReportedCompletedBookingsForUserByAdmin(@RequestBody UserIdRequest request, HttpServletRequest httpRequest) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7); // Extract the token
            Integer adminUserId = jwtService.extractUserId(token); // Extract admin user ID from the token

            List<BokningDTO> reportedCompletedBookings = bokningService.fetchReportedCompletedBookingsForUserByAdmin(request.getUserId(), adminUserId);
            return ResponseEntity.ok(reportedCompletedBookings);
        } catch (SecurityException e) {
            // Log the exception for internal monitoring
            logger.warn("Unauthorized attempt by user to fetch reported completed bookings for user [" + request.getUserId() + "]: " + e.getMessage());

            // Return a 403 Forbidden response with an appropriate body
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }


    // Endpoints to search for users
    // Bellow two endpoints will be used before requesting the fetchCompletedBookingsForUserByAdmin and
    // fetchReportedCompletedBookingsForUserByAdmin endpoints above.
    // The third method bellow complements these two giving the functionality of checking JWT token- and 403 error
    // So only the first two are endpoints, not the third one.
    // These two will be used in two different cells in the front end to search for users.
    // after pressing on a user its userId needs to be saved in a state in the frontend
    // and to be sent in the request to the endpoints mentioned above

    @GetMapping("/search/kund")
    public ResponseEntity<?> searchKund(@RequestParam String query, HttpServletRequest httpRequest) {
        return searchUsersByRole(query, httpRequest, UserRole.KUND);
    }

    @GetMapping("/search/städare")
    public ResponseEntity<?> searchStädare(@RequestParam String query, HttpServletRequest httpRequest) {
        return searchUsersByRole(query, httpRequest, UserRole.STÄDARE);
    }

    private ResponseEntity<?> searchUsersByRole(String query, HttpServletRequest httpRequest, UserRole role) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Integer adminUserId = jwtService.extractUserId(token);

            // Verify if the user is an admin
            if (!adminService.isAdmin(adminUserId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
            }

            List<KundDTO> users = adminService.searchUsersByRole(query, role);
            return ResponseEntity.ok(users);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized access.");
        }
    }



    @PostMapping("/calculateCleanerMonthlyIncome")
    public ResponseEntity<?> calculateCleanerMonthlyIncome(HttpServletRequest httpRequest, @RequestBody CleanerIncomeRequest request) {
        try {
            String token = httpRequest.getHeader("Authorization").substring(7);
            Integer adminUserId = jwtService.extractUserId(token);

            // Verify if the user is an admin
            UserEntity adminUser = userRepository.findById(adminUserId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + adminUserId));
            if (!adminUser.getRole().equals(UserRole.ADMIN)) {
                throw new SecurityException("Unauthorized access - Admin privileges required.");
            }

            Map<YearMonth, Integer> monthlyIncome = bokningService.calculateCleanerMonthlyIncome(request.getCleanerId());
            return ResponseEntity.ok(monthlyIncome);
        } catch (SecurityException | EntityNotFoundException e) {
            logger.warn("Unauthorized attempt to calculate cleaner's monthly income: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }






}
