package com.justinwells.mytravelproject.Singletons;

import com.justinwells.mytravelproject.CustomObjects.Flight;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justinwells on 1/4/17.
 */
public class FlightResultsSingleton {
    private static FlightResultsSingleton sInstance;
    List<Flight>flightResultsList;
    Flight directSearchFlight;
    boolean callComplete;

    public boolean isCallComplete() {
        return callComplete;
    }

    public void setCallComplete(boolean callComplete) {
        this.callComplete = callComplete;
    }

    private FlightResultsSingleton () {

        flightResultsList = new ArrayList<>();
        callComplete = false;
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
        if (flightResultsList!=null) {
            flightResultsList.clear();
        }
    }

    public void newList (List<Flight>list) {
        clearList();
        flightResultsList = list;
    }

    public List<Flight> getFlightResultsList () {
        return flightResultsList;
    }

    public Flight getDirectSearchFlight() {
        return directSearchFlight;
    }

    public void setDirectSearchFlight(Flight directSearchFlight) {
        this.directSearchFlight = directSearchFlight;
    }

    public void reset () {
        sInstance = null;
    }
}
