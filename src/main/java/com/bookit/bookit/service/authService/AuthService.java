package com.bookit.bookit.service.authService;

import com.bookit.bookit.config.JwtService;
import com.bookit.bookit.controller.authController.AuthenticationRequest;
import com.bookit.bookit.controller.authController.AuthenticationResponse;
import com.bookit.bookit.controller.authController.RegisterRequest;
import com.bookit.bookit.entity.admin.Admin;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.UserRole;
import com.bookit.bookit.exception.UserAlreadyExistsException;
import com.bookit.bookit.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;


@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

  /*  public AuthenticationResponse register(RegisterRequest request) {
        var user = UserEntity.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }*/

    public AuthenticationResponse register(RegisterRequest request) {
        // Check if user already exists. If exists returnes 409 in the body when checking in Postman
        if (repository.findUserByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }

        UserEntity user;
        switch (request.getRole()) {
            case KUND:
                user = new Kund();
                break;
            case ADMIN:
                user = new Admin();
                break;
            case STÄDARE:
                user = new Städare();
                break;
            default:
                user = new UserEntity();
                break;
        }

        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());

        repository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
       try {
           authenticationManager.authenticate(
                   new UsernamePasswordAuthenticationToken(
                           request.getEmail(),
                           request.getPassword()
                   )
                   //Att this point the username(the email) and password are correct..
                   //So if both of them are correct I just need to generate a token and send it back to the client
           );
       }catch (AuthenticationException e){
           throw new BadCredentialsException("Invalid username/password");
       }
        var user = repository.findUserByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

       var jwtToken = jwtService.generateToken(user); //When I get the user I can use this user object to generate a token...
        return AuthenticationResponse.builder()
                .token(jwtToken) //...and return this authentication response
                .build();
    }
}
