package com.bookit.bookit.entity.notifications;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.entity.user.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notifications {
    @Id
    @GeneratedValue
    private Integer id;

    private String meddelande;

    @ManyToOne
    private Bokning bokning;

    @OneToOne
    @JoinColumn(name = "id")
    private Faktura faktura;

    @ManyToOne
    private UserEntity user;

    private String subject; // Subject of the notification

    private LocalDateTime timestamp; // Time when the notification was created or sent

    private Boolean isSent; // Whether the notification was successfully sent

    private Boolean isRead; // Whether the notification has been read by the user
}

