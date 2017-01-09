package com.justinwells.mytravelproject.ApiHelperClasses;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.justinwells.mytravelproject.CustomObjects.Airport;
import com.justinwells.mytravelproject.CustomObjects.Flight;
import com.justinwells.mytravelproject.CustomObjects.Hotel;
import com.justinwells.mytravelproject.Misc.CurrentDate;
import com.justinwells.mytravelproject.Singletons.UserSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.justinwells.mytravelproject.Misc.AppConstants.LOCATION_CODE;
import static com.justinwells.mytravelproject.Misc.AppConstants.TRAVEL_CODE;

/**
 * Created by justinwells on 1/4/17.
 */

public class TravelApiHelper {
    private String TRAVEL_API_KEY;
    private String GOOGLE_API_KEY;
    public static final String TAG = "TravelApiHelper";
    private OkHttpClient mClient;
    static TravelApiHelper sInstance;

    private TravelApiHelper() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference travelRef = database.getReference(TRAVEL_CODE);
        DatabaseReference locationRef = database.getReference(LOCATION_CODE);

        travelRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TRAVEL_API_KEY = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: " + TRAVEL_API_KEY);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

        locationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GOOGLE_API_KEY = dataSnapshot.getValue(String.class);
                Log.d(TAG, "onDataChange: " + GOOGLE_API_KEY);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: ");
            }
        });

        mClient = new OkHttpClient();
    }

    public static TravelApiHelper getInstance () {
        if (sInstance == null) {
            sInstance = new TravelApiHelper();
        }

        return sInstance;
    }

    public Airport getClosestAirport (String latitude, String longitude) throws IOException, JSONException {
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

    public String [] getLatLon (String text) throws JSONException, IOException {
        Request request = new Request.Builder()
                .url("https://maps.googleapis.com/maps/api/geocode/json?key="
                        + GOOGLE_API_KEY
                        + "&address='"
                        + text + "'")
                .build();
        String getCoordinatesResponse = " ";
        Log.d(TAG, "getLatLon: " + GOOGLE_API_KEY);

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

    public Flight getCheapestFlight (Airport destination) throws IOException, JSONException {
        Request request = new Request.Builder()
                .url("http://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?"
                        +"origin=" + UserSettings.getInstance().getAirport()
                        +"&destination="+destination.getCode()
                        +"&departure_date=" + CurrentDate.idealFlightDate()
                        +"&return_date=" + CurrentDate.returnDate()
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
        String arriveOrigin = inboundFlight.getString("arrives_at").substring(0,10);


        return new Flight(price,departOrigin,arriveOrigin, getDestinationNameFromAirportCode(destination.getCode()));
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
                        +"&number_of_results=50"
                        +"&apikey=" +TRAVEL_API_KEY)
                .build();

        Response response = mClient.newCall(request).execute();
        String getCheapestHotelResponse = response.body().string();

        Log.d(TAG, "getCheapestHotel: " + getCheapestHotelResponse);
    }


    public List<Flight> getRandomFlight () throws IOException, JSONException {
        String baseUrl = "https://api.sandbox.amadeus.com/v1.2/flights/inspiration-search?";
        final Request request = new Request.Builder()
                .url(baseUrl
                        +"origin=" + UserSettings.getInstance().getAirport()
                        +"&max_price=" + UserSettings.getInstance().getPrice()
                        +"&apikey=" + TRAVEL_API_KEY)
                .build();
        Log.d(TAG, "getRandomFlight: " + TRAVEL_API_KEY);
        Log.d(TAG, "getRandomFlight: " + UserSettings.getInstance().getAirport());
        Response response = mClient.newCall(request).execute();
        String responseText = response.body().string();

        Log.d(TAG, "getRandomFlight: " + responseText);

        if (response.isSuccessful()) {
            Log.d(TAG, "getRandomFlight: success" + responseText);

            JSONObject jsonObject = new JSONObject(responseText);
            JSONArray resultsArray = jsonObject.getJSONArray("results");

            List<Flight>flightList = new ArrayList<>();
            int length = resultsArray.length();

            if (length > 20) {
                length = 20;
            }

            for (int i = 0; i < length; i++) {
                JSONObject flightObject = resultsArray.getJSONObject(i);
                Flight flight = new Flight(flightObject.getString("price"),
                                            flightObject.getString("departure_date"),
                                            flightObject.getString("return_date"),
                                            getDestinationNameFromAirportCode(flightObject.getString("destination")));
                flightList.add(flight);
            }

            return flightList;
        }

        return null;
    }

    public String getDestinationNameFromAirportCode (String code) throws IOException, JSONException {
        String url = "http://api.sandbox.amadeus.com/v1.2/location/" + code
                + "?apikey=" + TRAVEL_API_KEY;
        Request request = new Request.Builder()
                .url(url)
                .build();
        String responseText = mClient.newCall(request).execute().body().string();
        Log.d(TAG, "getDestinationNameFromAirportCode: " + responseText);
        JSONObject jsonObject = new JSONObject(responseText);
        JSONArray airportInfo = jsonObject.getJSONArray("airports");
        JSONObject cityInfo = airportInfo.getJSONObject(0);


        return cityInfo.getString("city_name");
    }

    public String getAirportCodeByName (String query) throws IOException, JSONException {
        String url = "http://api.sandbox.amadeus.com/v1.2/airports/autocomplete"
                +"?apikey=" + TRAVEL_API_KEY
                +"&term=" + query;
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            return null;
        }

        String responseText = response.body().string();
        Log.d(TAG, "getAirportCodeByName: " + responseText);
        JSONArray jsonArray = new JSONArray(responseText);

        return jsonArray.getJSONObject(0).getString("value");
    }

    public Hotel getHotel (String code, String checkIn, String checkOut) throws IOException, JSONException {
        String url = "https://api.sandbox.amadeus.com/v1.2/hotels/search-airport"
                    +"?apikey=" + TRAVEL_API_KEY
                    +"&location=" + code
                    +"&check_in=" + checkIn
                    +"&check_out=" + checkOut
                    +"&number_of_results=1";
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = mClient.newCall(request).execute();

        if(!response.isSuccessful()) {
            Log.d(TAG, "getHotel: " + response.body().string());
            return null;
        }

        String responseText = response.body().string();
        Log.d(TAG, "getHotel: " + responseText);

        JSONObject jsonObject = new JSONObject(responseText);

        JSONObject result = jsonObject.getJSONArray("results")
                                .getJSONObject(0);

        String hotelName = result.getString("property_name");
        String hotelPrice = result.getJSONObject("total_price").getString("amount");

        return new Hotel(hotelName, hotelPrice);

    }
}
