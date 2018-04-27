package com.example.currentplacedetailsonmap.Adapter;

import android.app.Dialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.Activity.FullScreenActivity;
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
    private boolean zoomOut = false;
    private DatabaseReference mRootRef;


    private String my_url;

    private int teller = 0;

    public MessageAdapter(List<Messages> mMessageList, String selectedUserId, String mCurrentUserId)
    {

        this.mMessageList = mMessageList;
        this.selectedUserId = selectedUserId;
        this.mCurrentUserId = mCurrentUserId;
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
        boolean isImageFitToScreen;

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
            if (!c.getMessage().equals(""))
                viewHolder.messageText.setText(c.getMessage());
            viewHolder.displayTime.setText(getDate(String.valueOf(c.getTime())));

            viewHolder.messageImage.setVisibility(View.INVISIBLE);

            viewHolder.messageImage.setClickable(false);
        } else
        {

            viewHolder.messageText.setVisibility(View.INVISIBLE);
            viewHolder.messageImage.setClickable(true);
            viewHolder.displayTime.setText(getDate(String.valueOf(c.getTime())));
            Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage()).resize(300, 0).into(viewHolder.messageImage);


            viewHolder.messageImage.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View view)
                {
                    int position = viewHolder.getAdapterPosition();
                    new PhotoFullPopupWindow(view.getContext(), R.layout.popup_photo_full, viewHolder.messageImage, mMessageList.get(position).getMessage(), null);

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
            // profileImage = (CircleImageView) view.findViewById(R.id.custom_bar_image);
            displayName = (TextView) view.findViewById(R.id.name_text_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            displayTime = (TextView) view.findViewById(R.id.time_text_layout);


        }


    }
}
