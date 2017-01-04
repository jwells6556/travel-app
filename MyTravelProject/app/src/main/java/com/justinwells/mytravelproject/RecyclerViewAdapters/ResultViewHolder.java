package com.justinwells.mytravelproject.RecyclerViewAdapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.justinwells.mytravelproject.R;

/**
 * Created by justinwells on 1/4/17.
 */

public class ResultViewHolder extends RecyclerView.ViewHolder {
    CardView mCard;
    TextView mResultPrice, mResultDestination, mResultDeparture, mResultReturn;

    public ResultViewHolder(View itemView) {
        super(itemView);
        mCard = (CardView) itemView.findViewById(R.id.result);
        mResultPrice = (TextView) itemView.findViewById(R.id.price);
        mResultDestination = (TextView) itemView.findViewById(R.id.destination);
        mResultDeparture = (TextView) itemView.findViewById(R.id.result_leave_date);
        mResultReturn = (TextView) itemView.findViewById(R.id.result_return_date);

    }
}
