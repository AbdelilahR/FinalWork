package com.example.currentplacedetailsonmap.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Fragment.FriendsFragment;
import com.firebase.client.ServerValue;
import com.example.currentplacedetailsonmap.Adapter.MessageAdapter;
import com.example.currentplacedetailsonmap.Model.GetTimeAgo;
import com.example.currentplacedetailsonmap.Model.Messages;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
//import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * source -> https://github.com/akshayejh/Lapit---Android-Firebase-Chat-App
 */
public class ChatActivity extends AppCompatActivity
{

    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private static final int GALLERY_PICK = 1;
    private static final int SEND_LOCATION = 2;
    public static Location myLocation = new Location("");
    private static LocationManager mLocationManager;
    private final List<Messages> messagesList = new ArrayList<>();
    private String mChatUser;
    private Toolbar mChatToolbar;
    private DatabaseReference mRootRef;
    private FirebaseAuth mAuth;
    private TextView mTitleView;
    private TextView mLastSeenView;
    private CircleImageView mProfileImage;
    private String mCurrentUserId;
    private ImageButton mChatAddBtn;
    private ImageButton mChatSendBtn;
    private EditText mChatMessageView;
    private RecyclerView mMessagesList;
    private SwipeRefreshLayout mRefreshLayout;
    private LinearLayoutManager mLinearLayout;
    private MessageAdapter mAdapter;
    private int mCurrentPage = 1;
    // Storage Firebase
    private StorageReference mImageStorage;
    private int itemPos = 0;
    private String mLastKey = "";
    private String mPrevKey = "";
    private User selectedUser;
    private String mCurrent_state;
    private DatabaseReference mFriendReqDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mNotificationDatabase;
    private Menu menu;
    private User mCurrentUser;

    private static Location getLastKnownLocation(Context c)
    {


        mLocationManager = (LocationManager) c.getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers)
        {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null)
            {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy())
            {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.send_options, menu);
        this.menu = menu;
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        if (item.getItemId() == R.id.send_image)
        {
            Intent galleryIntent = new Intent();
            galleryIntent.setType("image/*");
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
            return true;

        } else if (item.getItemId() == R.id.send_location)
        {
            myLocation = getLastKnownLocation(this);
            //sendLocation(myLocation);


            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);

            sendIntent.setType("image/*");

