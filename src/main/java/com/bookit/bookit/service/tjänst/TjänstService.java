package com.bookit.bookit.service.tjänst;

import com.bookit.bookit.enums.StädningsAlternativ;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TjänstService {


    //Tjänsterpriser injekterade från Cleaning-prices.properties filen
    @Value("${basic}")
    private int basicPrice;

    @Value("${topp}")
    private int toppPrice;

    @Value("${diamant}")
    private int diamantPrice;

    @Value("${fonstertvatt}")
    private int fonstertvattPrice;
// ... other prices

    public int getPriceForCleaningType(StädningsAlternativ type) {
        switch (type) {
            case BASIC:
                return basicPrice;
            case TOPP:
                return toppPrice;
            // ... other cases
            default:
                throw new IllegalArgumentException("Unknown cleaning type: " + type);
        }
    }
}
