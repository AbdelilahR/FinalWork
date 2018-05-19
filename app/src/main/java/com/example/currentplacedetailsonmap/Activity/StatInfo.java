package com.example.currentplacedetailsonmap.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.Model.Statistiek;
import com.example.currentplacedetailsonmap.Model.Utility;
import com.example.currentplacedetailsonmap.R;

public class StatInfo extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat_info);

        //initialize textviews
        TextView sessionId = (TextView) this.findViewById(R.id.sessionid_value);
        TextView duration = (TextView) this.findViewById(R.id.duration_value);
        TextView burnedCalories = (TextView) this.findViewById(R.id.calories_value);
        TextView distance = (TextView) this.findViewById(R.id.distance_value);
        TextView date = (TextView) this.findViewById(R.id.date_value);

        //Retrieve selected stat from intent
        Statistiek selectedStat = (Statistiek) getIntent().getExtras().getSerializable("selectedStat");

        //initialize variables
        String id = String.valueOf(selectedStat.getId());
        String time = Utility.getDate(selectedStat.getTime(), "HH:mm:ss");
        String burnedCal = String.valueOf(selectedStat.getBurnedCalories());
        String dist = String.valueOf(selectedStat.getDistanceInMeters());
        String dates = selectedStat.getDate();

        //Set text for all textviews
        sessionId.setText(id);
        duration.setText(time);
        burnedCalories.setText(burnedCal);
        distance.setText(dist);
        date.setText(dates);

    }
}
