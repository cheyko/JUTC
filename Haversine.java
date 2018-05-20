package com.jutcjm.jutc;

import java.math.BigDecimal;

/**
 * Created by ariel on 4/21/18.
 */

// this is just to recommit this directory
public class Haversine {

    private static final int EARTH_RADIUS = 6371 * 1000; // Approx Earth radius in KM

    public static double distance(BigDecimal startLat, BigDecimal startLong,
                                  BigDecimal endLat, BigDecimal endLong) {

        double dLat  = Math.toRadians( Double.parseDouble((endLat.subtract(startLat)).toString()) );
        double dLong = Math.toRadians( Double.parseDouble((endLong.subtract(startLong)).toString()) );

        double startLatRad = Math.toRadians( Double.parseDouble(startLat.toString()));
        double endLatRad   = Math.toRadians( Double.parseDouble(endLat.toString()));

        double a = haversin(dLat) + Math.cos(startLatRad) * Math.cos(endLatRad) * haversin(dLong);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c; // <-- d
    }

    public static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
}
