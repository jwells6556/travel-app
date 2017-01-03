package com.justinwells.mytravelproject;

/**
 * Created by justinwells on 12/22/16.
 */

public class Hotel {
    String name, price;

    public Hotel(String name, String price) {
        this.name = name;
        this.price = price;
    }

    public String getHotelName() {
        return name;
    }

    public String getPrice() {
        return price;
    }
}
