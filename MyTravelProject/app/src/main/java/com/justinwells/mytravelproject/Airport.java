package com.justinwells.mytravelproject;

/**
 * Created by justinwells on 12/22/16.
 */

public class Airport {
    private String mCode, mCity, mName;

    public Airport(String mCode, String mCity, String mName) {
        this.mCode = mCode;
        this.mCity = mCity;
        this.mName = mName;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String mCode) {
        this.mCode = mCode;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String mCity) {
        this.mCity = mCity;
    }

    public String getAirportName() {
        return mName;
    }

}
