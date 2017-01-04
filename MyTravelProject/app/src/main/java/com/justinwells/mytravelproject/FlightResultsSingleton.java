package com.justinwells.mytravelproject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justinwells on 1/4/17.
 */
public class FlightResultsSingleton {
    private static FlightResultsSingleton sInstance;
    List<Flight>flightResultsList;

    private FlightResultsSingleton () {
       flightResultsList = new ArrayList<>();
    }

    public static FlightResultsSingleton getInstance () {
        if (sInstance == null) {
            sInstance = new FlightResultsSingleton();
        }

        return sInstance;
    }

    public Flight getFlight (int index) {
        return flightResultsList.get(index);
    }

    public void clearList () {
        flightResultsList.clear();
    }

    public void newList (List<Flight>list) {
        clearList();
        flightResultsList = list;
    }

    public List<Flight> getFlightResultsList () {
        return flightResultsList;
    }
}
