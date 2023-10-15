package com.bookit.bookit.entity.bokning;

import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.tjänst.Tjänst;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bokning {
    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Kund kund;

    @ManyToOne
    private Städare städare;

    @ManyToOne
    private Tjänst tjänst;

    private LocalDateTime bookingTime;

    private String message;


}
