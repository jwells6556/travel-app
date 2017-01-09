package com.justinwells.mytravelproject.Activities;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.justinwells.mytravelproject.CustomObjects.Flight;
import com.justinwells.mytravelproject.Singletons.FlightResultsSingleton;
import com.justinwells.mytravelproject.R;
import com.justinwells.mytravelproject.RecyclerViewAdapters.ResultRecyclerViewAdapter;
import com.justinwells.mytravelproject.ApiHelperClasses.TravelApiHelper;
import com.justinwells.mytravelproject.Singletons.UserSettings;

import java.util.List;

public class ResultsActivity extends AppCompatActivity {
    private static final String TAG = "ResultsActivity";
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpViews();
        FlightResultsSingleton.getInstance().reset();
        flightResultsSingleton = FlightResultsSingleton.getInstance();
        populateList();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flightResultsSingleton.isCallComplete()) {
            mAdapter.replaceList(flightResultsSingleton.getFlightResultsList());
            mAdapter.notifyDataSetChanged();
        }
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
                Log.d(TAG, "doInBackground: call made again" );
                TravelApiHelper helper = TravelApiHelper.getInstance();
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
                flightResultsSingleton.setCallComplete(true);
                if (!(mFlightList==null)) {
                    setUpRecyclerView(mFlightList);
                } else {
                    Toast.makeText(getApplicationContext(), "No results found, try expanding search criteria", Toast.LENGTH_SHORT).show();
                }
            }
        };

        if (!flightResultsSingleton.isCallComplete()) {
            fillList.execute();
        }
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
