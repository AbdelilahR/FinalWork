package com.example.currentplacedetailsonmap.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.currentplacedetailsonmap.Model.Statistiek;
import com.example.currentplacedetailsonmap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
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
    private List<Map<String, String>> stat_list;
    //Adapter
    private SimpleAdapter simpleAdapter;
    //ListView
    private ListView statListView;
    //Context to solve nullpointerException
    private Context mContext;

    public StatsFragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mContext = getActivity();
        return inflater.inflate(R.layout.fragment_stats, container, false);
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
        stat_list = new ArrayList<Map<String, String>>();

        //put data in the list
        database.addChildEventListener(new ChildEventListener()
        {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s)
            {
                Statistiek statistiek = dataSnapshot.getValue(Statistiek.class);
                String title = statistiek.getName() + " " + statistiek.getId();
                Map<String, String> data = new HashMap<>();
                data.put("title", title);
                data.put("date", statistiek.getDate());

                stat_list.add(data);

                if (!stat_list.isEmpty() && stat_list != null)
                {
                    simpleAdapter = new SimpleAdapter(mContext,
                            stat_list,
                            android.R.layout.simple_list_item_2,
                            new String[]{"title", "date"},
                            new int[]{android.R.id.text1, android.R.id.text2});
                }

                if (simpleAdapter != null && statListView != null)
                {
                    statListView.setAdapter(simpleAdapter);
                    simpleAdapter.notifyDataSetChanged();
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


    }

}