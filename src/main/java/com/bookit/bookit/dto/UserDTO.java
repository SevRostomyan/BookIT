package com.bookit.bookit.dto;

import com.bookit.bookit.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

        private Integer id;
        private String firstname;
        private String lastname;
        private String email;
        private UserRole role;
        // You can add more fields if needed

}
