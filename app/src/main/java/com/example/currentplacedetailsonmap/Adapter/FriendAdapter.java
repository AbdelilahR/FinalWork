package com.example.currentplacedetailsonmap.Adapter;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.currentplacedetailsonmap.Model.Friends;
import com.example.currentplacedetailsonmap.Model.FriendsViewHolder;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.Model.UserViewHolder;
import com.example.currentplacedetailsonmap.Model.Utility;
import com.example.currentplacedetailsonmap.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Abdel-Portable on 18-04-18.
 * source: http://tutos-android-france.com/listview-afficher-une-liste-delements/
 */
public class FriendAdapter extends ArrayAdapter<Friends>
{


    public String metric_symbol = "m";
    public Location myLocation = new Location("");
    public Location userLocation = new Location("");
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private LocationManager mLocationManager;
    private ArrayList<Friends> friends;

    public FriendAdapter(@NonNull Context context, ArrayList<Friends> friends)
    {
        super(context, 0, friends);
        this.friends = friends;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_friend_listview, parent, false);

        FriendsViewHolder friendsVH = (FriendsViewHolder) convertView.getTag();
        if (friendsVH == null)
        {
            friendsVH = new FriendsViewHolder();
            friendsVH.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            friendsVH.text = (TextView) convertView.findViewById(R.id.text);
            friendsVH.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            friendsVH.status = (ImageView) convertView.findViewById(R.id.status);
            friendsVH.cancel_accept = (Button) convertView.findViewById(R.id.cancel_accept_friendReq);
            friendsVH.decline = (Button) convertView.findViewById(R.id.decline_friendReq);
            convertView.setTag(friendsVH);
        }
        final Friends friends = getItem(position);
        /** Source: https://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
         */

        myLocation = Utility.getLastKnownLocation(this.getContext());
        userLocation.setLatitude(friends.getUser().getAdress().getLatitude());
        userLocation.setLongitude(friends.getUser().getAdress().getLongitude());

        float distanceInMeters = myLocation.distanceTo(userLocation);

        if (distanceInMeters > 1000)
        {
            distanceInMeters = (distanceInMeters / 1000);
            metric_symbol = "km";
        } else
        {
            metric_symbol = "m";
        }

        friendsVH.text.setText(Float.valueOf(Utility.round(distanceInMeters, 2)).toString() + " " + metric_symbol);

        friendsVH.pseudo.setText(friends.getUser().getVoornaam() + " " + friends.getUser().getAchternaam());

        if (friends.getUser().getAvatar().equals("default"))
            friendsVH.avatar.setImageResource(R.drawable.default_avatar);
        else
            Picasso.with(friendsVH.avatar.getContext()).load(friends.getUser().getAvatar()).resize(50, 50).into(friendsVH.avatar);
//  Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage()).resize(400, 0).into(viewHolder.messageImage);

        String online = friends.getUser().getStatus();
        if (online.equals("online"))
            friendsVH.status.setImageResource(R.drawable.online_icon);

        else
            friendsVH.status.setImageResource(R.drawable.offline_icon);


        if (friends.getRequest().equals("sent"))
        {
            //Cancel Friend Request
            friendsVH.cancel_accept.setText("Cancel Request");
            friendsVH.cancel_accept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    DatabaseReference request_currentUser = FirebaseDatabase.getInstance().getReference("Friends").child(auth.getUid()).child(friends.getUser().getUserId());
                    DatabaseReference request_selectedUser = FirebaseDatabase.getInstance().getReference("Friends").child(friends.getUser().getUserId()).child(auth.getUid());
                    request_currentUser.removeValue();
                    request_selectedUser.removeValue();

                }
            });
            friendsVH.decline.setVisibility(View.GONE);
            //friendsVH.decline.setText("Decline Request");
        } else if (friends.getRequest().equals("received"))
        {
            friendsVH.decline.setVisibility(View.VISIBLE);
            //Accept Friend Request
            friendsVH.cancel_accept.setText("Accept Request");
            friendsVH.cancel_accept.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    DatabaseReference acceptedRequest_currrentUser = FirebaseDatabase.getInstance().getReference("Friends").child(auth.getUid());
                }
            });

            //Decline Friend Request
            friendsVH.decline.setText("Decline Request");
            friendsVH.decline.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    DatabaseReference request_currentUser = FirebaseDatabase.getInstance().getReference("Friends").child(auth.getUid()).child(friends.getUser().getUserId());
                    DatabaseReference request_selectedUser = FirebaseDatabase.getInstance().getReference("Friends").child(friends.getUser().getUserId()).child(auth.getUid());
                    request_currentUser.removeValue();
                    request_selectedUser.removeValue();

                }
            });
        }

        return convertView;
    }


}
