package com.example.currentplacedetailsonmap.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Activity.AuthenticationActivity;
import com.example.currentplacedetailsonmap.Activity.ChatActivity;
import com.example.currentplacedetailsonmap.Activity.LoginActivity;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.Adapter.UserAdapter;
import com.example.currentplacedetailsonmap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


/**
 * Source used:
 * <p>
 * https://stackoverflow.com/questions/44286535/how-to-get-all-users-data-from-firebase-database-in-custom-listview-in-android
 * https://www.quora.com/How-do-I-show-list-of-other-logged-in-users-to-a-user-when-he-she-logs-in-to-the-app-in-firebase-Android
 * https://github.com/chat-sdk/chat-sdk-android
 * https://github.com/AleBarreto/FirebaseAndroidChat
 * https://zigazibert.wordpress.com/2014/03/19/using-android-togglebutton-inside-action-bar-2/
 * https://stackoverflow.com/questions/6159702/show-spinning-wheel-dialog-while-loading-data-on-android/6159735
 * https://stackoverflow.com/questions/24294936/how-to-put-method-into-the-asynctask
 */
public class UserFragment extends Fragment implements Serializable {
    public ListView userListView = null;
    public ArrayList<User> userList = new ArrayList<>();
    public User myUser = new User();

    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");

    //Test this --> https://stackoverflow.com/questions/32886546/how-to-get-all-child-list-from-firebase-android
    private FirebaseAuth mAuth;
    private String lastId;
    private UserAdapter userAdapter = null;
    private ArrayAdapter<User> adapter = null;
    private String mCurrentUserId;
    private LocationManager mLocationManager;
    private float radius = Float.MAX_VALUE;
    private Boolean online_only = false;
    private float distance;
    private Location userLocation = new Location("");
    private Location currentLocation = new Location("");
    private String userId;


    public UserFragment() {
        // this.radius = Float.MAX_VALUE;
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);


        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userListView = (ListView) view.findViewById(R.id.userList);

        /* https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            mCurrentUserId = mAuth.getCurrentUser().getUid();
            userId = mAuth.getUid();
        }
        //todo Message text no internet connection when mAuth is null
        currentLocation = getLastKnownLocation();
        //public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        /*https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */

        getActivity().setTitle("Users");
        loadUserList();

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //https://stackoverflow.com/questions/30847096/android-getmenuinflater-in-a-fragment-subclass-cannot-resolve-method
        //https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments
        inflater.inflate(R.menu.settings, menu);


        super.onCreateOptionsMenu(menu, inflater);

