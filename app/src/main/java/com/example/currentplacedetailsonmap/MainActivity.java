package com.example.currentplacedetailsonmap;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.currentplacedetailsonmap.Fragment.HomeFragment;
import com.example.currentplacedetailsonmap.Fragment.UserFragment;

/**
 * An activity that displays a map showing the place at the device's current location.
 * test
 */
public class MainActivity extends AppCompatActivity {


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_content, homeFragment);
        transaction.commit();
        setContentView(R.layout.activity_main);

        // code used from this site:
        // https://segunfamisa.com/posts/bottom-navigation-view-android
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                UserFragment userFragment = new UserFragment();
                HomeFragment homeFragment = new HomeFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                switch (item.getItemId()) {
                    //Todo implement fragments

                    case R.id.navigation_home:
                       transaction.replace(R.id.main_content, homeFragment);
                        transaction.commit();

                         /*
                        // android.support.v4.app.Fragment fragmentTransaction = getSupportFragmentManager().findFragmentById(R.id.main_content);
                        UserFragment userFragment = new UserFragment();
                        StatsFragment homeFragment = new StatsFragment();
                        HomeFragment homeFragment = new HomeFragment();

                        FragmentManager fm = getSupportFragmentManager();
                        // fm.beginTransaction().replace(R.id.main_map,homeFragment,homeFragment.getTag()).commit();
                        //fm.beginTransaction().replace(R.id.main_content,homeFragment,homeFragment.getTag()).commit();
                        */

                        break;
                    case R.id.navigation_dashboard:

                        break;
                    case R.id.navigation_notifications:
                        transaction.replace(R.id.main_content, userFragment, userFragment.getTag());

                        transaction.commit();
                        break;
                }

            }
        });
        //TODO Remove this line when the tests are done
        //FirebaseAuth.getInstance().signOut();
    }
}