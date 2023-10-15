package com.bookit.bookit.service.kund;
import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.tjänst.Tjänst;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class KundService {
    private final BokningRepository bokningRepository;
    private final KundRepository kundRepository;

    public String bookCleaning(CleaningBookingRequest request) {
        Kund kund = kundRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Kund not found"));

        // Check for duplicate bookings based on time
        if (bokningRepository.existsByKundAndTjänst_StädningsAlternativAndBookingTime(
                kund, request.getStädningsAlternativ(), request.getBookingTime())) {
            return "You have already booked this cleaning service at this time.";
        }

        // Save booking information to the database
        Bokning newBooking = new Bokning();
        newBooking.setKund(kund);  // Set the fetched Kund entity here

        Tjänst tjänst = new Tjänst();
        tjänst.setStädningsAlternativ(request.getStädningsAlternativ());
        newBooking.setTjänst(tjänst);

        newBooking.setMessage(request.getMessage());

        newBooking.setBookingTime(request.getBookingTime());  // Set the booking time here

        bokningRepository.save(newBooking);

        return "Booking successful.";
    }


}