        //return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.status_switch) {
            if (item.isChecked()) {
                setOnline_only(false);
                userList = new ArrayList<>();
                loadUserList();
                item.setChecked(false);
            } else if (!item.isChecked()) {
                setOnline_only(true);
                userList = new ArrayList<>();
                loadUserList();
                item.setChecked(true);
            }
        }
        if (item.getItemId() == R.id.profile_picture) {
            Toast.makeText(getContext(), "what?", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), AuthenticationActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.distance_any) {


            setRadius(Float.MAX_VALUE);
            userList = new ArrayList<>();

            loadUserList();
            item.setChecked(true);
            return super.onOptionsItemSelected(item);
        } else if (item.getItemId() == R.id.distance_1km)

        {
            setRadius(999);
            userList = new ArrayList<>();
            loadUserList();
            item.setChecked(true);

            return super.onOptionsItemSelected(item);
        } else if (item.getItemId() == R.id.distance_10km) {
            setRadius(10000);
            userList = new ArrayList<>();
            loadUserList();
            item.setChecked(true);

            return super.onOptionsItemSelected(item);
        }

        if (item.getItemId() == R.id.Logout) {


            Intent intent = new Intent(getActivity(), LoginActivity.class);

            long milis = new Date().getTime();
            String last_seen = String.valueOf(milis);
            FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("status").setValue(last_seen);
            FirebaseAuth.getInstance().signOut();
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Location getLastKnownLocation() {
        mLocationManager = (LocationManager) getContext().getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void loadUserList() {


        class LoadDataUserList extends AsyncTask<String, Void, String> {
            private ProgressDialog dialog = new ProgressDialog(getActivity());

            @Override
            protected void onPreExecute() {
                dialog.setMessage("Loading. Please wait...");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.setIndeterminate(true);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                //  super.onPreExecute();
            }

            @Override
            protected String doInBackground(String... strings) {
                ref.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                /*
                String achternaam = dataSnapshot.child(mCurrentUserId).child("achternaam").getValue().toString();
                String voornaam = dataSnapshot.child(mCurrentUserId).child("voornaam").getValue().toString();
                loggedUser = voornaam + " " + achternaam;
                if (loggedUser != null)
                    getActivity().setTitle(loggedUser);
                */
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {


                            myUser = dsp.getValue(User.class);
                            lastId = dsp.getKey();
                            if (!myUser.getUserId().equalsIgnoreCase(mCurrentUserId)) {
                                userLocation.setLatitude(myUser.getAdress().getLatitude());
                                userLocation.setLongitude(myUser.getAdress().getLongitude());
                                distance = currentLocation.distanceTo(userLocation);

                                if (getOnline_only()) {
                                    if (distance <= getRadius() && myUser.getStatus().equals("online"))
                                        userList.add(myUser);
                                } else {
                                    if (distance <= getRadius())
                                        userList.add(myUser);
                                }
                            }
                        }
                        /*https://stackoverflow.com/questions/44777989/firebase-infinite-scroll-list-view-load-10-items-on-scrolling?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa*/
                        if (userListView != null)
                            userListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                                private int currentVisibleItemCount;
                                private int currentScrollState;
                                private int currentFirstVisibleItem;
                                private int totalItem;


                                @Override
                                public void onScrollStateChanged(AbsListView view, int scrollState) {
                                    this.currentScrollState = scrollState;
                                    //https://stackoverflow.com/questions/23708271/count-total-number-of-list-items-in-a-listview?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
                                    this.currentFirstVisibleItem = view.getFirstVisiblePosition();
                                    this.isScrollCompleted();
                                }

                                @Override
                                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                                    this.currentFirstVisibleItem = firstVisibleItem;
                                    this.currentVisibleItemCount = visibleItemCount;
                                    this.totalItem = totalItemCount;

                                }

                                //stackoverflow.com/questions/39023945/how-to-get-data-from-real-time-database-in-firebase
                                private void isScrollCompleted() {
                                    if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                            && this.currentScrollState == SCROLL_STATE_IDLE) {

                                        ref.orderByKey().startAt(lastId + 1).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {

                                                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                                    myUser = dsp.getValue(User.class);
                                                    lastId = dsp.getKey();
                                                    if (!myUser.getUserId().equalsIgnoreCase(mCurrentUserId)) {
                                                        distance = currentLocation.distanceTo(userLocation);
                                                        if (distance <= getRadius())
                                                            userList.add(myUser);

                                                    }
                                                }

                                                userAdapter = new UserAdapter(getActivity().getApplicationContext(), userList);
                                                userListView.setAdapter(userAdapter);
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }

                                        });
                                    }
                                }
                            });

                        if (getActivity() != null)
                            userAdapter = new UserAdapter(getActivity().getApplicationContext(), userList);

                        if (userListView != null) {
                            userListView.invalidateViews();
                            userListView.setAdapter(userAdapter);

                            userListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    //https://stackoverflow.com/questions/3913592/start-an-activity-with-a-parameter?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
                                    mAuth = FirebaseAuth.getInstance();
                                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                                    User selectedUser = (User) parent.getAdapter().getItem(position);
                                    intent.putExtra("selectedUser", selectedUser);
                                    startActivity(intent);

                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                return "Executed";
            }

            @Override
            protected void onPostExecute(String s) {
                if (dialog.isShowing())
                    dialog.dismiss();
                // super.onPostExecute(s);
            }
        }


        new LoadDataUserList().execute("");

    }

    public Boolean getOnline_only() {
        return online_only;
    }

    public void setOnline_only(Boolean online_only) {
        this.online_only = online_only;
    }
}
