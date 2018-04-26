package com.example.currentplacedetailsonmap.Adapter;

import android.app.Notification;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.Model.Messages;
import com.example.currentplacedetailsonmap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by AkshayeJH on 24/07/17.
 * https://github.com/akshayejh/Lapit---Android-Firebase-Chat-App
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder>
{
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    public FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public String currentUserID = mAuth.getCurrentUser().getUid();
    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase;
    private SimpleDateFormat dateFormat;
    private String millisInString;
    private String selectedUserId;
    private String mCurrentUserId;
    private int viewType;
    private DatabaseReference mRootRef;

    public MessageAdapter(List<Messages> mMessageList, String selectedUserId, String mCurrentUserId)
    {

        this.mMessageList = mMessageList;
        this.selectedUserId = selectedUserId;
        this.mCurrentUserId = mCurrentUserId;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v;
        if (getViewType() == VIEW_TYPE_MESSAGE_SENT)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_send, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_receive, parent, false);
        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        millisInString = dateFormat.format(new Date());


        mUserDatabase = FirebaseDatabase.getInstance().getReference("User").child(from_user);
        mRootRef = FirebaseDatabase.getInstance().getReference().child("messages").child(mCurrentUserId).child(selectedUserId);
        mUserDatabase.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {

                String firstName = dataSnapshot.child("voornaam").getValue().toString();
                String lastName = dataSnapshot.child("achternaam").getValue().toString();
                //String childKey = dataSnapshot.getChildren().iterator().next().getKey();
                // String image = dataSnapshot.child("thumb_image").getValue().toString();

                viewHolder.displayName.setText(firstName + " " + lastName);

                //Picasso.with(viewHolder.profileImage.getContext()).load(image).placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        if (message_type.equals("text"))
        {
            viewHolder.messageText.setText(c.getMessage());
            viewHolder.displayTime.setText(getDate(String.valueOf(c.getTime())));
            //viewHolder.messageImage.setVisibility(View.INVISIBLE);


        } else
        {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                    .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

        }

    }

    @Override
    public int getItemViewType(int position)
    {
        Messages message = mMessageList.get(position);
        if (message.getFrom() == mCurrentUserId)
            return VIEW_TYPE_MESSAGE_SENT;
        else
            return VIEW_TYPE_MESSAGE_RECEIVED;

    }

    /**
     * source: https://stackoverflow.com/questions/13241251/timestamp-to-string-date?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
     *
     * @param timeStampStr
     * @return
     */
    private String getDate(String timeStampStr)
    {
        try
        {
            DateFormat sdf = new SimpleDateFormat("HH:mm");
            Date netDate = (new Date(Long.parseLong(timeStampStr)));
            return sdf.format(netDate);
        } catch (Exception ignored)
        {
            return "xx";
        }
    }

    @Override
    public int getItemCount()
    {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder
    {

        public TextView messageText;
        public CircleImageView profileImage;
        public TextView displayName;
        public TextView displayTime;
        public ImageView messageImage;

        public MessageViewHolder(View view)
        {
            super(view);
            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            //profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            //messageImage = (ImageView) view.findViewById(R.id.message_profile_layout);
            displayTime = (TextView) view.findViewById(R.id.time_text_layout);


        }


    }


}
