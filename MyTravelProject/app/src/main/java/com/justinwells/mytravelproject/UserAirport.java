package com.justinwells.mytravelproject;



/**
 * Created by justinwells on 1/5/17.
 */

public class UserAirport {
    String airport;
    public static UserAirport sInstance;

    private UserAirport () {

    }

    public static UserAirport getInstance () {
       if (sInstance == null) {
           sInstance = new UserAirport();
       }

       return sInstance;
    }

    public String getAirport() {
        if (airport == null) {
            airport = "LAX";
        }
        return airport;
    }

    public void setAirport(String airport) {
        this.airport = airport;
    }
}
