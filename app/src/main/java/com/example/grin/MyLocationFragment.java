package com.example.grin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grin.Classes.UserLocation;
import com.example.grin.adapter.PlaceAutoSuggestAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyLocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyLocationFragment extends Fragment implements OnMapReadyCallback {
    private static final String TAG = MyLocationFragment.class.getSimpleName();
    ProgressBar progressBar;
    ImageView marker,locateMe;
    Button btnSaveHomeLocation;
    private GoogleMap mMap;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    Double latitude = -33.8523341, longitude = 151.2106085;
    LatLng latLng;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;
    private boolean isLocationEnable = false;
    private AutoCompleteTextView mSearchText;
    ImageView btnBack;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation = null;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    GoogleApiClient mGoogleApiClient;
    private LocationCallback locationCallback;
    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    //PlacesClient placesClient;
    private ImageView btnSearch;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyLocationFragment() {
        // Required empty public constructor
    }

    public static MyLocationFragment newInstance(String param1, String param2) {
        MyLocationFragment fragment = new MyLocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_my_location, container, false);

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // find views by id
        progressBar =view.findViewById(R.id.fragment_loginProgress);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
        latLng = defaultLocation;
        mSearchText=(AutoCompleteTextView)view.findViewById(R.id.fragment_input_search);
        marker=view.findViewById(R.id.fragment_marker);
        mSearchText.setAdapter(new PlaceAutoSuggestAdapter(getContext(),android.R.layout.simple_list_item_1));
        mSearchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                geoLocate();
                // hideSoftKeyboard();
            }
        });
        btnSaveHomeLocation=view.findViewById(R.id.fragment_btnSetHomeLocation);
        btnSaveHomeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                marker.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                saveLocation();

            }
        });
        locateMe=view.findViewById(R.id.fragment_locateMeImage);
        locateMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                locateMe();
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH
                        || keyEvent.getKeyCode()== KeyEvent.ACTION_DOWN) {
                    geoLocate();
                    ///hideSoftKeyboard();
                }
                return  false;
            }


        });

        getUserLocaton();

    }
    public void geoLocate(){
        marker.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG,"geoLocate: geoLocating");
        String searchString=mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(getContext(), Locale.getDefault());
        List<Address> list=new ArrayList<>();
        try {
            list= geocoder.getFromLocationName(searchString,1);

        }
        catch (IOException e) {
            Log.e(TAG,"geoLocate: IOException:"+e.getMessage());
        }
        if(list.size()>0) {
            Address address=list.get(0);
            latitude=address.getLatitude();
            longitude=address.getLongitude();
            latLng=new LatLng(latitude,longitude);
            showMeOnMap();
           // hideSoftKeyboard();

        }
    }
   /* private void hideSoftKeyboard()
    {
        View view=this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        }
    }*/

    public void getUserLocaton() {
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null) {
            progressBar.setVisibility(View.VISIBLE);
            marker.setVisibility(View.INVISIBLE);
            Log.d(TAG,"getUserLocation: getting Location");
            final String userId = fAuth.getCurrentUser().getUid();
            rootnode = FirebaseDatabase.getInstance();
            reference = rootnode.getReference("users").child(userId);
            Log.d(TAG,"reference: "+reference.toString());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.d(TAG,"DataExist: ");
                        if (snapshot.child("longitude").exists() && snapshot.child("latitude").exists()) {
                            longitude = snapshot.child("longitude").getValue(Double.class);
                            latitude = snapshot.child("latitude").getValue(Double.class);
                            latLng = new LatLng(latitude, longitude);

                            Log.d(TAG,"getUserLocation: Location Found."+latLng);
                            showMeOnMap();
                        } else {
                            Log.d(TAG,"getUserLocation: Location Not Found."+latLng);

                            latLng = defaultLocation;
                            showMeOnMap();
                        }
                    } else {
                        Log.d(TAG,"getUserLocation: User Not Found"+latLng);

                        latLng = defaultLocation;
                        showMeOnMap();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {

            Log.d(TAG,"getUserLocation: User Not Logged In"+latLng);
            latLng = defaultLocation;
            showMeOnMap();
        }
/*
        getCurrentLocation();
        showMeOnMap();*/
    }

    public void showMeOnMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.fragment_map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Add a marker in Sydney and move the camera
        if (latLng != null) {
            int TIME = 1000; //5000 ms (5 Seconds)

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                    progressBar.setVisibility(View.INVISIBLE);
                    marker.setVisibility(View.VISIBLE);
                }
            }, TIME);

        }
       /* mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLong) {

                latLng=latLong;
                latitude=latLong.latitude;
                longitude= latLong.longitude;

                // Clears the previously touched position
                mMap.clear();
                // Animating to the touched position
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        });*/
    }
    public void saveLocation()
    {
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null) {
            final String userId = fAuth.getCurrentUser().getUid();
            rootnode= FirebaseDatabase.getInstance();
            reference=rootnode.getReference("users").child(userId);

            latLng=mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
            longitude=latLng.longitude;
            latitude=latLng.latitude;
            Map<String, Object> hopperUpdates = new HashMap<>();
            hopperUpdates.put("longitude", longitude);
            hopperUpdates.put("latitude", latitude);
            reference.updateChildren(hopperUpdates);
          //  UserLocation saveLocaton=new UserLocation(longitude,latitude);
            progressBar.setVisibility(View.INVISIBLE);
            Log.d(TAG,"saving location");
            Toast.makeText(getActivity(), "Location Saved successfully.", Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(getContext(), "User not found", Toast.LENGTH_SHORT).show();
        }

    }
    public void locateMe() {
        marker.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG,"Locate Me");

        if (!locationPermissionGranted) {

            getLocationPermission();
        }
        else {
            if(isLocationEnable)
            {

                Log.d(TAG,"locateMe:");
                getCurrentLocation();
            }
            else
            {
                checkIfLocationEnabled();
            }
        }

    }
    private void getLocationPermission() {
        String[] permissions={Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    permissions,
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    // [START maps_current_place_on_request_permissions_result]
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length >0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            locationPermissionGranted = false;
                            return;
                        }
                        locationPermissionGranted = true;
                    }
                }
            }
        }
        if(locationPermissionGranted)
        {

            if(isLocationEnable)
            {
                marker.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                getCurrentLocation();

            }
            else
            {

                checkIfLocationEnabled();
            }
        }
    }
    private void checkIfLocationEnabled()
    {
        //check if gps is enabled or not and then request user to enable it.

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                isLocationEnable=true;
                marker.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                getCurrentLocation();
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    isLocationEnable=false;
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(getActivity(), 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==51)
        {
            if(resultCode==RESULT_OK)
            {
                isLocationEnable=true;
                marker.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                getCurrentLocation();
            }
            else
            {
                isLocationEnable=false;
            }
        }
    }
    @SuppressLint("MissingPermission")
    private void getCurrentLocation()
    {
        marker.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        fusedLocationProviderClient.getLastLocation()
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if(task.isSuccessful())
                        {
                            lastKnownLocation = task.getResult();
                            if(lastKnownLocation != null)
                            {
                                Log.d(TAG,"Location Found");
                                latitude=lastKnownLocation.getLatitude();
                                longitude=lastKnownLocation.getLongitude();
                                latLng=new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                showMeOnMap();



                            }
                            else
                            {
                                final LocationRequest locationRequest=LocationRequest.create();
                                locationRequest.setInterval(10000);
                                locationRequest.setFastestInterval(5000);
                                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                                locationCallback = new LocationCallback()
                                {
                                    @Override
                                    public void onLocationResult(LocationResult locationResult) {
                                        super.onLocationResult(locationResult);
                                        if(locationResult == null)
                                        {
                                            return;
                                        }
                                        lastKnownLocation = locationResult.getLastLocation();
                                        latitude=lastKnownLocation.getLatitude();
                                        longitude=lastKnownLocation.getLongitude();
                                        latLng=new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());

                                        showMeOnMap();
                                        Log.d(TAG,"LocationUpdate:Location Found");
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,null);
                            }
                        }
                        else
                        {

                            Toast.makeText(getContext(),"Unable to get last location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    protected  void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    public void onStart() {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        super.onStart();
    }
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void showMessage(String title,String msg)
    {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this.getContext());
        dialog.setTitle(""+title)
                .setMessage(""+msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialog.show();
    }
}