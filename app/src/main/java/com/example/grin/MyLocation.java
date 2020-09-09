package com.example.grin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentActivity;

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
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grin.Classes.User;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.util.Map;
public class MyLocation extends AppCompatActivity implements OnMapReadyCallback,NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MyLocation.class.getSimpleName();

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitle("My Location");
        setSupportActionBar(toolbar);

        navigationView.bringToFront();
        ActionBarDrawerToggle toogle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toogle);
        toogle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        navigationView.setCheckedItem(R.id.nav_home);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        latLng = defaultLocation;
        mSearchText=(AutoCompleteTextView) findViewById(R.id.input_search);
        mSearchText.setAdapter(new PlaceAutoSuggestAdapter(MyLocation.this,android.R.layout.simple_list_item_1));
        mSearchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                geoLocate();
                hideSoftKeyboard();
            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH
                        || keyEvent.getKeyCode()== KeyEvent.ACTION_DOWN) {
                    geoLocate();
                    hideSoftKeyboard();
                }
                return  false;
            }


        });


        getUserLocaton();

    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                Intent intent=new Intent(MyLocation.this,DashBoard.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                Intent profileIntent=new Intent(MyLocation.this,UserProfile.class);
                startActivity(profileIntent);
                break;
            case R.id.nav_location:

                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent=new Intent(getApplicationContext(),Login.class);
                startActivity(loginIntent);
                finish();


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
    // and once again when the user makes a selection (for example when calling fetchPlace()).
   public void geoLocate(){

       Log.d(TAG,"geoLocate: geoLocating");
        String searchString=mSearchText.getText().toString();
        Geocoder geocoder=new Geocoder(MyLocation.this);
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));
           // Log.d(TAG,"geoLocate: found a location: " +address.toString());
            hideSoftKeyboard();

        }
    }
    private void hideSoftKeyboard()
    {
        View view=this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        }
    }

    public void getUserLocaton() {
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null) {
            final String userId = fAuth.getCurrentUser().getUid();
            rootnode = FirebaseDatabase.getInstance();
            reference = rootnode.getReference("users").child(userId);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {

                        if (snapshot.child("longitude").exists() && snapshot.child("latitude").exists()) {
                            longitude = snapshot.child("longitude").getValue(Double.class);
                            latitude = snapshot.child("latitude").getValue(Double.class);
                            latLng = new LatLng(latitude, longitude);
                            showMeOnMap();
                        } else {
                            latLng = defaultLocation;
                            showMeOnMap();
                        }
                    } else {
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
            latLng = defaultLocation;
            showMeOnMap();
        }
/*
        getCurrentLocation();
        showMeOnMap();*/
    }

    public void showMeOnMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        final Marker marker;
        // Add a marker in Sydney and move the camera
        if (latLng != null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));
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
            final String userId = fAuth.getCurrentUser().getUid();
            rootnode= FirebaseDatabase.getInstance();
            reference=rootnode.getReference("users").child(userId);
            latLng=mMap.getProjection().getVisibleRegion().latLngBounds.getCenter();
            longitude=latLng.longitude;
            latitude=latLng.latitude;
            UserLocation saveLocaton=new UserLocation(longitude,latitude);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Map<String, Object> postValues = new HashMap<String,Object>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        postValues.put(snapshot.getKey(),snapshot.getValue());
                    }
                    postValues.put("longitude", longitude);
                    postValues.put("latitude", latitude);
                    reference.updateChildren(postValues);
                    Toast.makeText(MyLocation.this, "Location Saved successfully.", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
    }
    public void saveHomeLocation(View view) {

        saveLocation();

    }
    public void locateMe(View view) {
        if (!locationPermissionGranted) {
            getLocationPermission();
        }
        else {
            if(isLocationEnable)
            {
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
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this.getApplicationContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
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

        SettingsClient settingsClient = LocationServices.getSettingsClient(MyLocation.this);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());
        task.addOnSuccessListener(MyLocation.this, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                isLocationEnable=true;
                getCurrentLocation();
            }
        });

        task.addOnFailureListener(MyLocation.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    isLocationEnable=false;
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    try {
                        resolvable.startResolutionForResult(MyLocation.this, 51);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==51)
        {
            if(resultCode==RESULT_OK)
            {
                isLocationEnable=true;
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
                                        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                                    }
                                };
                                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,null);
                            }
                        }
                        else
                        {
                            Toast.makeText(MyLocation.this,"Unable to get last location",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }




    protected  void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

    }

    @Override
    protected void onStart() {
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        super.onStart();
    }
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    private void showMessage(String title,String msg)
    {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
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