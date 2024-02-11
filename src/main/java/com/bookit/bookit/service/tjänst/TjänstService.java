package com.bookit.bookit.service.tjänst;

import com.bookit.bookit.enums.StädningsAlternativ;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
//@AllArgsConstructor
public class TjänstService {

    @Value("${cleaning.basic}")
    private int basicPrice;

    @Value("${cleaning.topp}")
    private int toppPrice;

    @Value("${cleaning.diamant}")
    private int diamantPrice;

    @Value("${cleaning.fonstertvatt}")
    private int fonstertvattPrice;

    // ... other prices

    public int getPriceForCleaningType(StädningsAlternativ type) {
        return switch (type) {
            case BASIC -> basicPrice;
            case TOPP -> toppPrice;
            case DIAMANT -> diamantPrice;
            case FÖNSTERTVÄTT -> fonstertvattPrice;
            // ... other cases
        };
    }
}

