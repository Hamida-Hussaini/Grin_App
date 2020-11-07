package com.example.grin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grin.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;

public class AddListtingItem extends AppCompatActivity {
    private static final int PERMISSION_CODE=1000;
    private static final int PERMISSION_GALARY_CODE=1001;
    private static final int GALLERY_REQUEST_CODE=123;
    private static final int CAMERA_REQUEST_CODE=124;

    private TabLayout  tabLayout;
    private ViewPager viewPager;
    private Toolbar toolbar;
    ImageView btnBack;
    ImageView imageDisplay;
    Button btnPickImage;
    Button btnCaptureImage;
    Spinner spnrListFor,spnrShowUsers;
    Uri image_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listting_item);
        imageDisplay= findViewById(R.id.imageCapture);

        toolbar = findViewById(R.id.addListingToolbar);
        toolbar.setTitle("Add Listing");
        setSupportActionBar(toolbar);

        spnrListFor=(Spinner)findViewById(R.id.spnrListFor);
        ArrayAdapter spnrListForAdapter = ArrayAdapter.createFromResource(this,
                R.array.list_duration, R.layout.spinner_item);
        spnrListForAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrListFor.setAdapter(spnrListForAdapter);



        spnrShowUsers=(Spinner)findViewById(R.id.spnrShowAllUsers);
        ArrayAdapter spnrShowUsersAdapter = ArrayAdapter.createFromResource(this,
                R.array.show_all_users_duration, R.layout.spinner_item);
        spnrShowUsersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnrShowUsers.setAdapter(spnrShowUsersAdapter);

        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddListtingItem.this, DashBoard.class);
                startActivity(intent);
                finish();


            }
        });
        btnCaptureImage = findViewById(R.id.btn_captureImage);
        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions={Manifest.permission.CAMERA};

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    ActivityCompat.requestPermissions(AddListtingItem.this,
                            permissions,PERMISSION_CODE);
                }


            }
        });
        btnPickImage = findViewById(R.id.btnPickImage);
        btnPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] permissions={Manifest.permission.WRITE_EXTERNAL_STORAGE};

                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    openGalary();
                } else {
                    ActivityCompat.requestPermissions(AddListtingItem.this,
                            permissions,PERMISSION_GALARY_CODE);
                }


            }
        });
    }

    private void openCamera() {
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,0);
    }
    private void openGalary() {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Choose from gallery"),GALLERY_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK && data != null){
            Uri imageData =data.getData();

            imageDisplay.setImageURI(imageData);
        }
        else{ super.onActivityResult(requestCode, resultCode, data);

            Bitmap bitmap =(Bitmap)data.getExtras().get("data");
            imageDisplay.setImageBitmap(bitmap);

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                }else {
                    Toast.makeText(this,"Permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
            case PERMISSION_GALARY_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    openGalary();
                }else {
                    Toast.makeText(this,"Permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new UploadFoodFragment(),"Food");
        viewPagerAdapter.addFragment(new UploadNonfoodFragment(),"Non-food");
        viewPager.setAdapter(viewPagerAdapter);

    }
}