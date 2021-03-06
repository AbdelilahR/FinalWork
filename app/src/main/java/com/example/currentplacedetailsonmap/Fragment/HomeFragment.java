package com.example.currentplacedetailsonmap.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.currentplacedetailsonmap.Activity.ChatActivity;
import com.example.currentplacedetailsonmap.MainActivity;
import com.example.currentplacedetailsonmap.Model.Address;
import com.example.currentplacedetailsonmap.Model.DirectionsJSONParser;
import com.example.currentplacedetailsonmap.Model.Friends;
import com.example.currentplacedetailsonmap.Model.Statistiek;
import com.example.currentplacedetailsonmap.Model.User;
import com.example.currentplacedetailsonmap.Model.Utility;
import com.example.currentplacedetailsonmap.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

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

import info.hoang8f.widget.FButton;


/**
 * A simple {@link Fragment} subclass.
 * https://stackoverflow.com/questions/42619863/how-to-calculate-distance-every-15-sec-with-using-gps-heavy-accuracy
 * https://stackoverflow.com/questions/41601147/get-last-node-in-firebase-database-android
 * https://stackoverflow.com/questions/20550016/savedinstancestate-is-always-null-in-fragment/41388475
 * <p>
 * Formula user for calories burned
 * https://fitness.stackexchange.com/questions/15608/energy-expenditure-calories-burned-equation-for-running
 * World avg = 62 => https://en.wikipedia.org/wiki/Human_body_weight
 */
