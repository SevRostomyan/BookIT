package com.bookit.bookit.service.admin;


import com.bookit.bookit.dto.BokningDTO;
import com.bookit.bookit.dto.KundDTO;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.user.UserRepository;
import com.bookit.bookit.utils.BokningMapper;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final BokningRepository bokningRepository;
    private final BokningMapper bokningMapper;


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




}
