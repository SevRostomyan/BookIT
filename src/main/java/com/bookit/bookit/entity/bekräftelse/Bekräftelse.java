package com.bookit.bookit.entity.bekräftelse;

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
public class Bekräftelse {
    @Id
    @GeneratedValue
    private Integer id;
    private String meddelande;

    @ManyToOne
    private Bokning bokning;
}
