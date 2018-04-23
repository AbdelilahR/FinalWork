package com.example.currentplacedetailsonmap.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.Model.UserViewHolder;
import com.example.currentplacedetailsonmap.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;
import static android.content.Context.LOCATION_SERVICE;
import static android.location.LocationManager.GPS_PROVIDER;

/**
 * Created by Abdel-Portable on 18-04-18.
 * source: http://tutos-android-france.com/listview-afficher-une-liste-delements/
 */
public class UserAdapter extends ArrayAdapter<User> {

    private FusedLocationProviderClient mFusedLocationProviderClient;

    public String metric_symbol = "m";
    public Location myLocation = new Location("");
    public Location userLocation = new Location("");
    private LocationManager mLocationManager;

    public UserAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);


    }

    public final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }


        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }


        @Override
        public void onProviderEnabled(String provider) {

        }


        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //super.getView(position, convertView, parent);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_user_listview, parent, false);
        // Activity a = (Activity) getContext();

        UserViewHolder userVH = (UserViewHolder) convertView.getTag();
        if (userVH == null) {
            userVH = new UserViewHolder();
            userVH.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            userVH.text = (TextView) convertView.findViewById(R.id.text);
            userVH.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            convertView.setTag(userVH);
        }
        User user = getItem(position);
        userVH.pseudo.setText(user.getVoornaam() + " " + user.getAchternaam());
        /** Source: https://stackoverflow.com/questions/2741403/get-the-distance-between-two-geo-points?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa
         */

        // myLocation = locationResult.getResult();
      //  mLocationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        //mLocationManager.requestLocationUpdates(GPS_PROVIDER, 60, 100, mLocationListener);
        myLocation = getLastKnownLocation();
        userLocation.setLatitude(user.getAdress().getLatitude());
        userLocation.setLongitude(user.getAdress().getLongitude());
        float distanceInMeters = myLocation.distanceTo(userLocation);
        //userVH.text.setText(user.getAdress().toString());
        userVH.text.setText(Float.valueOf(distanceInMeters).toString() + " " + metric_symbol);
        userVH.avatar.setImageDrawable(new ColorDrawable(Color.GREEN));
        return convertView;
    }

    private Location getLastKnownLocation() {
        /** https://stackoverflow.com/questions/20438627/getlastknownlocation-returns-null?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa */
        mLocationManager = (LocationManager) getContext().getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
