package com.bookit.bookit.utils;

import com.bookit.bookit.enums.StädningsAlternativ;

public class CleaningServiceUtils {

    public static String getServiceTypeKey(StädningsAlternativ alternativ) {
        return switch (alternativ) {
            case BASIC -> "cleaning.basic";
            case TOPP -> "cleaning.topp";
            case DIAMANT -> "cleaning.diamant";
            case FÖNSTERTVÄTT -> "cleaning.fonstertvatt";
        };
    }
}
