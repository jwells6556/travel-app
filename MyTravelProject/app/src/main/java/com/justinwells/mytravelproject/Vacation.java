package com.justinwells.mytravelproject;

/**
 * Created by justinwells on 1/3/17.
 */

public class Vacation {
    Flight flight;
    Hotel hotel;
    String beginDate, endDate;

    public Vacation(Flight flight, Hotel hotel) {
        this.flight = flight;
        this.hotel = hotel;
        beginDate = flight.getDepartOrigin();
        endDate = flight.getArriveOrigin();
    }

    public Flight getFlight() {
        return flight;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public String getEndDate() {
        return endDate;
    }
}
