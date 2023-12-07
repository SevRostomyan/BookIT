package com.bookit.bookit.entity.kund;
import com.bookit.bookit.entity.bokning.Bokning;

import com.bookit.bookit.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Kund extends UserEntity {


    @OneToMany(mappedBy = "kund", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    private List<Bokning> bokningar;

/*
    @OneToOne(cascade = CascadeType.ALL)
    private User user; //Kopplat till User klassen för inloggningssyfte
*/

}
