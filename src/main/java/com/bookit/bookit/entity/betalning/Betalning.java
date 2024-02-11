package com.bookit.bookit.entity.betalning;

import com.bookit.bookit.entity.bokning.Bokning;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Betalning {
    @Id
    @GeneratedValue
    private Integer id;
    private Double belopp;
    private String betalningsmetod; // t.ex. Kort, banköverföring, etc.
    @ManyToOne
    private Bokning bokning;
}
