package com.bookit.bookit.config;

import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.repository.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    //loadUserByUsername försöker hitta en användare med angiven e-post i databasen;
    // om användaren inte finns kastas ett undantag.
    // Om användaren finns, returneras ett UserDetails-objekt som innehåller användarens e-post, lösenord och behörigheter,
    // vilket är nödvändigt för Spring Securitys autentiseringssystem.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getAuthorities()
        );
    }
}







