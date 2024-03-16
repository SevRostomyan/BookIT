package com.bookit.bookit.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponsDTO {

        private Integer id;
        private Double totaltBelopp;
        private LocalDate invoiceDate;
        private String förfallodatum;
        // Andra relevanta fält...

        // Information från Tjänst entiteten
        private String tjänstTyp;

        // Getters och Setters...

}
