package com.justinwells.mytravelproject;

import java.io.Serializable;

/**
 * Created by justinwells on 12/22/16.
 */

public class Flight {
    private String price, departOrigin, arriveDestination, departDestination, arriveOrigin, destination;


    public Flight(String price, String departDate, String returnDate, String destination) {
        this.price = "$" + price;
        departOrigin = departDate;
        arriveOrigin = returnDate;
        this.destination = destination;

    }

    public String getPrice() {
        return price;
    }

    public String getDepartOrigin() { return departOrigin; }

    public String getArriveDestination() {
        return arriveDestination;
    }

    public String getDepartDestination() {
        return departDestination;
    }

    public String getArriveOrigin() {
        return arriveOrigin;
    }

    public String getDestination() { return destination; }
}
