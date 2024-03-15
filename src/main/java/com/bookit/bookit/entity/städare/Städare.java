package com.bookit.bookit.entity.städare;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Städare extends UserEntity {


    @OneToMany(mappedBy = "städare", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Bokning> bokningar;



}
