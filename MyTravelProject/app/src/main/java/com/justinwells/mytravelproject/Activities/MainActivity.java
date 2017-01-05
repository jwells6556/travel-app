package com.justinwells.mytravelproject.Activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.justinwells.mytravelproject.Airport;
import com.justinwells.mytravelproject.CurrentDate;
import com.justinwells.mytravelproject.Flight;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.TravelApiHelper;
import com.justinwells.mytravelproject.UserAirport;

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

import static com.justinwells.mytravelproject.AppConstants.PREFERRED_AIRPORT;
import static com.justinwells.mytravelproject.AppConstants.USER_SETTINGS;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{

    public static final String TAG = "i'm a tag!";
    TravelApiHelper travelHelper;
    GoogleApiClient googleApiClient;
    String text;
    Location userLocation;
    LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    138);
        }

        travelHelper = new TravelApiHelper();

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(60000);
        locationRequest.setFastestInterval(60000);

        Button button = (Button) findViewById(R.id.button);
        Button otherButton = (Button) findViewById(R.id.no_destination_search_button);
        final EditText editText = (EditText) findViewById(R.id.destination);
        Log.d(TAG, "onCreate: " + CurrentDate.isValidDate("yyyy-mm-dd"));



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


            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        //setUserLocation();
        try {
            startLocationUpdates();
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "onConnected: failed " );
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Cannot find user location", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        try {
            String closestAirportCode = travelHelper.getClosestAirport(String.valueOf(location.getLatitude())
                                        ,String.valueOf(location.getLongitude())).getCode();
            UserAirport.getInstance().setAirport(closestAirportCode);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "onLocationChanged: updated");
    }

    protected void startLocationUpdates() throws SecurityException{
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
    }

    public void setUserLocation() {
        final SharedPreferences sharedPreferences = getSharedPreferences(USER_SETTINGS,
                Context.MODE_PRIVATE);
        String airport = sharedPreferences.getString(PREFERRED_AIRPORT, null);
        Log.d(TAG, "setUserLocation: " + airport);
        ;
        if (airport != null) {
            UserAirport.getInstance().setAirport(airport);
            Toast.makeText(this, UserAirport.getInstance().getAirport(), Toast.LENGTH_SHORT).show();
        } else {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            Location location = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);

            Log.d(TAG, "setUserLocation: " + location.getLatitude());
            if (location != null) {
                AsyncTask<Location,Void,Airport> setDefaultAirport = new AsyncTask<Location, Void, Airport>() {
                    @Override
                    protected Airport doInBackground(Location... locations) {
                        String latitude = String.valueOf(locations[0].getLatitude());
                        String longitude = String.valueOf(locations[0].getLongitude());

                        try {
                            return travelHelper.getClosestAirport(latitude,longitude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Airport airport) {
                        super.onPostExecute(airport);
                        String airportCode;
                        if (airport == null) {
                            Toast.makeText(MainActivity.this, "Closest airport location failed", Toast.LENGTH_SHORT).show();
                            airportCode = "LAX";
                        } else {
                            airportCode = airport.getCode();
                        }

                        Log.d(TAG, "onPostExecute: " + airportCode);
                        UserAirport.getInstance().setAirport(airportCode);

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PREFERRED_AIRPORT, airportCode);
                        editor.commit();
                    }
                }.execute(location);
            }
        }

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
        };
    }
}

