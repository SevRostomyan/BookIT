package com.bookit.bookit.service.admin;


import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.*;
import com.bookit.bookit.entity.admin.Admin;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.repository.admin.AdminRepository;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import com.bookit.bookit.repository.städare.StädareRepository;
import com.bookit.bookit.repository.user.UserRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import com.bookit.bookit.service.tjänst.TjänstService;
import com.bookit.bookit.utils.BokningMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.antlr.v4.runtime.misc.LogManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.bookit.bookit.exception.UserAlreadyExistsException;
import java.time.YearMonth;
import com.bookit.bookit.exception.UserNotFoundException;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BokningRepository bokningRepository;
    private final BokningMapper bokningMapper;
    private final TjänstService tjanstService;
    private final KundRepository kundRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationsService notificationsService;
    private final JwtService jwtService;
    private final StädareRepository städareRepository;
    private final AdminRepository adminRepository;


    public List<BokningDTO> getBookingsForUserByAdmin(Integer targetUserId) {
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found"));

        List<Bokning> bookings;
        if (UserRole.KUND == targetUser.getRole()) {
            bookings = bokningRepository.findAllByKundId(targetUserId);
        } else if (UserRole.STÄDARE == targetUser.getRole()) {
            bookings = bokningRepository.findAllByStädareId(targetUserId);
        } else {
            return Collections.emptyList();
        }

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No bookings found for the user");
        }


        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<BokningDTO> getNotAssignedBookings() {
        List<Bokning> bookings = bokningRepository.findAllByCleaningReportStatus(CleaningReportStatus.NOT_ASSIGNED);

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No not assigned bookings found");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<BokningDTO> fetchNotStartedBookingsForUserByAdmin(Integer targetUserId) {
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found"));

        List<Bokning> bookings;
        if (UserRole.KUND == targetUser.getRole()) {
            bookings = bokningRepository.findAllByKundIdAndCleaningReportStatus(targetUserId, CleaningReportStatus.NOT_STARTED);
        } else if (UserRole.STÄDARE == targetUser.getRole()) {
            bookings = bokningRepository.findAllByStädareIdAndCleaningReportStatus(targetUserId, CleaningReportStatus.NOT_STARTED);
        } else {
            return Collections.emptyList();
        }

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No not started bookings found for the user");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }


    public List<BokningDTO> fetchInProgressBookingsForUserByAdmin(Integer targetUserId) {
        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found"));

        List<Bokning> bookings;
        if (UserRole.KUND == targetUser.getRole()) {
            bookings = bokningRepository.findAllByKundIdAndCleaningReportStatus(targetUserId, CleaningReportStatus.IN_PROGRESS);
        } else if (UserRole.STÄDARE == targetUser.getRole()) {
            bookings = bokningRepository.findAllByStädareIdAndCleaningReportStatus(targetUserId, CleaningReportStatus.IN_PROGRESS);
        } else {
            return Collections.emptyList();
        }

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No in-progress bookings found for the user");
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }



    //Från kundens perspektiv (alltså det är bara kunden som använder BookingStatus enumet borträknad admin)
    public List<BokningDTO> fetchCompletedBookingsForUserByAdmin(Integer targetUserId, Integer adminUserId) {
        UserEntity admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found with id: " + adminUserId));

        if (!admin.getRole().equals(UserRole.ADMIN)) {
            throw new SecurityException("Unauthorized access. Only admins can fetch bookings for other users.");
        }

        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found with id: " + targetUserId));

        List<Bokning> bookings;
        if (targetUser.getRole().equals(UserRole.KUND)) {
            bookings = bokningRepository.findAllByKundIdAndBookingStatus(targetUserId, BookingStatus.COMPLETED);
        } else if (targetUser.getRole().equals(UserRole.STÄDARE)) {
            bookings = bokningRepository.findAllByStädareIdAndBookingStatus(targetUserId, BookingStatus.COMPLETED);
        } else {
            // Handle other roles or default case as needed
            bookings = Collections.emptyList();
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }



    //Från städarens perspektiv (alltså det är bara städaren som använder CleaningReportStatus enumet borträknad admin)
    public List<BokningDTO> fetchReportedCompletedBookingsForUserByAdmin(Integer targetUserId, Integer adminUserId) {
        UserEntity admin = userRepository.findById(adminUserId)
                .orElseThrow(() -> new EntityNotFoundException("Admin user not found with id: " + adminUserId));

        if (!admin.getRole().equals(UserRole.ADMIN)) {
            throw new SecurityException("Unauthorized access. Only admins can fetch reported completed bookings for other users.");
        }

        UserEntity targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new EntityNotFoundException("Target user not found with id: " + targetUserId));

        List<Bokning> bookings;
        if (targetUser.getRole().equals(UserRole.KUND)) {
            bookings = bokningRepository.findAllByKundIdAndCleaningReportStatus(targetUserId, CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        } else if (targetUser.getRole().equals(UserRole.STÄDARE)) {
            bookings = bokningRepository.findAllByStädareIdAndCleaningReportStatus(targetUserId, CleaningReportStatus.REPORTED_COMPLETED_AND_READY_FOR_CUSTOMER_REVIEW);
        } else {
            // Handle other roles or default case as needed
            bookings = Collections.emptyList();
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }

    // Endpoints to search for users
    // Bellow two service methods used for search endpoints will be used before requesting the fetchCompletedBookingsForUserByAdmin and
    // fetchReportedCompletedBookingsForUserByAdmin service methods above.

    // The corresponding endpoints will be used in two different cells in the frontend to search for users.
    // after pressing on a user its userId needs to be saved in a state in the frontend
    // and to be sent in the request to the endpoints mentioned above
    public boolean isAdmin(Integer userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));
        return user.getRole().equals(UserRole.ADMIN);
    }

    public List<KundDTO> searchUsersByRole(String query, UserRole role) {
        List<UserEntity> users = userRepository.findByFirstnameContainingOrLastnameContainingOrEmailContainingAndRole(query, query, query, role);
        return users.stream().map(bokningMapper::mapUserEntityToKundDTO).collect(Collectors.toList());
    }




    public Map<YearMonth, Integer> calculateCleanerMonthlyIncome(Integer cleanerId) {
        List<Bokning> completedBookings = fetchCompletedBookingsForCleaner(cleanerId, BookingStatus.COMPLETED);
        return completedBookings.stream()
                .collect(Collectors.groupingBy(
                        booking -> YearMonth.from(booking.getBookingTime()),
                        Collectors.summingInt(booking -> tjanstService.getPriceForCleaningType(booking.getTjänst().getStädningsAlternativ()))
                ));
    }

    private List<Bokning> fetchCompletedBookingsForCleaner(Integer cleanerId, BookingStatus status) {
        // Fetch bookings for the specified cleaner
        return bokningRepository.findAllByStädareIdAndBookingStatus(cleanerId, status);
    }



//Fetch a list of all customers
    public List<KundDTO> getAllUsersByRole(UserRole role) {
        List<UserEntity> users = userRepository.findByRole(role);
        return users.stream()
                .map(bokningMapper::mapUserEntityToKundDTO) // Use the mapper method
                .collect(Collectors.toList());
    }

    //Create a new user and send mail

    public AuthenticationResponse registerUser(RegisterRequest request) {
        // Check if user with the given email already exists
        if (userRepository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        UserEntity user;
        switch (request.getRole()) {
            case KUND:
                user = new Kund();
                break;
            case ADMIN:
                user = new Admin();
                break;
            case STÄDARE:
                user = new Städare();
                break;
            default:
                user = new UserEntity();
                break;
        }

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        userRepository.save(user);

        // Prepare and send registration email
        prepareAndSendRegistrationEmail(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // This method should be outside the transactional context
    private void prepareAndSendRegistrationEmail(UserEntity user) {
        String email = user.getEmail();
        String subject = "Welcome to Our Service";
        String body = "Dear " + user.getFirstname() + ",\n\nWelcome to our service. Your account has been successfully created.";

        // Send the email
        notificationsService.sendRegistrationEmail(email, subject, body, user);
    }



    //Metod för att uppdatera kundinformationen
    public void updateUser(UserUpdateRequest updateRequest) throws UserNotFoundException {
        UserEntity user = userRepository.findById(updateRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update details. Bellows it is included null handling in cas Admin wants to change only some of the fields and not others.

        if (updateRequest.getFirstname() != null) {
            user.setFirstname(updateRequest.getFirstname());
        }
        if (updateRequest.getLastname() != null) {
            user.setLastname(updateRequest.getLastname());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }
        if (updateRequest.getPassword() != null) {
            user.setPassword(updateRequest.getPassword());
        }
            // Other fields as needed

            userRepository.save(user);
        }


    //Metod för att ta bort en kund
    public void deleteKund(UserDeleteRequest deleteRequest) throws UserNotFoundException, IllegalStateException {
        Integer kundId = deleteRequest.getUserId();
        Kund kund = kundRepository.findById(kundId)
                .orElseThrow(() -> new UserNotFoundException("Kund not found"));

        // Prevent deletion if there are active bookings
        if (hasActiveBookings(kund)) {
            throw new IllegalStateException("Kund cannot be deleted due to active bookings.");
        }

        // Disassociate non-active bookings from the Kund
        for (Bokning booking : kund.getBokningar()) {
            if (!isBookingActive(booking)) {
                booking.setKund(null);
                bokningRepository.save(booking);
            }
        }

        // Proceed with deletion if there are no active bookings
        kundRepository.delete(kund);
    }



    //Delete STÄDARE
    public void deleteStädare(UserDeleteRequest deleteRequest) throws UserNotFoundException, IllegalStateException {
        Integer städareId = deleteRequest.getUserId();
        Städare städare = städareRepository.findById(städareId)
                .orElseThrow(() -> new UserNotFoundException("Städare not found"));

        // Check for active bookings specifically for this Städare
        if (hasActiveBookingsForStädare(städare)) {
            throw new IllegalStateException("Städare cannot be deleted due to active bookings.");
        }

        // Disassociate non-active bookings from the Städare
        for (Bokning booking : städare.getBokningar()) {
            if (!isBookingActive(booking)) {
                booking.setStädare(null);
                bokningRepository.save(booking);
            }
        }

        // Delete the Städare
        städareRepository.delete(städare);
    }



    // Endpoint to delete an Admin
    public void deleteAdmin(UserDeleteRequest deleteRequest) throws UserNotFoundException {
        Admin admin = adminRepository.findById(deleteRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        adminRepository.delete(admin);
    }

    private boolean hasActiveBookings(Kund kund) {
        // Find bookings by user
        List<Bokning> bookings = bokningRepository.findAllByKundId(kund.getId());

        return bookings.stream()
                .anyMatch(this::isBookingActive);
    }

    private boolean hasActiveBookingsForStädare(Städare städare) {
        List<Bokning> bookings = bokningRepository.findAllByStädareId(städare.getId());

        return bookings.stream()
                .anyMatch(this::isBookingActive);
    }


    private boolean isBookingActive(Bokning booking) {
        // A booking is considered active if it is either cancelled or paid
        return booking.getBookingStatus() != BookingStatus.CANCELLED ||
                booking.getBookingStatus() != BookingStatus.PAID;
    }
}

