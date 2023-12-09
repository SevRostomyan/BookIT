package com.bookit.bookit.dto;

import com.bookit.bookit.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//Request DTO - Skickar in data till servern
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private UserRole role;
    // You can add more fields if needed
}
