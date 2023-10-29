package com.bookit.bookit.service.bokning;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.repository.bokning.BokningRepository;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;

@Service
public class BokningService {

    private final BokningRepository bokningRepository;

    public BokningService(BokningRepository bokningRepository) {
        this.bokningRepository = bokningRepository;
    }

    public List<Bokning> getBookingsByRole(String role, Integer userId) {
        if ("Kund".equals(role)) {
            return bokningRepository.findAllByKundId(userId);
        } else if ("Städare".equals(role)) {
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

    public List<Bokning> getCompletedBookingsByRole(String role, Integer userId) {
        if ("Kund".equals(role)) {
            return bokningRepository.findAllByKundIdAndStatus(userId, BookingStatus.COMPLETED);
        } else if ("Städare".equals(role)) {
            // Assuming you have a similar method in your repository for Städare
            return bokningRepository.findAllByStädareIdAndStatus(userId, BookingStatus.COMPLETED);
        }
        // Add more roles as needed
        return null;
    }
}





