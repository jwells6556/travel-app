package com.justinwells.mytravelproject.Activities;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.justinwells.mytravelproject.CustomObjects.Airport;
import com.justinwells.mytravelproject.Misc.CurrentDate;
import com.justinwells.mytravelproject.CustomObjects.Flight;
import com.justinwells.mytravelproject.Singletons.FlightResultsSingleton;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.ApiHelperClasses.TravelApiHelper;
import com.justinwells.mytravelproject.Singletons.UserSettings;

import org.json.JSONException;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener{

    public static final String TAG = "i'm a tag!";
    TravelApiHelper travelHelper;
    GoogleApiClient googleApiClient;
    String text;
    Location userLocation;
    LocationRequest locationRequest;
    RelativeLayout loadingScreen;
    LinearLayout homeScreen;

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

        travelHelper = TravelApiHelper.getInstance();

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

        homeScreen = (LinearLayout) findViewById(R.id.home_screen);
        loadingScreen = (RelativeLayout) findViewById(R.id.loading_screen);
        Button directSearchButton = (Button) findViewById(R.id.button);
        Button randomSearchButton = (Button) findViewById(R.id.no_destination_search_button);
        final EditText editText = (EditText) findViewById(R.id.destination);
        Log.d(TAG, "onCreate: " + CurrentDate.isValidDate("yyyy-mm-dd"));



        randomSearchButton.setOnClickListener(new View.OnClickListener() {
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

                        if (!price.equals("") && isValidBudget(price)) {
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

        directSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = editText.getText().toString();

                AsyncTask<String,Void,Flight> search = new AsyncTask<String, Void, Flight>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        homeScreen.setVisibility(View.GONE);
                        loadingScreen.setVisibility(View.VISIBLE);
                    }

                    @Override
                    protected Flight doInBackground(String... strings) {
                        try {
                            String [] lat_lon =travelHelper.getLatLon(strings[0]);
                            Airport closestAirport = travelHelper.getClosestAirport(lat_lon[0], lat_lon[1]);
                            return travelHelper.getCheapestFlight(closestAirport);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Flight flight) {
                        super.onPostExecute(flight);
                        Intent goToDetail = new Intent(MainActivity.this,DetailActivity.class);
                        goToDetail.putExtra("id", "DirectSearch");
                        FlightResultsSingleton.getInstance().setDirectSearchFlight(flight);
                        homeScreen.setVisibility(View.VISIBLE);
                        loadingScreen.setVisibility(View.GONE);
                        startActivity(goToDetail);
                    }
                }.execute(text);
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

        try {
            startLocationUpdates();
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.d(TAG, "onConnected: failed " );
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended: " + i);
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

    public boolean isValidBudget (String input) {
        try {
            int num = Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void completeSearch (final Airport airport) {
        AsyncTask<Airport, Void, Flight> flightTask = new AsyncTask<Airport, Void, Flight>() {
            @Override
            protected Flight doInBackground(Airport... airports) {
                try {
                    return travelHelper.getCheapestFlight(airport);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Flight flight) {
                super.onPostExecute(flight);
                Intent goToDetail = new Intent(MainActivity.this,DetailActivity.class);
                goToDetail.putExtra("id", "DirectSearch");
                FlightResultsSingleton.getInstance().setDirectSearchFlight(flight);
                homeScreen.setVisibility(View.VISIBLE);
                loadingScreen.setVisibility(View.GONE);
                startActivity(goToDetail);
            }
        };
    }
}

