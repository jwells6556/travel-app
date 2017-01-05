package com.justinwells.mytravelproject.Activities;

import android.Manifest;
import android.app.Dialog;
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
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.TravelApiHelper;
import com.justinwells.mytravelproject.UserSettings;

import org.json.JSONException;

import java.io.IOException;

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
                final Dialog randomSearchParameterDialog = new Dialog(MainActivity.this);
                randomSearchParameterDialog.setContentView(R.layout.random_flight_search_parameter_dialog);

                final EditText maxPrice = (EditText) randomSearchParameterDialog.findViewById(R.id.random_search_budget);
                final EditText originAirport = (EditText) randomSearchParameterDialog.findViewById(R.id.random_search_departure);
                Button searchButton = (Button) randomSearchParameterDialog.findViewById(R.id.continue_random_search);
                Button cancelButton = (Button) randomSearchParameterDialog.findViewById(R.id.cancel_random_search);

                maxPrice.setHint("Enter Budget (Currently $" + UserSettings.getInstance().getPrice()+")");
                originAirport.setHint("Enter Origin (Currently " + UserSettings.getInstance().getAirport() + ")");

                searchButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String price = maxPrice.getText().toString();
                        final String origin = originAirport.getText().toString();

                        if (!price.equals("")) {
                            UserSettings.getInstance().setPrice(Integer.valueOf(maxPrice.getText().toString()));
                        }

                        if (!origin.equals("")) {
                            AsyncTask<String, Void, String> task = new AsyncTask<String, Void, String>() {
                                @Override
                                protected String doInBackground(String... strings) {
                                    try {
                                        return travelHelper.getAirportCodeByName(strings[0]);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    return null;
                                }

                                @Override
                                protected void onPostExecute(String s) {
                                    super.onPostExecute(s);
                                    if (s!=null) {
                                        Log.d(TAG, "onPostExecute: " + s);
                                        UserSettings.getInstance().setAirport(s);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Invalid Origin", Toast.LENGTH_SHORT).show();
                                    }
                                    randomSearchParameterDialog.dismiss();
                                    Intent getRandomResults = new Intent(MainActivity.this, ResultsActivity.class);
                                    startActivity(getRandomResults);
                                }
                            }.execute(origin);

                        } else {

                            randomSearchParameterDialog.dismiss();
                            Intent getRandomResults = new Intent(MainActivity.this, ResultsActivity.class);
                            startActivity(getRandomResults);
                        }
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        randomSearchParameterDialog.dismiss();
                    }
                });

                randomSearchParameterDialog.show();
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
       AsyncTask<Location, Void, String> task = new AsyncTask<Location, Void, String>() {
           @Override
           protected String doInBackground(Location... locations) {
               userLocation = locations[0];
               try {
                   return travelHelper.getClosestAirport(String.valueOf(userLocation.getLatitude())
                           ,String.valueOf(userLocation.getLongitude())).getCode();
               } catch (IOException e) {
                   e.printStackTrace();
               } catch (JSONException e) {
                   e.printStackTrace();
               }

               return null;
           }

           @Override
           protected void onPostExecute(String s) {
               super.onPostExecute(s);
               UserSettings.getInstance().setAirport(s);
           }
       }.execute(location);
        Log.d(TAG, "onLocationChanged: updated");
    }

    protected void startLocationUpdates() throws SecurityException{
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, this);
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

