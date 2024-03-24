package com.bookit.bookit.entity.kund;
import com.bookit.bookit.entity.bokning.Bokning;

import com.bookit.bookit.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.ToString;
import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Kund extends UserEntity {


    @OneToMany(mappedBy = "kund", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude // Exclude from toString() to avoid infinite recursion
    private List<Bokning> bokningar;
    //Kopplad till Bokning klassen för att kunna se bokningar kopplade till kunden

}
