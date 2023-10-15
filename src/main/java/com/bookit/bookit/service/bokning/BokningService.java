package com.bookit.bookit.service.bokning;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.repository.bokning.BokningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public void cancelBooking(Integer bookingId) {
        bokningRepository.updateBookingStatus(bookingId, BookingStatus.CANCELLED);
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





