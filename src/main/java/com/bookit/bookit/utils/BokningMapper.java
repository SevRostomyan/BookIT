package com.bookit.bookit.utils;

import com.bookit.bookit.dto.*;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.user.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class BokningMapper {

    public BokningDTO mapToDTO(Bokning bokning) {
        BokningDTO dto = new BokningDTO();
        dto.setId(bokning.getId());

        if (bokning.getKund() != null) {
            BasicKundDTO kundDto = new BasicKundDTO();
            kundDto.setId(bokning.getKund().getId());
            kundDto.setFirstname(bokning.getKund().getFirstname());
            kundDto.setLastname(bokning.getKund().getLastname());
            kundDto.setEmail(bokning.getKund().getEmail());
            dto.setKund(kundDto);
        }

        // Check if Städare is not null before mapping
        if (bokning.getStädare() != null) {
            dto.setStädare(mapToStädareDTO(bokning.getStädare()));
        }
        dto.setTjänst(bokning.getTjänst().getStädningsAlternativ());
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


}
