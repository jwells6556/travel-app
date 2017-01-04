package com.justinwells.mytravelproject.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.justinwells.mytravelproject.Flight;
import com.justinwells.mytravelproject.FlightResultsSingleton;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.Weather;
import com.justinwells.mytravelproject.WeatherApiHelper;

import org.json.JSONException;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {
    TextView title, flightPrice, hotelName, hotelPrice, weatherView;
    String location;
    FlightResultsSingleton flightResultsSingleton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = (TextView) findViewById(R.id.destination_title);
        flightPrice = (TextView) findViewById(R.id.flight_pricing_view);
        hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelPrice = (TextView) findViewById(R.id.hotel_price);
        weatherView = (TextView) findViewById(R.id.weather);

        flightResultsSingleton = FlightResultsSingleton.getInstance();

        Flight flight = flightResultsSingleton.getFlight(getIntent().getIntExtra("pos",-1));
        location = flight.getDestination();
        title.setText(location);

        setWeather();
    }

    public void setWeather () {
        final WeatherApiHelper helper = new WeatherApiHelper(location);
        AsyncTask<Void,Void,Weather> weatherAsyncTask = new AsyncTask<Void, Void, Weather>() {
            @Override
            protected Weather doInBackground(Void... voids) {
                try {
                    return helper.getWeather();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Weather weather) {
                super.onPostExecute(weather);
                weatherView.setText(weather.getDescription() + " "
                                    +weather.getTemperature());
            }
        }.execute();
    }
}
