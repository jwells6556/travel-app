package com.justinwells.mytravelproject.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.justinwells.mytravelproject.CustomObjects.Flight;
import com.justinwells.mytravelproject.Singletons.FlightResultsSingleton;
import com.justinwells.mytravelproject.CustomObjects.Hotel;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.ApiHelperClasses.TravelApiHelper;
import com.justinwells.mytravelproject.CustomObjects.Weather;
import com.justinwells.mytravelproject.ApiHelperClasses.WeatherApiHelper;

import org.json.JSONException;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {
    TextView title, flightPrice, hotelName, hotelPrice, weatherView;
    String location;
    FlightResultsSingleton flightResultsSingleton;
    Flight flight;
    int totalPrice, airFare, hotelFare;
    TravelApiHelper travelHelper;

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
        travelHelper = new TravelApiHelper();

        flight = getFlight();
        location = flight.getDestination();
        title.setText(location);

        setWeather();
        setHotelInfo();
        setFlightInfo();
        setTotalPrice();
    }

    public void setHotelInfo () {
        AsyncTask<Flight,Void,Hotel> getHotel = new AsyncTask<Flight, Void, Hotel>() {
            @Override
            protected Hotel doInBackground(Flight... flights) {
                try {
                    String code = travelHelper.getAirportCodeByName(flights[0].getDestination());
                    return travelHelper.getHotel(code,flights[0].getDepartOrigin(),flights[0].getArriveOrigin());
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Hotel hotel) {
                super.onPostExecute(hotel);
                hotelName.setText(hotel.getHotelName());
                hotelPrice.setText(hotel.getPrice());
                hotelFare = Integer.valueOf(hotel.getPrice());
            }
        }.execute(flight);
    }

    public void setFlightInfo () {
        flightPrice.setText(flight.getPrice());
        airFare = Integer.valueOf(flight.getPrice());
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

    public Flight getFlight () {
        String originActivity = getIntent().getStringExtra("id");

        if (originActivity.equals("RandomSearch")) {
            return flightResultsSingleton.getFlight(getIntent().getIntExtra("pos",-1));
        }

        return flightResultsSingleton.getDirectSearchFlight();
    }

    public void setTotalPrice () {
        totalPrice = airFare + hotelFare;
    }
}
