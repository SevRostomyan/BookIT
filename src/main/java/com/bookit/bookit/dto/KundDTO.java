package com.bookit.bookit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KundDTO {
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    // You can add more fields if needed
}
