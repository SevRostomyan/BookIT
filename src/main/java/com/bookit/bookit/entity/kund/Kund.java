package com.bookit.bookit.entity.kund;
import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.user.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Kund extends User{


    @OneToMany(mappedBy = "kund")
    private List<Bokning> bokningar;

/*
    @OneToOne(cascade = CascadeType.ALL)
    private User user; //Kopplat till User klassen för inloggningssyfte
*/

}
