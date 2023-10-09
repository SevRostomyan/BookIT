package com.bookit.bookit.entity.admin;

import com.bookit.bookit.entity.user.User;
import com.bookit.bookit.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Admin extends User{





    public Admin(Integer id, String firstname, String lastname, String email, String password, UserRole role) {
        super(id, firstname, lastname, email, password, role);
    }

/*  @OneToOne(cascade = CascadeType.ALL)
    private User user;
    //Kopplat till User klassen för inloggningssyfte*/

    //Troligen behöver komplettering
}
