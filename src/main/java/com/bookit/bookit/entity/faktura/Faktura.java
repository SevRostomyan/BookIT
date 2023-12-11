package com.bookit.bookit.entity.faktura;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.notifications.Notifications;
import com.bookit.bookit.entity.tjänst.Tjänst;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Faktura {
    @Id
    @GeneratedValue
    private Integer id;
    private String invoiceFilePath; // Path or URL to the invoice file   (Ej använt än. Behöver logik för att hämta filer)

    @OneToOne(mappedBy = "faktura")
    private Notifications notification;

    private String fakturanummer;

    @ManyToOne
    private Kund kund;

    @ManyToOne
    private Bokning bokning;

    @ManyToOne
    private Tjänst tjänst;

    private Double totaltBelopp;
    private Double priceExclVAT;
    private LocalDate invoiceDate;
    private String förfallodatum;


    // Company details
    private String companyName;
    private String organisationalNumber;
    private String companyAddress;

    // Customer details
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
}
