package com.example.currentplacedetailsonmap;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.currentplacedetailsonmap.Activity.LoginActivity;
import com.example.currentplacedetailsonmap.Fragment.HomeFragment;
import com.example.currentplacedetailsonmap.Fragment.StatsFragment;
import com.example.currentplacedetailsonmap.Fragment.UserFragment;
import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;

/**
 * An activity that displays a map showing the place at the device's current location.
 * test
 */
public class MainActivity extends AppCompatActivity {


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, homeFragment);
        transaction.commit();

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            // code used from this site:
            // https://segunfamisa.com/posts/bottom-navigation-view-android
            final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);

            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    UserFragment userFragment = new UserFragment();
                    HomeFragment homeFragment = new HomeFragment();
                    StatsFragment statsFragment = new StatsFragment();
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                    switch (item.getItemId()) {
                        //Todo implement fragments

                        case R.id.navigation_home:

                            transaction.replace(R.id.main_content, homeFragment);
                            transaction.commit();

                            break;
                        case R.id.navigation_stats:
                            transaction.replace(R.id.main_content, statsFragment);
                            transaction.commit();

                            break;
                        case R.id.navigation_users:

                            transaction.replace(R.id.main_content, userFragment, userFragment.getTag());
                            transaction.commit();

                            break;
                    }
                    return true;
                }
            });

            //TODO Remove this line when the tests are done
            //FirebaseAuth.getInstance().signOut();
        }
    }
}