package com.example.currentplacedetailsonmap.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.Model.Statistiek;
import com.example.currentplacedetailsonmap.Model.Utility;
import com.example.currentplacedetailsonmap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * https://stackoverflow.com/questions/7916834/adding-listview-sub-item-text-in-android
 */
public class StatsFragment extends Fragment
{
    //Data from Firebase
    private StatsFragment mAuth;
    private FirebaseAuth auth;
    private String current_user;
    private DatabaseReference database;
    private List<Map<String, String>> stat_map;
    private ArrayList<Integer> statlist_time;
    private ArrayList<Integer> statlist_calorie;
    private ArrayList<Float> statlist_distance;
    //Adapter
    private SimpleAdapter simpleAdapter;
    //ListView
    private ListView statListView;
    //Context to solve nullpointerException
    private Context mContext;
    // stats average
    private double avg_time;
    private double avg_calorie;
    private float avg_ditance;
    //Textviews
    private TextView txt_avg_time;
    private TextView txt_avg_cal;
    private TextView txt_avg_distance;
    // item options
    private boolean all_session = true;
    private boolean today_session = false;
    private boolean yesterday_session = false;
    private boolean lastWeek_session = false;

    public StatsFragment()
    {

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.filter_stat, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.all_session)
        {

            list_settings(true,false,false,false);
            initializeLists();
            loadSessionList();
            item.setChecked(true);
            return true;
        }
        if (item.getItemId() == R.id.today_session)
        {
            list_settings(false,true,false,false);
            initializeLists();
            loadSessionList();
            item.setChecked(true);
            return true;
        }
        if (item.getItemId() == R.id.yesterday_session)
        {
            list_settings(false,false,true,false);
            initializeLists();
            loadSessionList();
            item.setChecked(true);
            return true;
        }
        if (item.getItemId() == R.id.lastWeek_session)
        {
            list_settings(false,false,false,true);
            initializeLists();
            loadSessionList();
            item.setChecked(true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mContext = getActivity();
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        txt_avg_time = (TextView) view.findViewById(R.id.value_avg_time);
        txt_avg_cal = (TextView) view.findViewById(R.id.value_avg_cal);
        txt_avg_distance = (TextView) view.findViewById(R.id.value_avg_distance);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        statListView = (ListView) view.findViewById(R.id.stat_listView);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        //initialize data
        auth = FirebaseAuth.getInstance();
        current_user = auth.getUid();
        database = FirebaseDatabase.getInstance().getReference().child("Stats").child(current_user);
        // initialize all lists
        initializeLists();
        //put data in the lists
        loadSessionList();
        setHasOptionsMenu(true);
    }

    /**
     * Initialize all the lists
     */
    private void initializeLists()
    {
        stat_map = new ArrayList<>();
        statlist_time = new ArrayList<>();
        statlist_calorie = new ArrayList<>();
        statlist_distance = new ArrayList<>();

    }

    /**
     * source --> https://stackoverflow.com/questions/10791568/calculating-average-of-an-array-list
     *
     * @param marks
     * @return
     */
    private double calculateAverage_integer(List<Integer> marks)
    {
        Integer sum = 0;
        if (!marks.isEmpty())
        {
            for (Integer mark : marks)
            {
                sum += mark;
            }
            return sum.doubleValue() / marks.size();
        }
        return sum;
    }

    private float calculateAverag_float(List<Float> marks)
    {
        Float sum = 0.0f;
        if (!marks.isEmpty())
        {
            for (Float mark : marks)
            {
                sum += mark;
            }
            return sum.floatValue() / marks.size();
        }
        return sum;
    }

    /**
     * Check all the list of statistics
     * @return false if the lists are null or empty
     */
    private boolean check_Statslists()
    {
        if (statlist_distance == null && !statlist_distance.isEmpty()
                && statlist_time == null && !statlist_time.isEmpty()
                && statlist_calorie == null && !statlist_calorie.isEmpty())
            return false;
        else
            return true;
    }

    /**
     * Load a list for all the statistics of the user async
     */
    private void loadSessionList()
    {
        class loadSessionListAsync extends AsyncTask<String, Void, String>
        {

            @Override
            protected String doInBackground(String... strings)
            {
                database.addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {
                        Statistiek all_Stats = dataSnapshot.getValue(Statistiek.class);
                        String title = all_Stats.getName() + " " + all_Stats.getId();

                        Map<String, String> data = new HashMap<>();
                        data.put("title", title);
                        data.put("date", all_Stats.getDate());
                        Date date = Utility.convertStringTo_Date(all_Stats.getDate());

                        //Check the settings of the user and put the correct data in the lists
                        if (all_session)
                        {
                            stat_map.add(data);
                            fill_statLists(all_Stats);
                        } else if (today_session)
                        {
                            if (DateUtils.isToday(date.getTime()))
                            {
                                stat_map.add(data);
                                fill_statLists(all_Stats);
                            }
                        } else if (yesterday_session)
                        {
                            if (Utility.isYesterday(date.getTime()))
                                stat_map.add(data);
                            fill_statLists(all_Stats);
                        } else if (lastWeek_session)
                        {
                            if (Utility.isLastWeek(date.getTime()))
                                stat_map.add(data);
                            fill_statLists(all_Stats);
                        }

                        if (stat_map != null)
                        {
                            simpleAdapter = new SimpleAdapter(mContext,
                                    stat_map,
                                    android.R.layout.simple_list_item_2,
                                    new String[]{"title", "date"},
                                    new int[]{android.R.id.text1, android.R.id.text2});
                        }

                        if (simpleAdapter != null && statListView != null)
                        {
                            statListView.setAdapter(simpleAdapter);
                            simpleAdapter.notifyDataSetChanged();
                        }

                        if (check_Statslists())
                        {
                            avg_time = calculateAverage_integer(statlist_time);
                            avg_calorie = calculateAverage_integer(statlist_calorie);
                            avg_ditance = calculateAverag_float(statlist_distance);

                            txt_avg_time.setText(Utility.getDate((long) avg_time, "HH:mm:ss"));
                            txt_avg_cal.setText(String.valueOf(Utility.round((float) avg_calorie, 2)));
                            txt_avg_distance.setText(Float.toString(avg_ditance));
                        }

                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s)
                    {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot)
                    {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s)
                    {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }


                });


                return "Executed";

            }
        }

        new loadSessionListAsync().execute("");

    }

    /**
     * Configure the settings for the requested lists
     * @param all
     * @param today
     * @param yesterday
     * @param lastWeek
     */
    private void list_settings(boolean all, boolean today, boolean yesterday, boolean lastWeek)
    {
        all_session = all;
        today_session = today;
        yesterday_session = yesterday;
        lastWeek_session = lastWeek;

    }

    /**
     * Add the data for all the statlist_
     * @param statistiek
     */
    private void fill_statLists(Statistiek statistiek)
    {

        if (statistiek != null)
        {
            statlist_time.add(statistiek.getTime());
            statlist_calorie.add(statistiek.getBurnedCalories());
            statlist_distance.add(statistiek.getDistanceInMeters());
        }

    }
}