public class HomeFragment extends Fragment implements OnMapReadyCallback {


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final String BUNDLE_LAST_ID = "last_id";
    // Used for selecting the current place.
    private static final int M_MAX_ENTRIES = 5;
    public static View popup;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted. test
    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    public ArrayList<LatLng> userlocations_list = null;
    //Speedometer
    public SensorManager sManager;
    public Sensor stepSensor;
    public long offset = 0;
    public boolean start = true;
    public boolean pause = true;
    public LatLng oldPosition;
    public LatLng newPosition;
    public float[] results = new float[1];
    public float correctedDistance = 0;
    public TextView distance;
    public TextView metric;
    public TextView kcal;
    public TextView calories;
    public LatLng myCurrentPosition;
    //Custom
    long time = 0;
    float mCalories = 0f;
    private long plus_one_minute = 1;
    private long ONE_MINUTE = plus_one_minute * 60 * 1000;
    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    // The entry points to the Places API.
    private GeoDataClient mGeoDataClient; /* provides access to Google's database of local place */
    private PlaceDetectionClient mPlaceDetectionClient; /* provides quick access to the device's current place + report the location*/
    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;
    private Location mNewLocation;
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private float timeWhenStopped;
    private float distanceInMeters;
    private FirebaseAuth auth;
    private String current_user;
    private int last_id;
    private boolean position_enable;
    private ArrayList<Friends> friendsList = new ArrayList<>();
    private Friends friend = new Friends();
    private MarkerOptions markerOptions = new MarkerOptions();
    private User added_user;
    private Bitmap _default;
    private Bitmap bitmap;
    private ArrayList<MarkerOptions> listOf_markersOptions = new ArrayList<>();
    private ArrayList<Marker> listOf_markers = new ArrayList<>();
    private SupportMapFragment mapFragment;
    private HttpURLConnection connection;
    private Thread helper_thread;
    private String avatar_url;
    private MarkerOptions goal;
    private Chronometer chrono;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private boolean locationHasChanger;
    private String metric_symbol;
    private LocationManager manager;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_google_maps, container, false);
        // Build the map.
        final LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Check if GPS is enabled
        manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            buildAlertMessageNoGps();
        else
            Toast.makeText(getActivity(), "Please enable your localisation for the app to work properly", Toast.LENGTH_SHORT);


        //Start Pause button
        final FButton btnStart = (FButton) view.findViewById(R.id.btn_start);
        final FButton btnPause = (FButton) view.findViewById(R.id.btn2);

        //Chronometer
        chrono = (Chronometer) view.findViewById(R.id.chronometer);

        //speedometer
        sManager = (SensorManager)

                getActivity().

                        getSystemService(Context.SENSOR_SERVICE);

        stepSensor = sManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        distance = (TextView) view.findViewById(R.id.distance);
        metric = (TextView) view.findViewById(R.id.metre);
        kcal = (TextView) view.findViewById(R.id.kcal);
        calories = (TextView) view.findViewById(R.id.calories);

        final DatabaseReference database = FirebaseDatabase.getInstance().getReference().child("Stats").child(current_user);
        locationManager = (LocationManager)

                getActivity().

                        getSystemService(Context.LOCATION_SERVICE);

        btnPause.setEnabled(false);
        btnStart.setOnClickListener(new View.OnClickListener()

        {

            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {

                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    buildAlertMessageNoGps();
                else {
                    if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                        getDeviceLocation();
                }


                if (start == true) {

                    locationListener = new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {

                            if (mLastKnownLocation != null && location != null) {
                                distanceInMeters += mLastKnownLocation.distanceTo(location);
                                float one_mile = 0.000621371f;

                                //TODO update directions when Goal is chosen/
                                /*
                                if (goal != null)
                                {
                                    //refresh map
                                    mMap.clear();
                                    addHeatMap();
                                    mMap.addMarker(goal);
                                    String url = getDirectionsUrl(new LatLng(location.getLatitude(), location.getLongitude()), goal.getPosition());
                                    new DownloadTask().execute(url);
                                }
                                */
                                sendLocation(location);
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
                                //update burnedcalories
                                time = (SystemClock.elapsedRealtime() - chrono.getBase());
                                if (time > ONE_MINUTE) {
                                    mCalories = (0.2f * distanceInMeters) + 3.5f;
                                    plus_one_minute++;
                                    if (mCalories < 1000f) {
                                        calories.setText(String.valueOf(Utility.round(mCalories, 2)));
                                        kcal.setText("cal");
                                    } else {
                                        calories.setText(String.valueOf(Utility.round(mCalories, 2)) + " kcal");
                                        kcal.setText("kcal");
                                    }
                                } else {
                                    Toast.makeText(getContext(), "Run at least 1m", Toast.LENGTH_SHORT).show();

                                }
                                if (distanceInMeters < 1000f) {
                                    distance.setText(String.valueOf(Utility.round(distanceInMeters, 2)));
                                    metric.setText("m");
                                } else {
                                    distance.setText(String.valueOf(Utility.round((distanceInMeters / 1000), 2)));
                                    metric.setText("km");
                                }


                                Log.d("Location Updates", "Calories: " + String.valueOf(mCalories) + " Distance: " + String.valueOf(distanceInMeters));
                            }
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {

                        }

                        @Override
                        public void onProviderDisabled(String s) {

                        }
                    };

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 100, locationListener);
                    btnStart.setText("Stop");
                    btnStart.setButtonColor(getResources().getColor(R.color.stopcolor));
                    chrono.start();
                    btnPause.setEnabled(true);
                    start = false;
                    //clearLists();

                } else {
                    locationManager.removeUpdates(locationListener);
                    if (mLastKnownLocation != null)
                        sendLocation(mLastKnownLocation);

                    chrono.stop();
                    time = (SystemClock.elapsedRealtime() - chrono.getBase());
                    start = true;
                    timeWhenStopped = 0;
                    //set variable last_id
                    ++last_id;
                    Statistiek statistiek = new Statistiek("Session", last_id, time, (int) mCalories, distanceInMeters, Utility.getTime());
                    chrono.setBase(SystemClock.elapsedRealtime() - offset);
                    btnStart.setText("Start");
                    btnStart.setButtonColor(getResources().getColor(R.color.fbutton_color_turquoise));
                    btnStart.setShadowColor(getResources().getColor(R.color.fbutton_color_emerald));
                    btnPause.setEnabled(false);
                    distance.setText("0");
                    calories.setText("0");

                    mCalories = 0f;
                    distanceInMeters = 0f;
                    mLastKnownLocation = Utility.getLastKnownLocation(getContext());
                    database.push().setValue(statistiek);


                }


            }


        });

        btnPause.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                if (pause == true) {

                    timeWhenStopped = chrono.getBase() - SystemClock.elapsedRealtime();
                    chrono.stop();

                    btnPause.setText("Resume");
                    btnPause.setButtonColor(getResources().getColor(R.color.fbutton_color_carrot));
                    btnPause.setShadowColor(getResources().getColor(R.color.fbutton_color_pumpkin));

                    pause = false;
                } else {
                    chrono.setBase(SystemClock.elapsedRealtime() + (int) timeWhenStopped);
                    chrono.start();
                    btnPause.setText("Pause");
                    btnPause.setButtonColor(getResources().getColor(R.color.fbutton_color_orange));
                    btnPause.setShadowColor(getResources().getColor(R.color.fbutton_color_sun_flower));
                    pause = true;
                }


            }
        });


        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // Put a marker on the map for every friend of the user
        loadFriendList_onMap();

        // Set a heatmap layer on the map
        addHeatMap();

    }

    private void setLAstSessionId() {

        class loadLastId extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... strings) {
                final int[] get_id = {0};
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Stats").child(current_user);
                Query lastQuery = reference.orderByKey().limitToLast(1);
                lastQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dss : dataSnapshot.getChildren()) {
                            get_id[0] = dss.child("id").getValue(int.class);
                            setLast_id(get_id[0]);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                return "Executed";
            }
        }
        new loadLastId().execute("");

    }


    private void addHeatMap() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("DataMap");
        userlocations_list = new ArrayList<>();
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Address user_location = dataSnapshot.getValue(Address.class);

                // Get the data: latitude/longitude positions of all users.
                LatLng location = new LatLng(user_location.getLatitude(), user_location.getLongitude());

                userlocations_list.add(location);

                if (userlocations_list != null && !userlocations_list.isEmpty()) {
                    // Create a heat map tile provider, passing it the latlngs of the police stations.
                    mProvider = new HeatmapTileProvider.Builder().data(userlocations_list).build();

                    // Add a tile overlay to the map, using the heat map tile provider.
                    if (mMap != null)
                        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //https://stackoverflow.com/questions/30847096/android-getmenuinflater-in-a-fragment-subclass-cannot-resolve-method
        //https://stackoverflow.com/questions/15653737/oncreateoptionsmenu-inside-fragments
        inflater.inflate(R.menu.current_place_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        //return true;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().setTitle("Home");
        setRetainInstance(true);


        // Firebase variables
        auth = FirebaseAuth.getInstance();
        current_user = auth.getUid();

        //initialize default avatar
        _default = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.default_avatar);

        // Retrieve last id from firebase and initialize it
        setLAstSessionId();

        // Retrieve location and camera position from saved instance state + last id.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mNewLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
            last_id = savedInstanceState.getInt(BUNDLE_LAST_ID);
        }


        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(getActivity(), null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(getActivity(), null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            outState.putParcelable(KEY_LOCATION, mNewLocation);
            outState.putInt(BUNDLE_LAST_ID, last_id);
            super.onSaveInstanceState(outState);
        }
    }

    /**
     * Sets up the options menu.
     *
     * @param menu The options menu.
     * @return Boolean.
     */


    /**
     * Handles a click on the menu option to get a place.
     *
     * @param item The menu item to handle.
     * @return Boolean.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item.getItemId() == R.id.option_get_place) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                getDeviceLocation();
                LayoutInflater inflater = LayoutInflater.from(getActivity());

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LinearLayout layout = new LinearLayout(getActivity());
                builder.setTitle("Chose your end Goal");


                try {
                    popup = inflater.inflate(R.layout.custom_popup, layout);


                } catch (InflateException e) {
                    e.getMessage();

                }
                // final EditText input = (EditText) popup.findViewById(R.id.editText1);
                //final EditText input2 = (EditText) popup.findViewById(R.id.editText2);

                final TextView txt1 = new TextView(getActivity());
                final TextView txt2 = new TextView(getActivity());
                txt1.setText("Point A");
                txt2.setText("Point B");

                PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                        getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

                AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                        .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                        .build();
                autocompleteFragment.setFilter(typeFilter);
                mMap.clear();
                addHeatMap();
                autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                    @Override
                    public void onPlaceSelected(final Place place) {
                        Log.i(TAG, "Place: " + place.getName());//get place details here
                        getDeviceLocation();
                        myCurrentPosition = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        String url = getDirectionsUrl(myCurrentPosition, place.getLatLng());
                        new DownloadTask().execute(url);
                        goal = new MarkerOptions().title("Goal").position(place.getLatLng());
                        mMap.addMarker(goal);

                    }

                    @Override
                    public void onError(Status status) {
                        Log.i(TAG, "An error occurred: " + status);
                    }
                });

                if (popup.getParent() != null)
                    ((ViewGroup) popup.getParent()).removeView(popup);

                builder.setView(popup);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // String m_Text = input.getText().toString();
                        //String m_Text2 = input2.getText().toString();

                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });
            /*

             */
                builder.show();
            }
            else
                buildAlertMessageNoGps();
        }

        return true;
    }

    /**
     * Manipulates the map when it's available.
     * This callback is triggered when the map is ready to be used.
     */
    @Override
    public void onMapReady(final GoogleMap map) {
        mMap = map;


        // Use a custom info window adapter to handle multiple lines of text in the
        // info window contents.
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) getActivity().findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });

        // Prompt the user for permission.
        //getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.

