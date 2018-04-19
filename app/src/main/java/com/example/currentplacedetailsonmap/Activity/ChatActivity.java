package com.example.currentplacedetailsonmap.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.currentplacedetailsonmap.Adapter.ChatAdapter;
import com.example.currentplacedetailsonmap.Model.Chat;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.R;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//https://github.com/akshayejh/Lapit---Android-Firebase-Chat-App
public class ChatActivity extends Activity {
    private static final String TAG = ChatActivity.class.getName();

    private EditText metText;
    private Button mbtSent;
    private DatabaseReference mFirebaseRef;
    private User selectedUser;
    private List<Chat> mChats;
    private RecyclerView mRecyclerView;
    private ChatAdapter mAdapter;
    private String mId;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        metText = (EditText) findViewById(R.id.etText);
        mbtSent = (Button) findViewById(R.id.btSent);
        mRecyclerView = (RecyclerView) findViewById(R.id.rvChat);
        mChats = new ArrayList<>();

        //mId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        selectedUser = (User) getIntent().getExtras().getSerializable("selectedUser");
        mId = selectedUser.getUserId();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        //mRecyclerView.setItemAnimator(new SlideInOutLeftItemAnimator(mRecyclerView));
        mAdapter = new ChatAdapter(mChats, mId);
        mRecyclerView.setAdapter(mAdapter);

        /**
         * Firebase - Inicialize
         */
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mFirebaseRef = database.getReference().child("user").child(selectedUser.getUserId());


        mbtSent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = metText.getText().toString();

                if (!message.isEmpty()) {
                    /**
                     * Firebase - Send message
                     */
                    mFirebaseRef.setValue(new Chat(message, selectedUser.getEmail()));
                }

                metText.setText("");
            }
        });


        /**
         * Firebase - Receives message
         */
        mFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    try {

                        Chat model = dataSnapshot.getValue(Chat.class);

                        mChats.add(model);
                        mRecyclerView.scrollToPosition(mChats.size() - 1);
                        mAdapter.notifyItemInserted(mChats.size() - 1);
                    } catch (Exception ex) {
                        Log.e(TAG, ex.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*
        mFirebaseRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

               }
            }
*/


    }
}
