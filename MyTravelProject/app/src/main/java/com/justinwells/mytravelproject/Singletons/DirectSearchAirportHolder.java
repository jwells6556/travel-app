package com.justinwells.mytravelproject.Singletons;

import com.justinwells.mytravelproject.CustomObjects.Airport;

/**
 * Created by justinwells on 1/6/17.
 */
public class DirectSearchAirportHolder {
    private static DirectSearchAirportHolder sInstance;
    Airport airport;

    public static DirectSearchAirportHolder getInstance() {
        if (sInstance == null) {
            sInstance = new DirectSearchAirportHolder();
        }
        return sInstance;
    }

    public Airport getAirport() {
        return airport;
    }

    public void setAirport(Airport airport) {
        this.airport = airport;
    }

    private DirectSearchAirportHolder() {
    }
}
