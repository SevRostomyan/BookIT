package com.bookit.bookit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    private String token; //Token som ska skickas tillbaka till usern
    private String role; //User rollen också skickas som en del av jwt token
    private String errorMessage; // New field for error messages in case a user is unauthorised
}
