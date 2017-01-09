package com.justinwells.mytravelproject.Singletons;

import com.justinwells.mytravelproject.CustomObjects.Flight;
import com.justinwells.mytravelproject.CustomObjects.Hotel;
import com.justinwells.mytravelproject.CustomObjects.Weather;

/**
 * Created by justinwells on 1/7/17.
 */

public class DetailDataHolder {
    static DetailDataHolder sInstance;
    Hotel hotel;
    Flight flight;
    Weather weather;
    Boolean weatherDone, hotelDone;

    private DetailDataHolder () {
        weatherDone = false;
        hotelDone = false;
    }

    public static DetailDataHolder getInstance () {
        if (sInstance == null) {
            sInstance = new DetailDataHolder();
        }

        return sInstance;
    }

    public void destroy () {
        sInstance = null;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }



    public void setWeatherDone(Boolean weatherDone) {
        this.weatherDone = weatherDone;
    }



    public void setHotelDone(Boolean hotelDone) {
        this.hotelDone = hotelDone;
    }

    public boolean getCallsDone () {
        return weatherDone && hotelDone;
    }
}
