package com.bookit.bookit.utils;

import com.bookit.bookit.dto.BokningDTO;
import com.bookit.bookit.dto.KundDTO;
import com.bookit.bookit.dto.StädareDTO;
import com.bookit.bookit.dto.UserDTO;
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
        dto.setTjänst(bokning.getTjänst());
        dto.setBookingTime(bokning.getBookingTime());
        dto.setEndTime(bokning.getEndTime() != null ? bokning.getEndTime() : null);
        dto.setAdress(bokning.getAdress());
        dto.setMessageAtBooking(bokning.getMessageAtBooking());
        dto.setCleaningReportedTime(bokning.getCleaningReportedTime() != null ? bokning.getCleaningReportedTime() : null);
        dto.setCustomerFeedback(bokning.getCustomerFeedback() != null ? bokning.getCustomerFeedback() : null);
        dto.setStatus(bokning.getBookingStatus());
        dto.setCleaningReportStatus(bokning.getCleaningReportStatus());

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



    public UserDTO mapUserEntityToUserDTO(UserEntity user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }


    //Mappar kundEntity till KundDTO
    public KundDTO mapToKundDTO(Kund kund) {
        KundDTO dto = new KundDTO();
        dto.setId(kund.getId());
        dto.setFirstname(kund.getFirstname());
        dto.setLastname(kund.getLastname());
        dto.setEmail(kund.getEmail());
        dto.setPassword(kund.getPassword());
        dto.setRole(kund.getRole());
        return dto;
    }

    //Mappar KundDTO till KundEntity
    public Kund mapToKund(KundDTO kundDTO) {
        Kund kund = new Kund();
        kund.setId(kundDTO.getId());
        kund.setFirstname(kundDTO.getFirstname());
        kund.setLastname(kundDTO.getLastname());
        kund.setEmail(kundDTO.getEmail());
        kund.setPassword(kund.getPassword());
        kund.setRole(kundDTO.getRole());
        return kund;
    }

}
