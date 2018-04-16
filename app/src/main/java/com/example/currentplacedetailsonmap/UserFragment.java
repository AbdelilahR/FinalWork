package com.example.currentplacedetailsonmap;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Class.User;
import com.google.firebase.auth.FirebaseAuth;
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
 * A simple {@link Fragment} subclass.
 * Links used
 * https://stackoverflow.com/questions/44286535/how-to-get-all-users-data-from-firebase-database-in-custom-listview-in-android
 * https://www.quora.com/How-do-I-show-list-of-other-logged-in-users-to-a-user-when-he-she-logs-in-to-the-app-in-firebase-Android
 * https://github.com/chat-sdk/chat-sdk-android
 * <p>
 * https://github.com/AleBarreto/FirebaseAndroidChat
 */
public class UserFragment extends ListFragment {
    //Test this --> https://stackoverflow.com/questions/32886546/how-to-get-all-child-list-from-firebase-android
    private FirebaseAuth mAuth;
    private String lastId;
    private ArrayAdapter<User> adapter = null;
    private int preLast;
    public ListView userListView = null;
    public ArrayList<User> userList = new ArrayList<>();
    public User myUser = new User();
    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");

    public UserFragment() {
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
        userListView = getListView();
        /* https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */
        //ArrayList<User> userArrayList = new ArrayList<>();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       /*https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */
        ref.limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                //Map<String,Object> td = (((HashMap<String, Object>) dataSnapshot.getValue()));

                //userList = getAllUsers((Map<String, Object>) dataSnapshot.getValue());
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    //dsp.getChildrenCount();
                    myUser = dsp.getValue(User.class);
                    lastId = dsp.getKey();
                 //   if (dsp.getKey() != lastId)
                        userList.add(myUser);
                }
              /*  adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userList);

                userListView.setAdapter(adapter);/*
                /*https://stackoverflow.com/questions/44777989/firebase-infinite-scroll-list-view-load-10-items-on-scrolling?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa*/
                userListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    private int currentVisibleItemCount;
                    private int currentScrollState;
                    private int currentFirstVisibleItem;
                    private int totalItem;
                    private int lastItem;


                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {
                        this.currentScrollState = scrollState;
                        //https://stackoverflow.com/questions/23708271/count-total-number-of-list-items-in-a-listview?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
                        //this.currentFirstVisibleItem = userListView.getAdapter().getCount();
                        this.currentFirstVisibleItem = getListView().getFirstVisiblePosition();
                        this.isScrollCompleted();
                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        //view = getListView();
                        this.currentFirstVisibleItem = firstVisibleItem;
                        this.currentVisibleItemCount = visibleItemCount;
                        this.totalItem = totalItemCount;
                        //https://stackoverflow.com/questions/5123675/find-out-if-listview-is-scrolled-to-the-bottom
                        lastItem = firstVisibleItem + visibleItemCount;
                        visibleItemCount = view.getLastVisiblePosition();

                    }

                    private void isScrollCompleted() {
                        if (totalItem - currentFirstVisibleItem == currentVisibleItemCount
                                && this.currentScrollState == SCROLL_STATE_IDLE) {

                            ref.orderByKey().startAt(lastId + 1).limitToFirst(5).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                                        myUser = dsp.getValue(User.class);
                                        lastId = dsp.getKey();
                                        //if ( )
                                            userList.add(myUser);

                                    }

                                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userList);

                                    userListView.setAdapter(adapter);
                                    // currentFirstVisibleItem = userList.;
/*                                    adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userList);
                                    userListView.setAdapter(adapter);
*/
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });
                        }
                    }
                });
                adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userList);

                userListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }
}