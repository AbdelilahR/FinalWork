package com.example.currentplacedetailsonmap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * An activity that displays a map showing the place at the device's current location.
 * test
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;

    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient; /* provides access to Google's database of local place */
    private PlaceDetectionClient mPlaceDetectionClient; /* provides quick access to the device's current place + report the location*/

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private FusedLocationProviderClient mFusedLocationProviderClient2;

    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted. test
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private Location mNewLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    private String[] mLikelyPlaceNames;
    private String[] mLikelyPlaceAddresses;
    private String[] mLikelyPlaceAttributions;
    private LatLng[] mLikelyPlaceLatLngs;

    //Speedometer
    public SensorManager sManager;
    public Sensor stepSensor;
    private long steps = 0;

    //Custom
    int time = 0;
    double mCalories = 0;
    public long offset = 0;
    public Boolean firstTime = true;
    public LatLng oldPosition;
    public LatLng newPosition;
    public float[] results = new float[1];
    public float correctedDistance = 0;
    public TextView distance;
    public TextView calories;
    public static View popup;
    public LatLng myCurrentPosition;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //Todo implement fragments

                    case R.id.navigation_home:
                        GoogleMapsFragment googleMapsFragment = new GoogleMapsFragment();
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_content,googleMapsFragment);
                        transaction.commit();
                         /*
                        // android.support.v4.app.Fragment fragmentTransaction = getSupportFragmentManager().findFragmentById(R.id.main_content);
                        UserFragment userFragment = new UserFragment();
                        HomeFragment homeFragment = new HomeFragment();
                        GoogleMapsFragment googleMapsFragment = new GoogleMapsFragment();

                        FragmentManager fm = getSupportFragmentManager();
                        // fm.beginTransaction().replace(R.id.main_map,googleMapsFragment,googleMapsFragment.getTag()).commit();
                        //fm.beginTransaction().replace(R.id.main_content,homeFragment,homeFragment.getTag()).commit();
                        */

                        break;
                    case R.id.navigation_dashboard:

                        break;
                    case R.id.navigation_notifications:
                    UserFragment userFragment = new UserFragment();
                        FragmentTransaction transaction2 = getSupportFragmentManager().beginTransaction();
                        transaction2.replace(R.id.main_content,userFragment,userFragment.getTag());
                        transaction2.commit();
                        break;
                }

            }
        });
/*
        bottomNavigationView.setOnNavigationItemReselectedListener(); {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    //Todo implement fragments

                    case R.id.navigation_home:

                        // android.support.v4.app.Fragment fragmentTransaction = getSupportFragmentManager().findFragmentById(R.id.main_content);
                        UserFragment userFragment = new UserFragment();
                        HomeFragment homeFragment = new HomeFragment();
                        GoogleMapsFragment googleMapsFragment = new GoogleMapsFragment();

                        FragmentManager fm = getSupportFragmentManager();
                        // fm.beginTransaction().replace(R.id.main_map,googleMapsFragment,googleMapsFragment.getTag()).commit();
                        //fm.beginTransaction().replace(R.id.main_content,homeFragment,homeFragment.getTag()).commit();

                        return true;
                    case R.id.navigation_dashboard:

                        return true;
                    case R.id.navigation_notifications:

                        return true;
                }
                return false;
            }
        };
        */
/*
        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mNewLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        //TODO fix this
        setContentView(R.layout.activity_main);

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
       //Todo set this into nested child fragment

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync((OnMapReadyCallback) this);

        //Check if GPS is enabled
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            getDeviceLocation();
            updateLocationUI();
            // Set current location
            //   myCurrentPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

        } else {
            getLocationPermission();

        }


              updateLocationUI();
//        getLocationPermission();

        //Start button
        final Button btnStart = (Button) findViewById(R.id.btn_start);
        //Chronometer
        final Chronometer chrono = (Chronometer) findViewById(R.id.chronometer);

        //speedometer
        sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        distance = (TextView) findViewById(R.id.distance);
        calories = (TextView) findViewById(R.id.calories);

        // final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        onMapReady(mMap);
        // Set current location
        getDeviceLocation();
        getLocationPermission();
//        myCurrentPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());

        btnStart.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    buildAlertMessageNoGps();
                else
                    getDeviceLocation();

                if (firstTime == true) {
                    btnStart.setText("Stop");
                    //  distance.setText("0 m");

                    chrono.start();

                    firstTime = false;
                    onResume();
                } else {
                    chrono.stop();

                    time = (int) (SystemClock.elapsedRealtime() - chrono.getBase());
                    float distanceInMeters = results[0];
                    int burnedCalories = (int) mCalories;
                    mCalories = (0.0005 * 65 * distanceInMeters + 0.0035) * time;
                    calories.setText(Double.toString(burnedCalories) + " Kcal");
                    btnStart.setText("Start");
                    firstTime = true;

                    results[0] = 0;
                    //distance.setText(Float.toString(correctedDistance) + " m");
                    chrono.setBase(SystemClock.elapsedRealtime() - offset);
                    onStop();
                }


            }

        });
*/
    }
}