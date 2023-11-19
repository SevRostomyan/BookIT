package com.bookit.bookit.utils;

import com.bookit.bookit.dto.BokningDTO;
import com.bookit.bookit.dto.KundDTO;
import com.bookit.bookit.dto.StädareDTO;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BokningMapper {

    public BokningDTO mapToDTO(Bokning bokning) {
        BokningDTO dto = new BokningDTO();
        dto.setId(bokning.getId());
        dto.setKund(mapToKundDTO(bokning.getKund()));

        // Check if Städare is not null before mapping
        if (bokning.getStädare() != null) {
            dto.setStädare(mapToStädareDTO(bokning.getStädare()));
        }

        dto.setBookingTime(bokning.getBookingTime());
        dto.setAdress(bokning.getAdress());
        dto.setMessageAtBooking(bokning.getMessageAtBooking());
        dto.setCustomerFeedback(bokning.getCustomerFeedback());
        dto.setStatus(bokning.getBookingStatus());
        dto.setCleaningReportStatus(bokning.getCleaningReportStatus());
        return dto;
    }

    public KundDTO mapToKundDTO(Kund kund) {
        KundDTO dto = new KundDTO();
        dto.setId(kund.getId());
        dto.setFirstname(kund.getFirstname());
        dto.setLastname(kund.getLastname());
        dto.setEmail(kund.getEmail());
        return dto;
    }

    public StädareDTO mapToStädareDTO(Städare städare) {
        StädareDTO dto = new StädareDTO();
        dto.setId(städare.getId());
        dto.setFirstname(städare.getFirstname());
        dto.setLastname(städare.getLastname());
        dto.setEmail(städare.getEmail());
        return dto;
    }


    public KundDTO mapUserEntityToKundDTO(UserEntity user) {
        KundDTO dto = new KundDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname()); // Adjust these according to the actual structure of UserEntity
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        return dto;
    }

}
