package com.justinwells.mytravelproject.RecyclerViewAdapters;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.justinwells.mytravelproject.Activities.DetailActivity;
import com.justinwells.mytravelproject.Flight;
import com.justinwells.mytravelproject.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by justinwells on 1/4/17.
 */
public class ResultRecyclerViewAdapter extends RecyclerView.Adapter<ResultViewHolder> {
    List<Flight> flightList = new ArrayList<>();

    public ResultRecyclerViewAdapter(List<Flight> flightList) {
        this.flightList = flightList;
    }

    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View parentView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_result_viewholder, parent, false);
        ResultViewHolder viewHolder = new ResultViewHolder(parentView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        final Flight thisFlight = flightList.get(position);

        holder.mResultDestination.setText(thisFlight.getDestination());
        holder.mResultDeparture.setText(thisFlight.getDepartOrigin());
        holder.mResultReturn.setText(thisFlight.getArriveOrigin());
        holder.mResultPrice.setText(thisFlight.getPrice());

        holder.mCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToDetail = new Intent(view.getContext(), DetailActivity.class);
                goToDetail.putExtra("flight", thisFlight);
                view.getContext().startActivity(goToDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (flightList.isEmpty()) {
            return 0;
        }

        return flightList.size();
    }
}
