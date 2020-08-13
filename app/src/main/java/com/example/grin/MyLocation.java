package com.example.grin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grin.Classes.ErrorMEssages;
import com.example.grin.Classes.User;
import com.example.grin.Classes.UserLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;

import java.util.HashMap;
import java.util.Map;

public class MyLocation extends FragmentActivity implements OnMapReadyCallback,
        LocationListener{

    private static final String TAG = MyLocation.class.getSimpleName();
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private GoogleMap mMap;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationRequest mLocationRequest;
    Double latitude=-33.8523341,longitude=151.2106085;
    LatLng latLng;
    // A default location (Sydney, Australia) and default zoom to use when location permission is
    // not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean locationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation=null;

    // Keys for storing activity state.
    // [START maps_current_place_state_keys]
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    GoogleApiClient mGoogleApiClient;

    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        latLng = defaultLocation;
        getUserLocaton();
    }
    public void getUserLocaton()
    {
        fAuth = FirebaseAuth.getInstance();
        final String userId = fAuth.getCurrentUser().getUid();
        rootnode=FirebaseDatabase.getInstance();
        reference=rootnode.getReference("users").child(userId);

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    longitude = snapshot.child("longitude").getValue(Double.class);
                    latitude = snapshot.child("latitude").getValue(Double.class);
                    latLng = new LatLng(latitude, longitude);
                    showMeOnMap();
                }
                else
                {
                    latLng=defaultLocation;
                    showMeOnMap();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        getDeviceLocation();
        showMeOnMap();
    }
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        // Add a marker in Sydney and move the camera
        if (latLng != null) {

            mMap.addMarker(new MarkerOptions().position(latLng).title(""));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));

        }

    }
    public void saveLocation()
    {

            fAuth = FirebaseAuth.getInstance();
            final String userId = fAuth.getCurrentUser().getUid();
            rootnode= FirebaseDatabase.getInstance();
            reference=rootnode.getReference("users").child(userId);
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
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
           /* reference.child(userId).setValue(saveLocaton).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        showMessage("Saving Location","Saving Location to firebase.");

                    }
                    else
                    {
                        showMessage("Error Saving",""+task.getException().toString());

                    }
                }
            });*/

    }
    public void saveHomeLocation(View view) {

        saveLocation();

    }
    public void locateMe(View view) {
        if (!locationPermissionGranted) {
            getLocationPermission();
        }
        getDeviceLocation();
    }



    public void showMeOnMap()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    protected  void buildGoogleApiClient() {

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .build();

    }
    protected void startLocationUpdate() {

        mLocationRequest =LocationRequest.create()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,mLocationRequest,this);
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
    private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {

                if(isLocationEnabled()) {
                     fusedLocationProviderClient.getLastLocation()
                            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        lastKnownLocation=location;
                                        latitude=location.getLatitude();
                                        longitude=location.getLongitude();
                                        latLng=new LatLng(location.getLatitude(), location.getLongitude());
                                        showMeOnMap();
                                    }
                                    else
                                    {
                                        startLocationUpdate();
                                        showMeOnMap();
                                    }
                                }
                            });
                }
                else
                {
                    showAlert();
                }
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }
    private boolean isLocationEnabled() {
        LocationManager locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

    }
    private void showAlert()
    {
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("Enable Location")
                .setMessage("Your Location is 'OFF' \nPlease enable your location to locate your device")
                .setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent myIntent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(myIntent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        dialog.show();
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
    private void getLocationPermission() {

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
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
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        if(locationPermissionGranted)
        {
            getDeviceLocation();
        }
    }
    // [END maps_current_place_on_request_permissions_result]

    // [START maps_current_place_update_location_ui]
    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation=location;
        if(location!=null)
        {
            latitude=location.getLatitude();
            longitude=location.getLongitude();
           latLng = new LatLng(lastKnownLocation.getLongitude(), lastKnownLocation.getAltitude());
        }
        else {
            lastKnownLocation=null;
            showMessage("Location Error: ", "Location Not Detected.");

        }

    }
}