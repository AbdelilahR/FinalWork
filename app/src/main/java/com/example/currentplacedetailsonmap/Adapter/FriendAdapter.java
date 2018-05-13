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
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_user_listview, parent, false);

        FriendsViewHolder friendsVH = (FriendsViewHolder) convertView.getTag();
        if (friendsVH == null)
        {
            friendsVH = new FriendsViewHolder();
            friendsVH.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            friendsVH.text = (TextView) convertView.findViewById(R.id.text);
            friendsVH.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            friendsVH.status = (ImageView) convertView.findViewById(R.id.status);
            friendsVH.request = (Button) convertView.findViewById(R.id.request);
            convertView.setTag(friendsVH);
        }
        Friends friends = getItem(position);
        /** Source: https://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
         */

        myLocation = Utility.getLastKnownLocation(this.getContext());
        userLocation.setLatitude(friends.getAdress().getLatitude());
        userLocation.setLongitude(friends.getAdress().getLongitude());

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
        return convertView;
    }


}
