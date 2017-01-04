package com.justinwells.mytravelproject;

/**
 * Created by justinwells on 1/4/17.
 */

public class Weather {
    int temperature;
    String description;

    public Weather(int temperature, String description) {
        this.temperature = temperature;
        this.description = description;
    }

    public int getTemperature() {
        return temperature;
    }

    public String getDescription() {
        return description;
    }
}
