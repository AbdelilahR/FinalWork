package com.example.currentplacedetailsonmap.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Activity.ChatActivity;
import com.example.currentplacedetailsonmap.Adapter.FriendAdapter;
import com.example.currentplacedetailsonmap.Adapter.UserAdapter;
import com.example.currentplacedetailsonmap.Model.Friends;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.Model.Utility;
import com.example.currentplacedetailsonmap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment
{


    private ListView friendsListView;
    private FirebaseAuth mAuth;
    private String mCurrentUserId;
    private Location currentLocation;
    private ArrayList<Friends> friendList = new ArrayList<>();
    private FriendAdapter friendAdapter;
    private Friends myFriend;
    private String lastId;
    private Location friendsLocation = new Location("");
    private float distance;
    public DatabaseReference ref;
    private User user_request;

    public FriendsFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        friendsListView = (ListView) view.findViewById(R.id.friendList);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null)
        {

            mCurrentUserId = mAuth.getCurrentUser().getUid();
            ref = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrentUserId);
        }
        currentLocation = Utility.getLastKnownLocation(getActivity());
        //public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        /*https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */

        getActivity().setTitle("Friend list");
        loadFriendList();

        //setHasOptionsMenu(true);
    }

    //TODO finish this function (loadFriendlist)
    public void loadFriendList()
    {

        if (Utility.isNetworkAvailable(getActivity()))
        {
            class LoadDataFriendList extends AsyncTask<String, Void, String>
            {
                private ProgressDialog dialog = new ProgressDialog(getActivity());

                @Override
                protected void onPreExecute()
                {
                    dialog.setMessage("Loading. Please wait...");
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setIndeterminate(true);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();

                    //  super.onPreExecute();
                }

                @Override
                protected String doInBackground(String... strings)
                {
                    ref.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener()
                    {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot)
                        {
                            for (DataSnapshot dsp : dataSnapshot.getChildren())
                            {

                                String request_type = dsp.child("request_type").getValue().toString();
                                user_request = dsp.getValue(User.class);

                                myFriend = new Friends(request_type,user_request);
                                lastId = dsp.getKey();


                                friendList.add(myFriend);


                            }
                            /**/
                            if (friendsListView != null)
                                friendsListView.setOnScrollListener(new AbsListView.OnScrollListener()
                                {
                                    private int currentVisibleItemCount;
                                    private int currentScrollState;
                                    private int currentFirstVisibleItem;
                                    private int totalItem;


                                    @Override
                                    public void onScrollStateChanged(AbsListView view, int scrollState)
                                    {

                                        this.currentScrollState = scrollState;
                                        this.currentFirstVisibleItem = view.getFirstVisiblePosition();
                                        this.isScrollCompleted();
                                    }

                                    @Override
                                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
                                    {
                                        this.currentFirstVisibleItem = firstVisibleItem;
                                        this.currentVisibleItemCount = visibleItemCount;
                                        this.totalItem = totalItemCount;

                                    }

                                    //
                                    private void isScrollCompleted()
                                    {
                                        if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                                && this.currentScrollState == SCROLL_STATE_IDLE)
                                        {

                                            ref.orderByKey().startAt(lastId + 1).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener()
                                            {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot)
                                                {

                                                    for (DataSnapshot dsp : dataSnapshot.getChildren())
                                                    {
                                                        myFriend = dsp.getValue(Friends.class);
                                                        lastId = dsp.getKey();


                                                        friendList.add(myFriend);


                                                    }

                                                    friendAdapter = new FriendAdapter(getActivity().getApplicationContext(), friendList);
                                                    friendsListView.setAdapter(friendAdapter);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError)
                                                {

                                                }

                                            });
                                        }
                                    }
                                });

                            if (getActivity() != null)
                                friendAdapter = new FriendAdapter(getActivity().getApplicationContext(), friendList);

                            if (friendsListView != null && friendList != null)
                            {

                                friendsListView.setAdapter(friendAdapter);

                                friendsListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                    {
                                        //https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
                                        mAuth = FirebaseAuth.getInstance();
                                        Intent intent = new Intent(getActivity(), ChatActivity.class);
                                        Friends selectedFriend = (Friends) parent.getAdapter().getItem(position);
                                        intent.putExtra("selectedUser", selectedFriend.getUser());
                                        startActivity(intent);

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError)
                        {

                        }
                    });

                    return "Executed";
                }

                @Override
                protected void onPostExecute(String s)
                {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    // super.onPostExecute(s);
                }
            }


            new LoadDataFriendList().execute("");
        } else
            Toast.makeText(getContext(), "Please enable your network connection", Toast.LENGTH_SHORT).show();

    }

}
