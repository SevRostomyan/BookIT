package com.bookit.bookit.entity.städare;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Städare extends User{


    @OneToMany(mappedBy = "städare")
    private List<Bokning> bokningar;

  /*  @OneToOne(cascade = CascadeType.ALL)
    private User user; //Kopplat till User klassen för inloggningssyfte*/

}
