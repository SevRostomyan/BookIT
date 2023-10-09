package com.bookit.bookit.entity.gdpr;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GDPR {
    @Id
    @GeneratedValue
    private Integer id;
    // fält som representerar användardata och GDPR-relaterad information
    // Kan vara kopplad till både Kund och Städare om nödvändigt
}
