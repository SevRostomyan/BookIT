package com.bookit.bookit.entity.faktura;

import com.bookit.bookit.entity.bokning.Bokning;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faktura {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String fakturanummer;
    private Double totaltBelopp;
    private String förfallodatum;

    @ManyToOne
    private Bokning bokning;
}
