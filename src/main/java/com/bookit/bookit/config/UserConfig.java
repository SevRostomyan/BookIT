package com.bookit.bookit.config;

import com.bookit.bookit.entity.admin.Admin;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.tjänst.Tjänst;
import com.bookit.bookit.enums.BookingStatus;
import com.bookit.bookit.enums.CleaningReportStatus;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.repository.admin.AdminRepository;
import com.bookit.bookit.repository.bokning.BokningRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import com.bookit.bookit.repository.städare.StädareRepository;
import com.bookit.bookit.repository.tjänst.TjänstRepository;
import com.bookit.bookit.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaRepositories(
        {
                "com.bookit.bookit.repository.user",
                "com.bookit.bookit.repository.admin",
                "com.bookit.bookit.repository.kund",
                "com.bookit.bookit.repository.bokning",
                "com.bookit.bookit.repository.städare",
                "com.bookit.bookit.repository.tjänst"})
@Configuration
public class UserConfig {

    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminRepository adminRepository, KundRepository kundRepository, BokningRepository bokningRepository, TjänstRepository tjänstRepository, StädareRepository städareRepository) {
        return args -> {
            if (userRepository.countByRole(UserRole.ADMIN) == 0) {
                Admin admin = new Admin();
                admin.setFirstname("Sevak");
                admin.setLastname("Rostomyan");
                admin.setEmail("sev-rostomyan@hotmail.com");
                admin.setPassword(passwordEncoder.encode("superSecretPassword"));
                admin.setRole(UserRole.ADMIN);
                adminRepository.save(admin);
            }
            //Tillfällig testkund
            if (userRepository.countByRole(UserRole.KUND) == 0) {
                Kund kund = new Kund();
                kund.setFirstname("Erik");
                kund.setLastname("Erikson");
                kund.setEmail("erik-erikson@hotmail.com");
                kund.setPassword(passwordEncoder.encode("superSecretPassword"));
                kund.setRole(UserRole.KUND);
                kundRepository.save(kund);

                // Tillfällig teststädare
                if (userRepository.countByRole(UserRole.STÄDARE) == 0) {
                    Städare städare = new Städare();
                    städare.setFirstname("Anna");
                    städare.setLastname("Andersson");
                    städare.setEmail("anna-andersson@hotmail.com");
                    städare.setPassword(passwordEncoder.encode("superSecretPassword"));
                    städare.setRole(UserRole.STÄDARE);
                    städareRepository.save(städare);
                }

                /*// Hardcoded booking
                Tjänst tjänst = new Tjänst();
                tjänst.setStädningsAlternativ(StädningsAlternativ.BASIC);  // Hardcoded to BASIC
                tjänstRepository.save(tjänst);

                Bokning newBooking = new Bokning();
                newBooking.setKund(kund);
                newBooking.setTjänst(tjänst);
                newBooking.setBookingTime(LocalDateTime.now());  // Hardcoded to current time
                newBooking.setMessageAtBooking("Your message here");
                newBooking.setBookingStatus(BookingStatus.PENDING);  // Hardcoded status
                newBooking.setCleaningReportStatus(CleaningReportStatus.NOT_ASSIGNED);
                bokningRepository.save(newBooking);*/

            }


        };

    }
}

