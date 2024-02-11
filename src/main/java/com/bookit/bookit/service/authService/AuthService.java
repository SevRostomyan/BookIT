package com.bookit.bookit.service.authService;

import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.dto.AuthenticationRequest;
import com.bookit.bookit.dto.AuthenticationResponse;
import com.bookit.bookit.dto.RegisterRequest;
import com.bookit.bookit.entity.admin.Admin;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.exception.UserAlreadyExistsException;
import com.bookit.bookit.repository.user.UserRepository;
import com.bookit.bookit.service.notifications.NotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationsService notificationsService;

    private final AuthenticationManager authenticationManager;


    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists. If exists return 409 in the body when checking in Postman
        if (repository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        UserEntity user = switch (request.getRole()) {
            case KUND -> new Kund();
            case ADMIN -> new Admin();
            case STÄDARE -> new Städare();
        };

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        repository.save(user);

        // Prepare and send registration email
        prepareAndSendRegistrationEmail(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    // This method should be outside the transactional context
    private void prepareAndSendRegistrationEmail(UserEntity user) {
        String email = user.getEmail();
        String subject = "Welcome to Our Service";
        String body = "Dear " + user.getFirstname() + ",\n\nWelcome to our service. Your account has been successfully created.";

        // Send the email
        notificationsService.sendRegistrationEmail(email, subject, body, user);
    }

    // This method actually sends the email and should handle any exceptions internally


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
                    //Att this point the username(the email) and password are correct...
                    //So if both of them are correct I just need to check the role and if it is also correct generate a token
                    // and send it back to the client
            );
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Invalid username/password");
        }
        var user = repository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Check if the user's actual role matches the intended role
        if (!user.getRole().name().equalsIgnoreCase(request.getIntendedRole())) {
            return AuthenticationResponse.builder()
                    .errorMessage("Obehörig åtkomst. Vänligen välj rätt inloggningsroll.")
                    .build();
        }

        var jwtToken = jwtService.generateToken(user); //When I get the user I can use this user object to generate a token...
        return AuthenticationResponse.builder()
                .token(jwtToken) //...and return this authentication response
                .role(user.getRole().name())
                .build();
    }
}
