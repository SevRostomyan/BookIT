package com.bookit.bookit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private Integer userId;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    // Other fields as needed
}
