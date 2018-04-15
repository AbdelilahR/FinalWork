package com.example.currentplacedetailsonmap;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
public class UserFragment extends ListFragment
{
    //Test this --> https://stackoverflow.com/questions/32886546/how-to-get-all-child-list-from-firebase-android
    private FirebaseAuth mAuth;
    public ListView userListView = null;
    public ArrayList<User> userList = new ArrayList<>();

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
        userListView = getListView();
        /* https://stackoverflow.com/questions/38965731/how-to-get-all-childs-data-in-firebase-database */
        //ArrayList<User> userArrayList = new ArrayList<>();


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

// Inflate the layout for this fragment

        //userListView = getListView();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User");

        ref.addListenerForSingleValueEvent(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {


                //Map<String,Object> td = (((HashMap<String, Object>) dataSnapshot.getValue()));

                //userList = getAllUsers((Map<String, Object>) dataSnapshot.getValue());
                for (DataSnapshot dsp : dataSnapshot.getChildren())
                {
                    User myUser = dsp.getValue(User.class);

                    userList.add(myUser);
                }
                ArrayAdapter<User> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, userList);
                userListView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }



        });


    }

    private ArrayList<String> getAllUsers(Map<String, Object> users)
    {

        ArrayList<String> userList = new ArrayList<>();

        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet())
        {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            userList.add((String) singleUser.get("User"));
        }

        System.out.println(userList.toString());
        return userList;
    }

}
