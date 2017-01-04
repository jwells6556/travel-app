package com.justinwells.mytravelproject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by justinwells on 1/4/17.
 */

public class WeatherApiHelper {
    public static final String WEATHER_API_KEY = "b0225e11e3a02c48832f2be025e06b98";
    public String city;
    private OkHttpClient mClient;

    public WeatherApiHelper(String city) {
        this.city = city;
        mClient = new OkHttpClient();
    }

    public Weather getWeather () throws IOException, JSONException {
        String url = "http://api.openweathermap.org/data/2.5/weather?q="
                    + city
                    +"&apikey=" + WEATHER_API_KEY
                    +"&units=imperial";

        Request request = new Request.Builder()
                .url(url)
                .build();

        String responseText = mClient.newCall(request).execute().body().string();

        JSONObject jsonObject = new JSONObject(responseText);

        String weather = jsonObject.getJSONArray("weather")
                                .getJSONObject(0)
                                .getString("main");

        int temperature = (int) jsonObject.getJSONObject("main")
                                        .getInt("temp");


        return new Weather(temperature, weather);

    }
}
