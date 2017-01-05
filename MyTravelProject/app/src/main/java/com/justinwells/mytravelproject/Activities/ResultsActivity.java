package com.justinwells.mytravelproject.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justinwells.mytravelproject.Flight;
import com.justinwells.mytravelproject.FlightResultsSingleton;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.RecyclerViewAdapters.ResultRecyclerViewAdapter;
import com.justinwells.mytravelproject.TravelApiHelper;
import com.justinwells.mytravelproject.UserSettings;

import java.io.IOException;
import java.util.List;

import static java.security.AccessController.getContext;

public class ResultsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private ResultRecyclerViewAdapter mAdapter;
    LinearLayout loadingScreen;
    TextView loadingText;
    private List<Flight> mFlightList;
    FlightResultsSingleton flightResultsSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setUpViews();
        flightResultsSingleton = FlightResultsSingleton.getInstance();
        populateList();

    }

    private void populateList () {
        AsyncTask <Void,Void,List<Flight>> fillList = new AsyncTask<Void, Void, List<Flight>>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingScreen.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.INVISIBLE);
                loadingText.setText("Searching for flights costings under $"
                                    + UserSettings.getInstance().getPrice()
                                    + ".00 departing from "
                                    + UserSettings.getInstance().getAirport());
            }

            @Override
            protected List<Flight> doInBackground(Void... voids) {
                TravelApiHelper helper = new TravelApiHelper();
                List <Flight> list;
                try {
                    return helper.getRandomFlight();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Flight> flights) {
                loadingScreen.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                mFlightList = flights;

                flightResultsSingleton.newList(mFlightList);

                if (!(mFlightList==null)) {
                    setUpRecyclerView(mFlightList);
                } else {
                    Toast.makeText(getApplicationContext(), "No results found, try expanding search criteria", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }

    private void setUpRecyclerView (List<Flight> list) {
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new ResultRecyclerViewAdapter(list);

        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void setUpViews () {
        loadingText = (TextView) findViewById(R.id.search_criteria);
        mRecyclerView = (RecyclerView) findViewById(R.id.flight_search_results);
        loadingScreen = (LinearLayout) findViewById(R.id.random_results_loading);
    }
}
