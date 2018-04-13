package com.example.currentplacedetailsonmap;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;


/**
 * A simple {@link Fragment} subclass.
 * Links used
 * https://stackoverflow.com/questions/44286535/how-to-get-all-users-data-from-firebase-database-in-custom-listview-in-android
 * https://www.quora.com/How-do-I-show-list-of-other-logged-in-users-to-a-user-when-he-she-logs-in-to-the-app-in-firebase-Android
 * https://github.com/chat-sdk/chat-sdk-android
 *
 * https://github.com/AleBarreto/FirebaseAndroidChat
 */
public class UserFragment extends Fragment {

    private FirebaseAuth mAuth;
    public UserFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user, container, false);
    }

}
