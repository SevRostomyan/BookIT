package com.bookit.bookit.service.kund;
import com.bookit.bookit.dto.CleaningBookingRequest;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.tjänst.Tjänst;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import com.bookit.bookit.repository.tjänst.TjänstRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
//@RequiredArgsConstructor
@AllArgsConstructor
public class KundService {
    private final BokningRepository bokningRepository;
    private final KundRepository kundRepository;
    private final TjänstRepository tjänstRepository;

    @Transactional
    public String bookCleaning(CleaningBookingRequest request) {
        Kund kund = kundRepository.findById(request.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Kund not found"));

        // Check for duplicate bookings based on time
        if (bokningRepository.existsByKundAndTjänst_StädningsAlternativAndBookingTime(
                kund, request.getStädningsAlternativ(), request.getBookingTime())) {
            return "You have already booked this cleaning service at this time.";
        }

        // Always create a new Tjänst for each booking
        Tjänst tjänst = new Tjänst();
        tjänst.setStädningsAlternativ(request.getStädningsAlternativ());
        tjänstRepository.save(tjänst); // Save the Tjänst entity

        // Save booking information to the database
        Bokning newBooking = new Bokning();
        newBooking.setKund(kund);
        newBooking.setTjänst(tjänst); // Set the persisted Tjänst entity here
        newBooking.setBookingTime(request.getBookingTime());
        newBooking.setAdress(request.getAdress());
        newBooking.setMessageAtBooking(request.getMessageAtBooking());
        newBooking.setBookingStatus(BookingStatus.PENDING);

        bokningRepository.save(newBooking);

        return "Booking successful.";
    }


}
