package com.bookit.bookit.entity.faktura;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.kund.Kund;
import com.bookit.bookit.entity.notifications.Notifications;
import com.bookit.bookit.entity.tjänst.Tjänst;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String invoiceFilePath; // Path or URL to the invoice file (Ej använt än. Behöver logik för att hämta filer)

    @OneToOne(mappedBy = "faktura")
    @JsonManagedReference
    private Notifications notification;

    private String fakturanummer;

    @ManyToOne
    @JoinColumn(name = "kund_id")
    @JsonBackReference
    private Kund kund;

    @ManyToOne
    @JoinColumn(name = "bokning_id")
    @JsonBackReference
    private Bokning bokning;

    @ManyToOne
    @JoinColumn(name = "tjänst_id")
    @JsonBackReference
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