//        getDeviceLocation();

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                if (mOverlay != null)
                    mOverlay.clearTileCache();

                if (mProvider != null) {
                    if (map.getCameraPosition().zoom >= 10)
                        mProvider.setRadius(150);
                    else
                        mProvider.setRadius(HeatmapTileProvider.DEFAULT_RADIUS);

                }

                //Log.d("ZOOM LEVEL", "zoom leve is " + zoom);
            }
        });


        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                User user = null;
                String user_name;
                String friend_name = marker.getTitle();
                if (friendsList != null)
                    for (Friends friends : friendsList) {
                        user_name = friends.getUser().getVoornaam() + " " + friends.getUser().getAchternaam();
                        if (user_name.equals(friend_name))
                            user = friends.getUser();
                    }
                if (user != null) {
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    intent.putExtra("selectedUser", user);
                    startActivity(intent);
                }
            }
        });
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    public void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();


                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });

            }

        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getActivity().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                mNewLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    //get distance in meters
    public double getDistanceRun(long steps) {
        //float distance = ((float) (steps * 78) / (float) 100000) * 1000;
        //int round =(int) distance;
        return steps * 0.762;

    }

    /**
     * Function to check if best network provider
     * @return boolean
     * */


    /**
     * Function to show settings alert dialog
     */
    public void buildAlertMessageNoGps() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                position_enable = true;
                mLastKnownLocation = Utility.getLastKnownLocation(getActivity());
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                position_enable = false;
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public int getLast_id() {
        return last_id;
    }

    public void setLast_id(int last_id) {
        this.last_id = last_id;
    }

    /**
     * This function will show all the friends of the user on Google map
     */
    private void loadFriendList_onMap() {

        class loadFriendList_onMapAsync extends AsyncTask<String, Void, String> {


            @Override
            protected String doInBackground(String... strings) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(auth.getUid());

                reference.addListenerForSingleValueEvent(new ValueEventListener()

                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            added_user = dsp.getValue(User.class);
                            String request_type = dsp.child("request_type").getValue().toString();
                            friend = new Friends(request_type, added_user);
                            friendsList.add(friend);
                        }
                        if (friendsList != null) {
                            for (Friends myFriend : new ArrayList<Friends>(friendsList)) {
                                if (myFriend.getRequest().equals("Accepted")) {
                                    markerOptions.position(new LatLng(myFriend.getUser().getAdress().getLatitude(), myFriend.getUser().getAdress().getLongitude()));
                                    markerOptions.title(myFriend.getUser().getVoornaam() + " " + myFriend.getUser().getAchternaam());
                                    String url = myFriend.getUser().getAvatar();
                                    if (url.equals("default")) {
                                        bitmap = Bitmap.createScaledBitmap(_default, 100, 100, true);
                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                                        mMap.addMarker(markerOptions);

                                    } else {
                                        avatar_url = myFriend.getUser().getAvatar();
                                        helper_thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    URL _url = new URL(avatar_url);
                                                    connection = (HttpURLConnection) _url.openConnection();
                                                    connection.setDoInput(true);
                                                    connection.connect();
                                                    InputStream input = connection.getInputStream();
                                                    Bitmap myBitmap = BitmapFactory.decodeStream(input);
                                                    Bitmap resized_bitmap = Bitmap.createScaledBitmap(myBitmap, 100, 100, true);
                                                    if (myBitmap != null) {
                                                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resized_bitmap));

                                                    }
                                                    //return myBitmap;
                                                } catch (IOException e) {
                                                    // Log exception
                                                    //return null;
                                                }
                                            }

                                        });
                                        helper_thread.start();
                                        try {
                                            helper_thread.join();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }

                                        mMap.addMarker(markerOptions);
                                    }

                                }
                            }

                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


                return "Executed";
            }

            @Override
            protected void onPostExecute(String s) {

            }
        }
        new loadFriendList_onMapAsync().execute();

    }


    /**
     * this function will update the current location of the user to all his friends
     * source used => https://stackoverflow.com/questions/33315353/update-specific-values-using-firebase-for-android
     */
    private void sendLocation(Location currentLocation) {


        if (friendsList != null && !friendsList.isEmpty())
            for (Friends friend : friendsList) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Friends").child(friend.getUser().getUserId());
                reference.child(auth.getUid()).child("adress").child("latitude").setValue(currentLocation.getLatitude());
                reference.child(auth.getUid()).child("adress").child("longitude").setValue(currentLocation.getLongitude());

            }
        loadFriendList_onMap();

    }

    /**
     * source => https://www.journaldev.com/13373/android-google-map-drawing-route-two-points
     * --- Start ---
     */

    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";
        String mode = "mode=walking";
        //String mode = "mode=driving";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor + "&" + mode;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    private String downloadUrl(final String strUrl) throws IOException {


        String data = "";

        InputStream iStream = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setAllowUserInteraction(false);

            urlConnection.connect();// not working?
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK)
                Log.i(TAG, "connection ok");
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));//iStream null

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d(TAG, "Exception" + e.toString());
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        return data;
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {


        @Override
        public String doInBackground(String... url) {
            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }


        public void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();


            parserTask.execute(result);

        }
    }

    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap point = path.get(j);

                    double lat = Double.parseDouble((String) point.get("lat"));
                    double lng = Double.parseDouble((String) point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.RED);
                lineOptions.geodesic(true);

            }

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null)
                mMap.addPolyline(lineOptions);
            else
                Log.e("ERROR", "goal impossible");
        }

    }
/**
 * --- End ---
 */


}


