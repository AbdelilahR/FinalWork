package com.example.currentplacedetailsonmap.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Model.Chat;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.R;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * My source for this code is --> https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
 */
//https://code.tutsplus.com/tutorials/how-to-create-an-android-chat-app-using-firebase--cms-27397
public class ChatActivity extends AppCompatActivity
{

    private static final int SIGN_IN_REQUEST_CODE = 1;

    private FirebaseListAdapter<Chat> adapter;
    private User selectedUser;
    private DatabaseReference mDatabase;
    private DatabaseReference mCurrentUser;
    private FirebaseAuth mAuth;
    String naam;
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();

        selectedUser = (User) getIntent().getExtras().getSerializable("selectedUser");
        //mDatabase = FirebaseDatabase.getInstance().getReference("User").child(selectedUser.getUserId()).child("Chat");
        mDatabase = FirebaseDatabase.getInstance().getReference("User").child(currentUserID).child("Chat");
        mCurrentUser = FirebaseDatabase.getInstance().getReference().child("User").child(currentUserID);
        mCurrentUser.addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                String voornaam = dataSnapshot.child("voornaam").getValue().toString();
                String achternaam = dataSnapshot.child("achternaam").getValue().toString();

                naam = voornaam + " " + achternaam;
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null)
            Toast.makeText(this, "" + currentUser, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "" + currentUserID, Toast.LENGTH_SHORT).show();
        selectedUser.getUserId();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                EditText input = (EditText) findViewById(R.id.input);

                // Read the input field and push a new instance
                // of Chat to the Firebase database
                //FirebaseDatabase.getInstance().getReference().push().setValue(new Chat(input.getText().toString(),selectedUser.getVoornaam() +""+ selectedUser.getAchternaam()));
                //TODO above code is correct, below code is for testing purpose remove it later
                mDatabase.push().setValue(new Chat(input.getText().toString(), naam));
                // Clear the input
                input.setText("");
            }
        });
        displayChats();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();

                displayChats();
            } else
            {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();
                finish();
            }
        }
    }

    /*
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.main_menu, menu);
            return true;
        }
    */
/*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ChatActivity.this,
                                "You have been signed out.",
                                Toast.LENGTH_LONG)
                                .show();

                        // Close activity
                        finish();
                    }
                });
        }
        return true;
    }
*/

    private void displayChats()
    {
        ListView listOfMessages = (ListView) findViewById(R.id.list_of_messages);
        //mDatabase = FirebaseDatabase.getInstance().getReference("User").child(selectedUser.getUserId()).child("Chat");
        adapter = new FirebaseListAdapter<Chat>(this, Chat.class, R.layout.message,
                mDatabase)

                //FirebaseDatabase.getInstance().getReference("User").child(currentUserID).child("Chat"))
                //FirebaseDatabase.getInstance().getReference("User").child(selectedUser.getUserId()).child("Chat"))
        {
            @Override
            protected void populateView(View v, Chat model, int position)
            {
                // Get references to the views of message.xml
                TextView messageText = (TextView) v.findViewById(R.id.message_text);
                TextView messageUser = (TextView) v.findViewById(R.id.message_user);
                TextView messageTime = (TextView) v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",
                        model.getMessageTime()));
            }
        };

        listOfMessages.setAdapter(adapter);
    }
}
