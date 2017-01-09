package com.justinwells.mytravelproject.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.justinwells.mytravelproject.CustomObjects.Flight;
import com.justinwells.mytravelproject.Singletons.DetailDataHolder;
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
    TextView title, flightPrice, departingFrom, departingOn, returningOn,
            hotelName, hotelPrice, weatherView, tempView, totalPriceView;
    ProgressBar loadingBar;
    LinearLayout mainScreen;
    String location;
    FlightResultsSingleton flightResultsSingleton;
    Flight flight;
    int totalPrice, airFare, hotelFare;
    TravelApiHelper travelHelper;
    public static final String TAG = "DetailActivity";
    DetailDataHolder holder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        findViews();

        flightResultsSingleton = FlightResultsSingleton.getInstance();
        travelHelper = TravelApiHelper.getInstance();
        holder = holder.getInstance();
        flight = getFlight();

        Log.d(TAG, "onCreate: " + flight);

        if (flight==null) {
            Toast.makeText(this, "No results found try modifying search", Toast.LENGTH_SHORT).show();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            holder.destroy();
            finish();
        } else {
            location = flight.getDestination();
            title.setText(location);


            setFlightInfo();

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!holder.getCallsDone()) {
            Log.d(TAG, "onStart: check failed" );
            getWeather();
            getHotelInfo();
        } else {
            setDataToScreen();
        }

        loadingBar.setVisibility(View.GONE);
        mainScreen.setVisibility(View.VISIBLE);
    }

    public void getHotelInfo () {
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
                holder.setHotel(hotel);
                holder.setHotelDone(true);
                setHotel();
            }
        }.execute(flight);
    }

    public void setFlightInfo () {
        flightPrice.setText(flight.getPrice());
        departingFrom.setText(UserSettings.getInstance().getAirport());
        departingOn.setText(flight.getDepartOrigin());
        returningOn.setText(flight.getArriveOrigin());
        airFare = Math.round(Float.valueOf(flight.getPrice().substring(1)));
    }

    public void getWeather () {
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
                holder.setWeather(weather);
                holder.setWeatherDone(true);
                setWeather();
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

    public void findViews () {
        title = (TextView) findViewById(R.id.destination_title);
        flightPrice = (TextView) findViewById(R.id.flight_pricing_view);
        departingFrom = (TextView) findViewById(R.id.departing_from);
        departingOn = (TextView) findViewById(R.id.departing_on);
        returningOn = (TextView) findViewById(R.id.returning_on);
        hotelName = (TextView) findViewById(R.id.hotel_name);
        hotelPrice = (TextView) findViewById(R.id.hotel_price);
        weatherView = (TextView) findViewById(R.id.weather);
        tempView = (TextView) findViewById(R.id.temperature);
        totalPriceView = (TextView) findViewById(R.id.total_vacation_price);
        loadingBar = (ProgressBar) findViewById(R.id.loading_indicator);
        mainScreen = (LinearLayout) findViewById(R.id.activity_detail);
        mainScreen.setVisibility(View.GONE);
        loadingBar.setVisibility(View.VISIBLE);
    }

    public void setWeather () {
        Weather weather = DetailDataHolder.getInstance().getWeather();
        if (weather == null) {
            weatherView.setText("Failed to load weather");
        } else {
            weatherView.setText(weather.getDescription());
            tempView.setText(weather.getTemperature() + " \u2109" );
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        holder.destroy();
    }

    public void setHotel () {
        Hotel hotel = holder.getHotel();
        if (hotel != null) {
            hotelName.setText(hotel.getHotelName());
            hotelPrice.setText(hotel.getPrice());
            hotelFare = Math.round(Float.valueOf(hotel.getPrice()));
        } else {
            hotelName.setText("No Hotels Found");
            hotelFare = 0;
        }
        setTotalPrice();
    }

    public void setDataToScreen () {
        setHotel();
        setFlightInfo();
        setWeather();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
