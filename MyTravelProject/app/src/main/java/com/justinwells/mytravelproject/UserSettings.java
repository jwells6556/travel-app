package com.justinwells.mytravelproject;



/**
 * Created by justinwells on 1/5/17.
 */

public class UserSettings {
    String airport;
    int price;

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public static UserSettings sInstance;

    private UserSettings() {

    }

    public static UserSettings getInstance () {
       if (sInstance == null) {
           sInstance = new UserSettings();
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
