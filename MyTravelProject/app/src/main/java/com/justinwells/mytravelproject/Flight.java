package com.justinwells.mytravelproject;

/**
 * Created by justinwells on 12/22/16.
 */

public class Flight {
    private String price, departOrigin, arriveDestination, departDestination, arriveOrigin;

    public Flight(String price, String departOrigin, String arriveDestination, String departDestination, String arriveOrigin) {
        this.price = price;
        this.departOrigin = departOrigin;
        this.arriveDestination = arriveDestination;
        this.departDestination = departDestination;
        this.arriveOrigin = arriveOrigin;
    }

    public String getPrice() {
        return price;
    }

    public String getDepartOrigin() {
        return departOrigin;
    }

    public String getArriveDestination() {
        return arriveDestination;
    }

    public String getDepartDestination() {
        return departDestination;
    }

    public String getArriveOrigin() {
        return arriveOrigin;
    }
}
