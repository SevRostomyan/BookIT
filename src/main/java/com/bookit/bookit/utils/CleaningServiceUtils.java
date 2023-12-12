package com.bookit.bookit.utils;

import com.bookit.bookit.enums.StädningsAlternativ;

public class CleaningServiceUtils {

    public static String getServiceTypeKey(StädningsAlternativ alternativ) {
        switch (alternativ) {
            case BASIC:
                return "cleaning.basic";
            case TOPP:
                return "cleaning.topp";
            case DIAMANT:
                return "cleaning.diamant";
            case FÖNSTERTVÄTT:
                return "cleaning.fonstertvatt";
            default:
                throw new IllegalArgumentException("Unknown StädningsAlternativ: " + alternativ);
        }
    }
}
