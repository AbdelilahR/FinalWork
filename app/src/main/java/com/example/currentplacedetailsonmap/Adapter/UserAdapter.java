package com.example.currentplacedetailsonmap.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.currentplacedetailsonmap.R;

import java.util.ArrayList;

/**
 * Created by Abdel-Portable on 18-04-18.
 * source: http://tutos-android-france.com/listview-afficher-une-liste-delements/
 */

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(@NonNull Context context, ArrayList<User> users) {
        super(context, 0, users);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //super.getView(position, convertView, parent);
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_user_listview, parent, false);

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
        userVH.text.setText(user.getAdress().toString());
        userVH.avatar.setImageDrawable(new ColorDrawable(Color.GREEN));
        return convertView;
    }
}
