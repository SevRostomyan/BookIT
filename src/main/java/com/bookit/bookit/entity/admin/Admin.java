package com.bookit.bookit.entity.admin;

import com.bookit.bookit.entity.notifications.Notifications;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.UserRole;
import jakarta.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class Admin extends UserEntity {


    public Admin(Integer id, String firstname, String lastname, String email, String password, UserRole role, List<Notifications> notifications) {
        super(id, firstname, lastname, email, password, role, notifications);
    }

  @OneToOne(cascade = CascadeType.ALL)
    private UserEntity user;
    //Kopplat till UserEntity klassen för inloggningssyfte


}
