package com.bookit.bookit.entity.tjänst;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tjänst {
    @Id
    @GeneratedValue
    private Integer id;

    @Enumerated(EnumType.STRING)
    private StädningsAlternativ städningsAlternativ; //BASIC, TOPP, DIAMANT, FÖNSTERTVÄTT

    @OneToMany(mappedBy = "tjänst")
    @JsonIgnore
    private List<Bokning> bokningar;

    @OneToMany(mappedBy = "tjänst")
    @JsonIgnore
    private List<Faktura> faktura;

}
