package com.justinwells.mytravelproject;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
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
        final EditText editText = (EditText) findViewById(R.id.destination);
        Log.d(TAG, "onCreate: " +CurrentDate.isValidDate("yyyy-mm-dd"));
        //Log.d(TAG, "onCreate: "+CurrentDate.isValidDate("2015-12-22"));
        //Log.d(TAG, "onCreate: "+CurrentDate.isValidDate("2016-12-23"));
        //Log.d(TAG, "onCreate: "+CurrentDate.isValidDate("2018-13-06"));
        mClient = new OkHttpClient();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = editText.getText().toString();

                AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {

                        try {
                            String [] lat_lon = getLatLon(text);

                            URL url = new URL("http://api.sandbox.amadeus.com/");
                            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setReadTimeout(300000);
                            urlConnection.setConnectTimeout(300000);


                            getHotelByAirport(null);
                            Airport closestAirport = getClosestAirport(lat_lon[0], lat_lon[1]);
                            Flight cheapestFlight = getCheapestFlight(closestAirport);
                            Flight randomFlight = getRandomFlight();
                            getCheapestHotel(lat_lon[0],lat_lon[1]);

                            Log.d(TAG, "doInBackground: " +cheapestFlight.getArriveDestination());

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

    public Airport getClosestAirport (String latitude, String longitude) throws IOException, JSONException, SocketTimeoutException {
        Request request2 = new Request.Builder()
                .url("http://api.sandbox.amadeus.com/v1.2/airports/nearest-relevant?latitude="
                        + latitude
                        + "&longitude="
                        + longitude
                        + "&apikey="
                        + TRAVEL_API_KEY)
                .build();

        Response response2 = mClient.newCall(request2).execute();


        String nearestAirPortResponse = response2.body().string();

        JSONArray jsonArray = new JSONArray(nearestAirPortResponse);
        JSONObject airportObject = jsonArray.getJSONObject(0);

        String airportCode = airportObject.getString("airport");
        String airportCity = airportObject.getString("city_name");
        String airportName = airportObject.getString("airport_name");

        return new Airport(airportCode,airportCity,airportName);
    }

    public String [] getLatLon (String location) throws JSONException, IOException, SocketTimeoutException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?key="
                        + GOOGLE_API_KEY
                        + "&address='"
                        + text + "'")
                .build();
        String getCoordinatesResponse = " ";

        Response response = mClient.newCall(request).execute();

        getCoordinatesResponse = response.body().string();
        Log.d("TAG", "doInBackground: " + getCoordinatesResponse);

        JSONObject json = new JSONObject(getCoordinatesResponse);
        JSONArray results = json.getJSONArray("results");
        JSONObject resultObject = results.getJSONObject(0);
        JSONObject geometry = resultObject.getJSONObject("geometry");
        JSONObject locationInfo = geometry.getJSONObject("location");

        String [] lat_lon = new String [2];

        lat_lon [0] = locationInfo.getString("lat");
        lat_lon [1] = locationInfo.getString("lng");

        return lat_lon;
    }

    public Flight getCheapestFlight (Airport destination) throws IOException, JSONException, SocketTimeoutException {
        Request request = new Request.Builder()
                .url("http://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?"
                        +"origin=" +"JFK"
                        +"&destination="+destination.getCode()
                        +"&departure_date=2017-10-15"
                        +"&return_date=2017-10-21"
                        +"&number_of_results=1"
                        +"&apikey="+TRAVEL_API_KEY)
                .build();

        Response response = mClient.newCall(request).execute();
        String getCheapestFlightResponse = response.body().string();

        Log.d(TAG, "getCheapestFlight: "+ getCheapestFlightResponse);

        JSONObject json = new JSONObject(getCheapestFlightResponse);
        JSONArray results = json.getJSONArray("results");
        JSONObject json2 = results.getJSONObject(0);
        JSONArray itineraries = json2.getJSONArray("itineraries");
        JSONObject itinerary = itineraries.getJSONObject(0);

        JSONObject outboundFlight = itinerary.getJSONObject("outbound")
                                                .getJSONArray("flights")
                                                .getJSONObject(0);
        JSONObject inboundFlight = itinerary.getJSONObject("inbound")
                                                .getJSONArray("flights")
                                                .getJSONObject(0);

        String price = json2.getJSONObject("fare")
                                .getString("total_price");

        String departOrigin = outboundFlight.getString("departs_at").substring(0,10);
        String arriveDestination = outboundFlight.getString("arrives_at").substring(0,10);
        String departDestination = inboundFlight.getString("departs_at").substring(0,10);
        String arriveOrigin = inboundFlight.getString("arrives_at").substring(0,10);


        return new Flight(price,departOrigin,arriveDestination,departDestination,arriveOrigin);
    }

    public void getCheapestHotel (String latitude, String longitude) throws IOException {
        Log.d(TAG, "getCheapestHotel: " + longitude);
        Request request = new Request.Builder()
                .url("http://api.sandbox.amadeus.com/v1.2/hotels/search-circle?"
                        +"latitude=" + latitude
                        +"&longitude=" + longitude
                        +"radius=50"
                        +"&check_in=2015-09-01"
                        +"&check_out=2015-09-03"
                        +"&number_of_results=2"
                        +"&apikey=" +TRAVEL_API_KEY)
                .build();

        Response response = mClient.newCall(request).execute();
        String getCheapestHotelResponse = response.body().string();

        Log.d(TAG, "getCheapestHotel: " + getCheapestHotelResponse);
    }

    public void getHotelByAirport (Airport airport) throws IOException, SocketTimeoutException, JSONException {
        Request request = new Request.Builder()
                .url("https://api.sandbox.amadeus.com/v1.2/hotels/search-airport?"
                        +"apikey=" + TRAVEL_API_KEY
                        +"&location=" + "LAX"
                        +"&check_in=2017-03-14"
                        +"&check_out=2017-03-16"
                        +"&number_of_results=2")
                .build();
        Response response = mClient.newCall(request).execute();
        String responseText = response.body().string();
        Log.d(TAG, "getHotelByAirport: " + responseText);

        JSONObject json = new JSONObject(responseText);




    }

    public String convertToDecimalDegrees (String latOrLon) {

        return null;
    }

    public Flight getRandomFlight () throws IOException, SocketTimeoutException {
        String baseUrl = "https://api.sandbox.amadeus.com/v1.2/flights/inspiration-search?";
        final Request request = new Request.Builder()
                .url(baseUrl
                    +"origin=" + "JFK"
                    +"&max_price=500"
                    +"&apikey=" + TRAVEL_API_KEY)
                .build();

        Response response = mClient.newCall(request).execute();
        String responseText = response.body().string();


        if (response.isSuccessful()) {
            Log.d(TAG, "getRandomFlight: " + responseText);
        }

        return null;
    }


}

