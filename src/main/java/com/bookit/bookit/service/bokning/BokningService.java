package com.bookit.bookit.service.bokning;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.repository.bokning.BokningRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BokningService {

    @Autowired
    private BokningRepository bokningRepository;

    public List<Bokning> getBookingsByRole(String role, Integer userId) {
        if ("Kund".equals(role)) {
            return bokningRepository.findAllByKundId(userId);
        } else if ("Städare".equals(role)) {
            return bokningRepository.findAllByStädareId(userId);
        }
        // Add more roles as needed
        return null;
    }
}
