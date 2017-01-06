package com.justinwells.mytravelproject.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.justinwells.mytravelproject.CustomObjects.Flight;
import com.justinwells.mytravelproject.Singletons.FlightResultsSingleton;
import com.justinwells.mytravelproject.CustomObjects.Hotel;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.ApiHelperClasses.TravelApiHelper;
import com.justinwells.mytravelproject.CustomObjects.Weather;
import com.justinwells.mytravelproject.ApiHelperClasses.WeatherApiHelper;
import com.justinwells.mytravelproject.Singletons.UserSettings;

import org.json.JSONException;

import java.io.IOException;

public class DetailActivity extends AppCompatActivity {
    TextView title, flightPrice, departingFrom, departingOn,
            hotelName, hotelPrice, weatherView, tempView, totalPriceView;
    String location;
    FlightResultsSingleton flightResultsSingleton;
    Flight flight;
    int totalPrice, airFare, hotelFare;
    TravelApiHelper travelHelper;
    public static final String TAG = "DetailActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        title = (TextView) findViewById(R.id.destination_title);
        flightPrice = (TextView) findViewById(R.id.flight_pricing_view);
        departingFrom = (TextView) findViewById(R.id.departing_from);
        departingOn = (TextView) findViewById(R.id.departing_on);
        hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelPrice = (TextView) findViewById(R.id.hotel_price);
        weatherView = (TextView) findViewById(R.id.weather);
        tempView = (TextView) findViewById(R.id.temperature);
        totalPriceView = (TextView) findViewById(R.id.total_vacation_price);

        flightResultsSingleton = FlightResultsSingleton.getInstance();
        travelHelper = TravelApiHelper.getInstance();

        flight = getFlight();

        Log.d(TAG, "onCreate: " + flight);

        if (flight==null) {
            finish();
            Toast.makeText(this, "Something went wrong, try modifying search", Toast.LENGTH_SHORT).show();
        } else {
            location = flight.getDestination();
            title.setText(location);

            setWeather();
            setHotelInfo();
            setFlightInfo();

        }
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
                if (hotel != null) {
                    hotelName.setText(hotel.getHotelName());
                    hotelPrice.setText(hotel.getPrice());
                    hotelFare = Math.round(Float.valueOf(hotel.getPrice()));
                } else {
                    hotelName.setText("Couldn't find hotel");
                    hotelFare = 0;
                }
                setTotalPrice();
            }
        }.execute(flight);
    }

    public void setFlightInfo () {
        flightPrice.setText(flight.getPrice());
        departingFrom.setText(UserSettings.getInstance().getAirport());
        departingOn.setText(flight.getDepartOrigin());
        airFare = Math.round(Float.valueOf(flight.getPrice().substring(1)));
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
                if (weather == null) {
                    weatherView.setText("Failed to load weather");
                } else {
                    weatherView.setText(weather.getDescription());
                    tempView.setText(weather.getTemperature() + " \u2109" );
                }
            }
        }.execute();
    }

    public Flight getFlight () {
        String originActivity = getIntent().getStringExtra("id");
        Flight workingFlight;

        if (originActivity.equals("RandomSearch")) {
            workingFlight = flightResultsSingleton.getFlight(getIntent().getIntExtra("pos",-1));
        } else {
            workingFlight = flightResultsSingleton.getDirectSearchFlight();
        }

        if (workingFlight == null) {
            finish();
            Log.d(TAG, "getFlight: finished");
        }
        return workingFlight;
    }

    public void setTotalPrice () {
        totalPrice = airFare + hotelFare;
        String price = totalPriceView.getText().toString() + totalPrice;
        totalPriceView.setText(price);
    }
}
