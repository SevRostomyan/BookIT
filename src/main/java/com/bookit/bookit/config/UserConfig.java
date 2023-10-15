package com.bookit.bookit.config;

import com.bookit.bookit.entity.admin.Admin;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.repository.admin.AdminRepository;
import com.bookit.bookit.repository.kund.KundRepository;
import com.bookit.bookit.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableJpaRepositories("com.bookit.bookit.repository.user")
@Configuration
public class UserConfig {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final KundRepository kundRepository;

    @Autowired
    public UserConfig(UserRepository userRepository, AdminRepository adminRepository, KundRepository kundRepository) {

        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.kundRepository = kundRepository;
    }


    @Bean
    CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
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
            }
        };
    };


}

