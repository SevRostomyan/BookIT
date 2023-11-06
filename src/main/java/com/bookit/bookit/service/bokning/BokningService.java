package com.bookit.bookit.service.bokning;

import com.bookit.bookit.dto.BokningDTO;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.user.UserRepository;
import com.bookit.bookit.utils.BokningMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BokningService {

    private final BokningRepository bokningRepository;
    private final UserRepository userRepository;
    private final BokningMapper bokningMapper;


    public String getUserRoleById(Integer userId) {
        Optional<UserEntity> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getRole().name();
        }
        return null;
    }

    public List<BokningDTO> getBookingsByUserId(Integer userId) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User not found");
        }

        List<Bokning> bookings;
        if (UserRole.KUND == user.getRole()) {
            bookings = bokningRepository.findAllByKundId(userId);
        } else if (UserRole.STÄDARE == user.getRole()) {
            bookings = bokningRepository.findAllByStädareId(userId);
        } else {
            return Collections.emptyList();
        }

        if (bookings.isEmpty()) {
            throw new EntityNotFoundException("No bookings found for the user");
        }

        // Filter by desired statuses
        List<BookingStatus> desiredStatuses = Arrays.asList(BookingStatus.PENDING, BookingStatus.CONFIRMED, BookingStatus.UNDERKAND);
        bookings = bookings.stream()
                .filter(b -> desiredStatuses.contains(b.getStatus()))
                .collect(Collectors.toList());

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }


    @Transactional
    //General method for changing the booking status based on the status enum including marking the booking as completed(godkänd,avslutad)
    public void updateBookingStatus(Integer bookingId, BookingStatus newStatus) {
        bokningRepository.updateBookingStatus(bookingId, newStatus);
    }

    @Transactional
    public void saveCustomerFeedback(Integer bookingId, String feedback) {
        Bokning bokning = bokningRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException ("Bokning not found"));
        bokning.setCustomerFeedback(feedback);
        bokningRepository.save(bokning);
    }

    public List<BokningDTO> getCompletedBookingsByUserId(Integer userId) {
        List<Bokning> bookings;
        String role = getUserRoleById(userId);
        if ("KUND".equals(role)) {
            bookings = bokningRepository.findAllByKundIdAndStatus(userId, BookingStatus.COMPLETED);
        } else if ("STÄDARE".equals(role)) {
            bookings = bokningRepository.findAllByStädareIdAndStatus(userId, BookingStatus.COMPLETED);
        } else {
            return Collections.emptyList();
        }

        // Convert to DTOs
        return bookings.stream()
                .map(bokningMapper::mapToDTO)
                .collect(Collectors.toList());
    }

}