            onActivityResult(SEND_LOCATION, RESULT_OK, sendIntent);
            return true;

        } else if (item.getItemId() == R.id.send_friendRequest)
        {


                if (mCurrent_state.equals("not_friends"))
                {


                    //DatabaseReference newNotificationref = mRootRef.child("notifications").child(mChatUser).push();
                    //String newNotificationId = newNotificationref.getKey();

                    // HashMap<String, String> notificationData = new HashMap<>();
                    //notificationData.put("from", mCurrentUserId);
                    //notificationData.put("type", "request");

                    Map requestMap = new HashMap();

                    String path_mCurrentUser = "Friends/" + mCurrentUserId + "/" + mChatUser + "/";
                    //requestMap.put("Friends/" + mCurrentUserId + "/", selectedUser.getUserId());
                    requestMap.put(path_mCurrentUser + "achternaam", selectedUser.getAchternaam());
                    requestMap.put(path_mCurrentUser + "adress", selectedUser.getAdress());
                    requestMap.put(path_mCurrentUser + "avatar", selectedUser.getAvatar());
                    requestMap.put(path_mCurrentUser + "email", selectedUser.getEmail());
                    requestMap.put(path_mCurrentUser + "geslacht", selectedUser.getGeslacht());
                    requestMap.put(path_mCurrentUser + "status", selectedUser.getStatus());
                    requestMap.put(path_mCurrentUser + "userId", selectedUser.getUserId());
                    requestMap.put(path_mCurrentUser + "voornaam", selectedUser.getVoornaam());
                    requestMap.put(path_mCurrentUser + "wachtwoord", selectedUser.getWachtwoord());
                    requestMap.put(path_mCurrentUser + "request_type", "sent");

                    String path_mSelectedUser = "Friends/" + mChatUser + "/" + mCurrentUserId + "/";
                    Log.d("VARIABLE","Current user object: " + mCurrentUser);
                    requestMap.put(path_mSelectedUser + "achternaam", mCurrentUser.getAchternaam());
                    requestMap.put(path_mSelectedUser + "adress", mCurrentUser.getAdress());
                    requestMap.put(path_mSelectedUser + "avatar", mCurrentUser.getAvatar());
                    requestMap.put(path_mSelectedUser + "email", mCurrentUser.getEmail());
                    requestMap.put(path_mSelectedUser + "geslacht", mCurrentUser.getGeslacht());
                    requestMap.put(path_mSelectedUser + "status", mCurrentUser.getStatus());
                    requestMap.put(path_mSelectedUser + "userId", mCurrentUser.getUserId());
                    requestMap.put(path_mSelectedUser + "voornaam", mCurrentUser.getVoornaam());
                    requestMap.put(path_mSelectedUser + "wachtwoord", mCurrentUser.getWachtwoord());
                    requestMap.put(path_mSelectedUser + "request_type", "received");
                    mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if (databaseError != null)
                            {

                                Toast.makeText(ChatActivity.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else
                            {

                                mCurrent_state = "req_sent";
                                //mProfileSendReqBtn.setText("Cancel Friend Request");
                                item.setEnabled(true);
                                item.setVisible(true);

                            }

                            //mProfileSendReqBtn.setEnabled(true);


                        }
                    });
                    item.setEnabled(false);
                    item.setVisible(false);
                }
                else
                {
                    item.setEnabled(true);
                    item.setVisible(true);
                }


            return true;
        }

        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatToolbar = (Toolbar) findViewById(R.id.chat_app_bar);
        mCurrent_state = "not_friends";
        setSupportActionBar(mChatToolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        //initialize firebase
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotificationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mRootRef = FirebaseDatabase.getInstance().getReference();
        //initialize current user and selected user
        mAuth = FirebaseAuth.getInstance();
        mCurrentUserId = mAuth.getCurrentUser().getUid();
        selectedUser = (User) getIntent().getExtras().getSerializable("selectedUser");
        loadCurrentUser();

        mChatUser = selectedUser.getUserId();
        String selectedUser_userame = selectedUser.getVoornaam() + " " + selectedUser.getAchternaam();
        //layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.chat_custom_bar, null);
        actionBar.setCustomView(action_bar_view);

        // ---- Custom Action bar Items ----

        mTitleView = (TextView) findViewById(R.id.custom_bar_title);
        mLastSeenView = (TextView) findViewById(R.id.custom_bar_seen);
        mProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);

        mChatAddBtn = (ImageButton) findViewById(R.id.chat_add_btn);
        mChatSendBtn = (ImageButton) findViewById(R.id.chat_send_btn);
        mChatMessageView = (EditText) findViewById(R.id.chat_message_view);

        mAdapter = new MessageAdapter(messagesList, mChatUser, mCurrentUserId, this);

        mMessagesList = (RecyclerView) findViewById(R.id.messages_list);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.message_swipe_layout);
        mLinearLayout = new LinearLayoutManager(this);

        mMessagesList.setHasFixedSize(true);
        mMessagesList.setLayoutManager(mLinearLayout);

        //Set profiel image of selected user

        if (selectedUser.getAvatar().equals("default"))
            mProfileImage.setImageResource(R.drawable.default_avatar);
        else
            Picasso.with(mProfileImage.getContext()).load(selectedUser.getAvatar()).resize(36, 36).into(mProfileImage);

        mMessagesList.setAdapter(mAdapter);

        //------- IMAGE STORAGE ---------
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);


        loadMessages();

        mTitleView.setText(selectedUser_userame);

        mRootRef.child("User").child(mChatUser).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                String online = dataSnapshot.child("status").getValue().toString();
                //String image = dataSnapshot.child("image").getValue().toString();

                if (online.equals("online"))
                {

                    mLastSeenView.setText("Online");

                } else
                {

                    GetTimeAgo getTimeAgo = new GetTimeAgo();

                    //long lastTime = Long.parseLong(online);
                    long lastTime = Long.valueOf(online);
                    String lastSeenTime = GetTimeAgo.getTimeAgo(lastTime, getApplicationContext());

                    mLastSeenView.setText(lastSeenTime);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        mRootRef.child("Chat").child(mCurrentUserId).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                if (!dataSnapshot.hasChild(mChatUser))
                {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("seen", false);
                    chatAddMap.put("timestamp", com.firebase.client.ServerValue.TIMESTAMP);

                    Map chatUserMap = new HashMap();
                    chatUserMap.put("Chat/" + mCurrentUserId + "/" + mChatUser, chatAddMap);
                    chatUserMap.put("Chat/" + mChatUser + "/" + mCurrentUserId, chatAddMap);

                    mRootRef.updateChildren(chatUserMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if (databaseError != null)
                            {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });


        mChatSendBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                sendMessage();


            }
        });


        mChatAddBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openOptionsMenu();


            }
        });


        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override
            public void onRefresh()
            {

                mCurrentPage++;

                itemPos = 0;

                loadMoreMessages();

            }
        });


    }

    private void loadCurrentUser()
    {

        
        class getCurrentUserAsync extends AsyncTask<String,Void,String>
        {


            @Override
            protected String doInBackground(String... strings)
            {
                FirebaseDatabase.getInstance().getReference("User").child(mCurrentUserId).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        mCurrentUser = dataSnapshot.getValue(User.class);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
                return "Executed";
            }

        }
       new getCurrentUserAsync().execute("");

    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


        class shareAsync extends AsyncTask<String, Void, String>
        {
            @Override
            protected void onPostExecute(String s)
            {
                mChatMessageView.setText("");
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... strings)
            {

                if (requestCode == GALLERY_PICK && resultCode == RESULT_OK)
                {

                    Uri imageUri = data.getData();

                    final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                    final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                    DatabaseReference user_message_push = mRootRef.child("messages")
                            .child(mCurrentUserId).child(mChatUser).push();

                    final String push_id = user_message_push.getKey();


                    StorageReference filepath = mImageStorage.child("message_images").child(push_id + ".jpg");

                    filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                        {

                            if (task.isSuccessful())
                            {

                                String download_url = task.getResult().getDownloadUrl().toString();


                                Map messageMap = new HashMap();
                                messageMap.put("message", download_url);
                                messageMap.put("seen", false);
                                messageMap.put("type", "image");
                                messageMap.put("time", ServerValue.TIMESTAMP);
                                messageMap.put("from", mCurrentUserId);

                                Map messageUserMap = new HashMap();
                                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


                                mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener()
                                {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                                    {

                                        if (databaseError != null)
                                        {

                                            Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                        }

                                    }
                                });


                            }

                        }
                    });

                }
                if (requestCode == SEND_LOCATION && resultCode == RESULT_OK)
                {


                    final String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                    final String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                    DatabaseReference user_message_push = mRootRef.child("messages")
                            .child(mCurrentUserId).child(mChatUser).push();

                    final String push_id = user_message_push.getKey();

                    String url = "http://maps.google.com/maps/api/staticmap?center=" + myLocation.getLatitude() + "," + myLocation.getLongitude() + "&zoom=15&size=400x400&sensor=true&format=jpg";


                    Map messageMap = new HashMap();
                    messageMap.put("message", url);
                    messageMap.put("seen", false);
                    messageMap.put("type", "map");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", mCurrentUserId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);

//                    mChatMessageView.setText("");

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if (databaseError != null)
                            {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });

                }

                return "Executed";
            }
        }
        new shareAsync().execute("");
    }

    private void loadMoreMessages()
    {

        class loadMoreMessagesAsync extends AsyncTask<String, Void, String>
        {

            @Override
            protected String doInBackground(String... strings)
            {
                DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

                Query messageQuery = messageRef.orderByKey().endAt(mLastKey).limitToLast(10);

                messageQuery.addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {


                        Messages message = dataSnapshot.getValue(Messages.class);
                        String messageKey = dataSnapshot.getKey();

                        if (!mPrevKey.equals(messageKey))
                        {

                            messagesList.add(itemPos++, message);

                        } else
                        {

                            mPrevKey = mLastKey;

                        }


                        if (itemPos == 1)
                        {

                            mLastKey = messageKey;

                        }


                        Log.d("TOTALKEYS", "Last Key : " + mLastKey + " | Prev Key : " + mPrevKey + " | Message Key : " + messageKey);

                        mAdapter.notifyDataSetChanged();

                        mRefreshLayout.setRefreshing(false);

                        mLinearLayout.scrollToPositionWithOffset(10, 0);

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
        new loadMoreMessagesAsync().execute("");

    }

    private void loadMessages()
    {

        class loadMessageAsync extends AsyncTask<String, Void, String>
        {

            @Override
            protected String doInBackground(String... strings)
            {
                DatabaseReference messageRef = mRootRef.child("messages").child(mCurrentUserId).child(mChatUser);

                Query messageQuery = messageRef.limitToLast(mCurrentPage * TOTAL_ITEMS_TO_LOAD);


                messageQuery.addChildEventListener(new ChildEventListener()
                {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s)
                    {

                        Messages message = dataSnapshot.getValue(Messages.class);

                        itemPos++;

                        if (itemPos == 1)
                        {

                            String messageKey = dataSnapshot.getKey();

                            mLastKey = messageKey;
                            mPrevKey = messageKey;

                        }

                        messagesList.add(message);

                        mAdapter.notifyDataSetChanged();

                        mMessagesList.scrollToPosition(messagesList.size() - 1);

                        mRefreshLayout.setRefreshing(false);

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
        new loadMessageAsync().execute("");

    }

    private void sendMessage()
    {

        class sendMessageAsync extends AsyncTask<String, Void, String>
        {
            @Override
            protected void onPostExecute(String s)
            {
                mChatMessageView.setText("");
            }

            @Override
            protected String doInBackground(String... strings)
            {
                String message = mChatMessageView.getText().toString();

                if (!TextUtils.isEmpty(message))
                {

                    String current_user_ref = "messages/" + mCurrentUserId + "/" + mChatUser;
                    String chat_user_ref = "messages/" + mChatUser + "/" + mCurrentUserId;

                    DatabaseReference user_message_push = mRootRef.child("messages")
                            .child(mCurrentUserId).child(mChatUser).push();

                    String push_id = user_message_push.getKey();

                    Map messageMap = new HashMap();
                    messageMap.put("message", message);
                    messageMap.put("seen", false);
                    messageMap.put("type", "text");
                    messageMap.put("time", ServerValue.TIMESTAMP);
                    messageMap.put("from", mCurrentUserId);

                    Map messageUserMap = new HashMap();
                    messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                    messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);


                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("seen").setValue(true);
                    mRootRef.child("Chat").child(mCurrentUserId).child(mChatUser).child("timestamp").setValue(ServerValue.TIMESTAMP);

                    mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("seen").setValue(false);
                    mRootRef.child("Chat").child(mChatUser).child(mCurrentUserId).child("timestamp").setValue(ServerValue.TIMESTAMP);

                    mRootRef.updateChildren(messageUserMap, new DatabaseReference.CompletionListener()
                    {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference)
                        {

                            if (databaseError != null)
                            {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });

                }
                return "Executed";
            }
        }
        new sendMessageAsync().execute("");
    }

    /**
     * source --> https://stackoverflow.com/questions/32969172/how-to-display-menu-item-with-icon-and-text-in-appcompatactivity
     *
     * @param r
     * @param title
     * @return
     */
    private CharSequence menuIconWithText(Drawable r, String title)
    {

        r.setBounds(0, 0, r.getIntrinsicWidth(), r.getIntrinsicHeight());
        SpannableString sb = new SpannableString("    " + title);
        ImageSpan imageSpan = new ImageSpan(r, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return sb;
    }
}
