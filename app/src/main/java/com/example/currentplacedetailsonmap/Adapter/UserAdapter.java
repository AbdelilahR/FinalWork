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
import android.widget.ImageView;
import android.widget.TextView;

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
public class UserAdapter extends ArrayAdapter<User> {


    public String metric_symbol = "m";
    public Location myLocation = new Location("");
    public Location userLocation = new Location("");
    private LocationManager mLocationManager;
    private ArrayList<User> users;
    private float distanceInMeters;

    public UserAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
        this.users = users;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_user_listview, parent, false);

        UserViewHolder userVH = (UserViewHolder) convertView.getTag();
        if (userVH == null) {
            userVH = new UserViewHolder();
            userVH.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            userVH.text = (TextView) convertView.findViewById(R.id.text);
            userVH.avatar = (CircleImageView) convertView.findViewById(R.id.avatar);
            userVH.status = (ImageView) convertView.findViewById(R.id.status);


            convertView.setTag(userVH);
        }
        User user = getItem(position);
        /** Source: https://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
         */

        myLocation = Utility.getLastKnownLocation(this.getContext());
        userLocation.setLatitude(user.getAdress().getLatitude());
        userLocation.setLongitude(user.getAdress().getLongitude());
        if (myLocation != null && userLocation != null)
             distanceInMeters = myLocation.distanceTo(userLocation);

        if (distanceInMeters > 1000) {
            distanceInMeters = (distanceInMeters / 1000);
            metric_symbol = "km";
        } else {
            metric_symbol = "m";
        }

        userVH.text.setText(Float.valueOf(Utility.round(distanceInMeters, 2)).toString() + " " + metric_symbol);

        userVH.pseudo.setText(user.getVoornaam() + " " + user.getAchternaam());

        if (user.getAvatar().equals("default"))
            userVH.avatar.setImageResource(R.drawable.default_avatar);
        else
            Picasso.with(userVH.avatar.getContext()).load(user.getAvatar()).resize(50, 50).into(userVH.avatar);
//  Picasso.with(viewHolder.messageImage.getContext()).load(c.getMessage()).resize(400, 0).into(viewHolder.messageImage);

        String online = user.getStatus();
        if (online.equals("online"))
            userVH.status.setImageResource(R.drawable.vert);

        else
            userVH.status.setImageResource(R.drawable.rouge);
        return convertView;
    }


}
