package com.justinwells.mytravelproject.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.justinwells.mytravelproject.CurrentDate;
import com.justinwells.mytravelproject.Flight;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.TravelApiHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    public static final String TRAVEL_API_KEY = "KMi5Y1K7oFM62kCGOzteHSV8AWL989Id";
    public static final String GOOGLE_API_KEY = "AIzaSyCQEjFv_ANAwNvxFvTCy97VK8PCkhM0hmw";
    public static final String TAG = "i'm a tag!";
    private OkHttpClient mClient;
    private String text, departureDate, returnDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.button);
        Button otherButton = (Button) findViewById(R.id.no_destination_search_button);
        final EditText editText = (EditText) findViewById(R.id.destination);
        Log.d(TAG, "onCreate: " + CurrentDate.isValidDate("yyyy-mm-dd"));
        //Log.d(TAG, "onCreate: "+CurrentDate.isValidDate("2015-12-22"));
        //Log.d(TAG, "onCreate: "+CurrentDate.isValidDate("2016-12-23"));
        //Log.d(TAG, "onCreate: "+CurrentDate.isValidDate("2018-13-06"));
        mClient = new OkHttpClient();

        otherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getRandomResults = new Intent(MainActivity.this, ResultsActivity.class);
                startActivity(getRandomResults);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = editText.getText().toString();

                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {

                        try {
                            /*
                            String [] lat_lon = getLatLon(text);

                            URL url = new URL("http://api.sandbox.amadeus.com/");
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setReadTimeout(30000);
                            urlConnection.setConnectTimeout(30000);

                            Airport closestAirport = getClosestAirport(lat_lon[0], lat_lon[1]);
                            Flight cheapestFlight = getCheapestFlight(closestAirport);
                            Flight randomFlight = getRandomFlight();
                            getCheapestHotel(lat_lon[0],lat_lon[1]);

                            Log.d(TAG, "doInBackground: " +cheapestFlight.getArriveDestination());
                            */
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(String s) {
                        super.onPostExecute(s);
                        try {

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.execute();
             }
        });
    }




}

