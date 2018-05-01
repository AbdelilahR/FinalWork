package com.example.currentplacedetailsonmap.Adapter;

import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Activity.ChatActivity;
import com.example.currentplacedetailsonmap.Model.Messages;
import com.example.currentplacedetailsonmap.PhotoFullPopupWindow;
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

import static android.content.Context.LOCATION_SERVICE;

/**
 * Created by AkshayeJH on 24/07/17.
 * https://github.com/akshayejh/Lapit---Android-Firebase-Chat-App
 * https://stackoverflow.com/questions/3990110/how-to-show-marker-in-maps-launched-by-geo-uri-intent
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
    private boolean zoomOut = false;
    private DatabaseReference mRootRef;
    public ChatActivity chatActivity;

    private String my_url;

    private int teller = 0;
    private Location myLocation = new Location("");
    private LocationManager mLocationManager;

    public MessageAdapter(List<Messages> mMessageList, String selectedUserId, String mCurrentUserId, ChatActivity chatActivity)
    {

        this.mMessageList = mMessageList;
        this.selectedUserId = selectedUserId;
        this.mCurrentUserId = mCurrentUserId;
        this.chatActivity = chatActivity;
    }


    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view;
        if (viewType == VIEW_TYPE_MESSAGE_SENT)
        {
            {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_send, parent, false);
                return new MessageViewHolder(view);
            }
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat_receive, parent, false);
            return new MessageViewHolder(view);
        }

        return null;
    }


    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i)
    {

        Messages c = mMessageList.get(i);
        String from_user = c.getFrom();
        String message_type = c.getType();

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        millisInString = dateFormat.format(new Date());
        getItemViewType(i);
        mUserDatabase = FirebaseDatabase.getInstance().getReference("User").child(from_user);


        viewHolder.displayName.setText("");

        if (message_type.equals("text"))
        {

            viewHolder.messageText.setText(c.getMessage());
            viewHolder.displayTime.setText(getDate(String.valueOf(c.getTime())));
            viewHolder.messageImage.setVisibility(View.INVISIBLE);
            viewHolder.messageImage.setClickable(false);
        } else if (message_type.equals("image"))
        {

            viewHolder.messageImage.setClickable(true);
            viewHolder.displayTime.setText(getDate(String.valueOf(c.getTime())));
            Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage()).resize(400, 0).into(viewHolder.messageImage);
            viewHolder.messageImage.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(final View view)
                {

                    Handler handler = new Handler();
                    handler.post(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            int position = viewHolder.getAdapterPosition();
                            new PhotoFullPopupWindow(view.getContext(), R.layout.popup_photo_full, viewHolder.messageImage, mMessageList.get(position).getMessage(), null);

                        }
                    });


                }
            });


        } else
        {
            viewHolder.messageImage.setClickable(true);
            viewHolder.displayTime.setText(getDate(String.valueOf(c.getTime())));
            Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage()).resize(400, 0).into(viewHolder.messageImage);
            viewHolder.messageImage.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {

                    Double myLatitude = chatActivity.myLocation.getLatitude();
                    Double myLongitude = chatActivity.myLocation.getLongitude();
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:<" + myLatitude + ">,<" + myLongitude + ">?q=<" + myLatitude + ">,<" + myLongitude + ">"));

                    chatActivity.startActivity(intent);
                }
            });
        }


    }


    @Override
    public int getItemViewType(int position)
    {
        Messages message = mMessageList.get(position);
        if (message.getFrom().equals(mCurrentUserId))
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

    public String getMy_url()
    {
        return my_url;
    }

    public void setMy_url(String my_url)
    {
        this.my_url = my_url;
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

            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            displayTime = (TextView) view.findViewById(R.id.time_text_layout);


        }


    }

}
