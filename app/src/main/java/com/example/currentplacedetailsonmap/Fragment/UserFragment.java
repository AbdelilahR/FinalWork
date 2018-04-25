package com.example.currentplacedetailsonmap.Fragment;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Activity.ChatActivity;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.Adapter.UserAdapter;
import com.example.currentplacedetailsonmap.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Links used
 * https://stackoverflow.com/questions/44286535/how-to-get-all-users-data-from-firebase-database-in-custom-listview-in-android
 * https://www.quora.com/How-do-I-show-list-of-other-logged-in-users-to-a-user-when-he-she-logs-in-to-the-app-in-firebase-Android
 * https://github.com/chat-sdk/chat-sdk-android
 * <p>
 * https://github.com/AleBarreto/FirebaseAndroidChat
 */
public class UserFragment extends Fragment implements Serializable
{
    //Test this --> https://stackoverflow.com/questions/32886546/how-to-get-all-child-list-from-firebase-android
    private FirebaseAuth mAuth;
    private String lastId;
    private UserAdapter userAdapter = null;
    private ArrayAdapter<User> adapter = null;
    private int preLast;
    public ListView userListView = null;
    public ArrayList<User> userList = new ArrayList<>();
    public User myUser = new User();
    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");

    public UserFragment()
    {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_user, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        userListView = (ListView) view.findViewById(R.id.userList);
        /* https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        /*https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */
        ref.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot dsp : dataSnapshot.getChildren())
                {

                    myUser = dsp.getValue(User.class);
                    lastId = dsp.getKey();

                    userList.add(myUser);
                }
                /*https://stackoverflow.com/questions/44777989/firebase-infinite-scroll-list-view-load-10-items-on-scrolling?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa*/
                if (userListView != null)
                    userListView.setOnScrollListener(new AbsListView.OnScrollListener()
                    {
                        private int currentVisibleItemCount;
                        private int currentScrollState;
                        private int currentFirstVisibleItem;
                        private int totalItem;


                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState)
                        {
                            this.currentScrollState = scrollState;
                            //https://stackoverflow.com/questions/23708271/count-total-number-of-list-items-in-a-listview?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
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

                        //stackoverflow.com/questions/39023945/how-to-get-data-from-real-time-database-in-firebase
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
                                            myUser = dsp.getValue(User.class);
                                            lastId = dsp.getKey();
                                            userList.add(myUser);
                                        }

                                        //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userList);
                                        userAdapter = new UserAdapter(getActivity().getApplicationContext(), userList);
                                        userListView.setAdapter(userAdapter);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError)
                                    {

                                    }

                                });
                            }
                        }
                    });
                //adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userList);
                if (getActivity() != null)
                    userAdapter = new UserAdapter(getActivity().getApplicationContext(), userList);
                if (userListView != null)
                {
                    userListView.setAdapter(userAdapter);

                    userListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                        {
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
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        //https://stackoverflow.com/questions/30847096/android-getmenuinflater-in-a-fragment-subclass-cannot-resolve-method
        //https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments
        inflater.inflate(R.menu.settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {


        if (item.getItemId() == R.id.profile_picture)
        {
            Toast.makeText(getContext(), "what?", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
