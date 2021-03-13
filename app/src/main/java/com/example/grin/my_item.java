package com.example.grin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.ContentValues.TAG;

public class my_item extends AppCompatActivity implements OnMapReadyCallback {
    Uri contentUri;
    LatLng latLng;
    private GoogleMap mMap;
    private Toolbar toolbar;
    Double longitude,latitude;
    ImageView btnBack,imageDisplay,userImage;
    TextView itemTitle,desc,pickupTime,date,userName,noLikes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_item);
        imageDisplay= findViewById(R.id.itemPicture);
        userImage= findViewById(R.id.userProfile);
        itemTitle= findViewById(R.id.txtItemName);
        userName= findViewById(R.id.txtName);
        date= findViewById(R.id.txtTime);
        desc= findViewById(R.id.txtDescribtion);
        pickupTime= findViewById(R.id.txtPickupTime);
        noLikes= findViewById(R.id.noLike);
        longitude = new Double(getIntent().getExtras().getString("longitude"));
        latitude = new Double(getIntent().getExtras().getString("latitude"));
        latLng = new LatLng(latitude, longitude);

        showMeOnMap();
        Glide.with(imageDisplay.getContext()).load(getIntent().getExtras().getString("itemUri")).into(imageDisplay);
        Glide.with(userImage.getContext()).load(getIntent().getExtras().getString("userImage")).into(userImage);
        itemTitle.setText(getIntent().getExtras().getString("itemName"));
        userName.setText(getIntent().getExtras().getString("userName")+" is giving away");

        date.setText(getIntent().getExtras().getString("listingDate"));
        pickupTime.setText(getIntent().getExtras().getString("pickupTime"));
        desc.setText(getIntent().getExtras().getString("itemDescribtion"));
        noLikes.setText(getIntent().getExtras().getString("noLikes"));

        toolbar = findViewById(R.id.itemRequestToolbar);
        toolbar.setTitle(getIntent().getExtras().getString("itemName"));
        setSupportActionBar(toolbar);
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(my_item.this, MainContainer.class);
                startActivity(intent);
                finish();


            }
        });
    }
    catch (Exception ex){
        Log.d(TAG,"Data_Not_Shown:"+ex.toString());
    }



}
    public void showMeOnMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.locationMap);
        mapFragment.getMapAsync(this);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        try {
            mMap = map;

            if (latLng != null) {
                mMap.addMarker(new MarkerOptions().position(latLng));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
            }

        }
        catch (Exception ex)
        {
            Log.d("Exception: ","Exception: "+ex.toString());
        }

    }
}