package com.bookit.bookit.service.bokning;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.user.User;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class BokningService {

    private final BokningRepository bokningRepository;
    private final UserRepository userRepository;

    public BokningService(BokningRepository bokningRepository, UserRepository userRepository) {
        this.bokningRepository = bokningRepository;
        this.userRepository = userRepository;
    }

    public String getUserRoleById(Integer userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get().getRole().name();
        }
        return null;
    }

    public List<Bokning> getBookingsByUserId(Integer userId) {
        String role = getUserRoleById(userId);
        if (role == null) {
            return null; // or throw an exception
        }

        if ("KUND".equals(role)) {
            return bokningRepository.findAllByKundId(userId);
        } else if ("STÄDARE".equals(role)) {
            return bokningRepository.findAllByStädareId(userId);
        }
        // Add more roles as needed
        return null;
    }


    //General method for changing the booking status based on the status enum including marking the booking as completed(godkänd,avslutad)
    public void updateBookingStatus(Integer bookingId, BookingStatus newStatus) {
        bokningRepository.updateBookingStatus(bookingId, newStatus);
    }

    public void saveCustomerFeedback(Integer bookingId, String feedback) {
        Bokning bokning = bokningRepository.findById(bookingId).orElseThrow(()->new EntityNotFoundException ("Bokning not found"));
        bokning.setCustomerFeedback(feedback);
        bokningRepository.save(bokning);
    }

    public List<Bokning> getCompletedBookingsByUserId(Integer userId) {
        String role = getUserRoleById(userId);
        if ("Kund".equals(role)) {
            return bokningRepository.findAllByKundIdAndStatus(userId, BookingStatus.COMPLETED);
        } else if ("Städare".equals(role)) {
            return bokningRepository.findAllByStädareIdAndStatus(userId, BookingStatus.COMPLETED);
        }
        // Add more roles as needed
        return null;
    }

}





