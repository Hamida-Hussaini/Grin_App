package com.example.grin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.usage.UsageEvents;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grin.Classes.ErrorMEssages;
import com.example.grin.Classes.User;
import com.example.grin.Classes.UserLocation;
import com.example.grin.adapter.PlaceAutoSuggestAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
/*import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;*/
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.internal.$Gson$Preconditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class MyLocation extends FragmentActivity implements OnMapReadyCallback {

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        latLng = defaultLocation;
        mSearchText=(AutoCompleteTextView) findViewById(R.id.input_search);
        mSearchText.setAdapter(new PlaceAutoSuggestAdapter(MyLocation.this,android.R.layout.simple_list_item_1));
        mSearchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                latLng=getLatLngFromAddress(mSearchText.getText().toString());
                if(latLng!=null) {
                    Toast.makeText(MyLocation.this, "Location selected", Toast.LENGTH_SHORT).show();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16F));
                    hideSoftKeyboard();
                }
                else {
                    Log.d("Lat Lng","Lat Lng Not Found");
                }

            }
        });

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId== EditorInfo.IME_ACTION_SEARCH
                        || keyEvent.getKeyCode()== KeyEvent.ACTION_DOWN) {
                    geoLocate();

                }
                return  false;
            }


        });
        getUserLocaton();

    }
    private LatLng getLatLngFromAddress(String address){

        Geocoder geocoder=new Geocoder(MyLocation.this);
        List<Address> addressList;

        try {
            Log.d(TAG,"item selected");

            addressList = geocoder.getFromLocationName(address, 1);
            if(addressList!=null){
                Address singleaddress=addressList.get(0);
                latitude=singleaddress.getLatitude();
                longitude=singleaddress.getLongitude();
                latLng=new LatLng(singleaddress.getLatitude(),singleaddress.getLongitude());
                return latLng;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }

   /* private Address getAddressFromLatLng(LatLng latLng){
        Geocoder geocoder=new Geocoder(MyLocation.this);
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 5);
            if(addresses!=null){
                Address address=addresses.get(0);
                return address;
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }*/
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
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);

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

    // [END maps_current_place_on_request_permissions_result]


    /* private void getDeviceLocation() {
        try {
            if (locationPermissionGranted) {

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
                                    lastKnownLocation=null;
                                    startLocationUpdate();
                                    showMeOnMap();

                                }
                            }
                        });
               *//* if(isLocationEnabled()) {

                }
                else
                {
                    showAlert();
                }*//*
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
    }*/
}