package com.bookit.bookit.config;

import com.bookit.bookit.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository repository;

    //Detta kodblock definierar en UserDetailsService som använder en funktionell metod
    // för att hämta en användare med e-post och kasta ett undantag om användaren inte finns.
    //Används i authenticationProvider nedan
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findUserByEmail(username)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    //Här definieras en AuthenticationProvider som använder DaoAuthenticationProvider
    // för att hämta användardetaljer och hantera lösenordskryptering.
    @Bean
    public AuthenticationProvider authenticationProvider(){  //This is the data access object  which is responsible to fetch the user details and also encode password etc
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    //Detta kodblock skapar en AuthenticationManager genom att hämta den från en AuthenticationConfiguration.
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    //Detta kodblock instansierar en PasswordEncoder som använder BCryptPasswordEncoder för att säkert koda lösenord.
    //Används i authenticationProvider ovan
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
