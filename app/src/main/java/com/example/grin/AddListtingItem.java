package com.example.grin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.renderscript.ScriptGroup;
import android.text.BoringLayout;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassifierEvent;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grin.Classes.Listing;
import com.example.grin.Classes.User;
import com.example.grin.Classes.common;
import com.example.grin.adapter.ViewPagerAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class AddListtingItem extends AppCompatActivity implements OnMapReadyCallback {
    private static final int GALLERY_PERM_CODE=100;
    private static final int CAMERA_PERM_CODE=101;
    public static final int CAMERA_REQUEST_CODE = 103;
    public static final int GALLERY_REQUEST_CODE = 104;
    String currentPhotoPath;
    Uri contentUri;
    String imageFileName;
    Dialog dialog;


    Uri firebaseImageUri;
    LatLng latLng;
    private GoogleMap mMap;
    private Toolbar toolbar;
    ImageView btnBack,imageDisplay;
    Button btnPickImage,btnCaptureImage,btnAddListing;
    RadioButton optFood,optNonFood; // initiate a radio button
    Switch wantedListing;
    TextView itemTitle,desc,quantity,lastDate,pickupTime;
    DatePickerDialog datePickerDialog;

    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    Double latitude = -33.8523341, longitude = 151.2106085;
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_listting_item);
        imageDisplay= findViewById(R.id.itemPicture);
        optFood = (RadioButton) findViewById(R.id.optFood); // initiate a radio button
        optNonFood = (RadioButton) findViewById(R.id.optNonFood); // initiate a radio button
        wantedListing=findViewById(R.id.checkType);
        toolbar = findViewById(R.id.addListingToolbar);
        toolbar.setTitle("Add Listing");
        setSupportActionBar(toolbar);
        contentUri=null;
        btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddListtingItem.this, MainContainer.class);
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
                    dispatchTakePictureIntent();
                } else {
                    ActivityCompat.requestPermissions(AddListtingItem.this,
                            permissions,CAMERA_PERM_CODE);
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
                            permissions,GALLERY_PERM_CODE);
                }


            }
        });
        btnAddListing=findViewById(R.id.btnAddListing);
        btnAddListing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ( !validateType() | !validateImagePath() | !validateTitle() | !validateDesc()  | !validateLastDate() | !validatePickupTime() | !validateQuantity()) {
                    return;

                }
                else {
                    if (contentUri != null) {
                        hideSoftKeyboard();
                        fAuth = FirebaseAuth.getInstance();
                        if(fAuth.getCurrentUser()!=null) {
                            addListingItem(contentUri);
                        }
                    }

                }
            }
        });
        itemTitle = findViewById(R.id.txtItemTitle);
        desc = findViewById(R.id.txtItemDesc);
        quantity = findViewById(R.id.txtQuantity);
        pickupTime = findViewById(R.id.txtPickupTime);
        lastDate = findViewById(R.id.txtLastDate);
        lastDate.setInputType(InputType.TYPE_NULL);
        lastDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               showDateDialog(lastDate);
            }
        });
        if(common.latitude!=null)
        {
            longitude = common.longitude;
            latitude = common.latitude;
            latLng = new LatLng(latitude, longitude);
            showMeOnMap();

        }
        else
        {
            getUserLocaton();
        }


                if(common.valuesSet==true)
                {
                        itemTitle.setText(common.title);
                        desc.setText(common.desc);
                        lastDate.setText(common.lastDate);
                        pickupTime.setText(common.pickupTime);
                        quantity.setText(Integer.toString(common.quantity));
                        optFood.setChecked(common.optFood);
                        optNonFood.setChecked(common.optNonFood);
                        wantedListing.setChecked(common.wantedListing);

                    contentUri=common.itemUri;
                    imageDisplay.setImageURI(contentUri);
                      /*  if(common.itemUri!=null)
                        {
                            contentUri=common.itemUri;
                            imageDisplay.setImageURI(contentUri);
                        }
                        else
                        {
                            contentUri=null;
                        }*/

                        common.valuesSet=false;
                    }



                }
        catch (Exception ex)
        {
            Toast.makeText(this,"Error: "+ex.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void showDateDialog(final TextView lastDate) {
        try {
            final Calendar calendar=Calendar.getInstance();
            DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    calendar.set(Calendar.YEAR,year);
                    calendar.set(Calendar.MONTH,month);
                    calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("dd-MM-yyyy");
                    lastDate.setText(simpleDateFormat.format(calendar.getTime()));
                }
            };
            new DatePickerDialog(AddListtingItem.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
        }
        catch (Exception ex)
        {
            Log.d("Exception","Date Eception: "+ex.toString());
        }
    }

    private Boolean validateTitle() {
        String val = itemTitle.getEditableText().toString();
        //String noWhiteSpace = "\\A\\w{3,20}\\z";
        if (val.isEmpty()) {
            itemTitle.setError("Title Cannot be blank");
            return false;
        }
        else {
            itemTitle.setError(null);
            return true;
        }
    }
    private Boolean validateType() {
        if (optFood.isChecked() == false && optNonFood.isChecked() == false) {

            Toast.makeText(this, "Please select item type", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
    private Boolean validateImagePath() {

        //String noWhiteSpace = "\\A\\w{3,20}\\z";
        if (contentUri==null) {
            Toast.makeText(this,"You must add an image.",Toast.LENGTH_SHORT).show();
            return false;
        }
        else {
            return true;
        }
    }
    private Boolean validateDesc() {
        String val = desc.getEditableText().toString();
        //String noWhiteSpace = "\\A\\w{3,20}\\z";
        if (val.isEmpty()) {
            desc.setError("Describtion cannot be blank");
            return false;
        }
        else {
            desc.setError(null);
            return true;
        }
    }
    private Boolean validateQuantity() {
        String val = quantity.getEditableText().toString();
        //String noWhiteSpace = "\\A\\w{3,20}\\z";
        if (val.isEmpty()) {
            quantity.setError("Quantity can not be blank");
            return false;
        }
        else if(Integer.parseInt(val)<=0 | Integer.parseInt(val)>5){
            quantity.setError("Quantity should be greater than 0 and less than 5");
            return false;
        }
        else {
            quantity.setError(null);
            return true;
        }
    }
    private Boolean validatePickupTime() {
        String val = pickupTime.getEditableText().toString();
        //String noWhiteSpace = "\\A\\w{3,20}\\z";
        if (val.isEmpty()) {
            pickupTime.setError("Field cannot be empty");
            return false;
        }
        else {
            pickupTime.setError(null);
            return true;
        }
    }
    private Boolean validateLastDate() {
        String val = lastDate.getEditableText().toString();
        //String noWhiteSpace = "\\A\\w{3,20}\\z";
        if (val.isEmpty()) {
            lastDate.setError("Field cannot be empty");
            return false;
        }
        else {
            lastDate.setError(null);
            return true;
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
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLong) {
                    setItemLocation();
                }
            });
        }
        catch (Exception ex)
        {
            Log.d("Exception: ","Exception: "+ex.toString());
        }

    }
    private void setItemLocation()
    {
        try {

                common.longitude=longitude;
                common.latitude=latitude;
                common.valuesSet=true;
                common.title=itemTitle.getEditableText().toString();
                common.desc=desc.getEditableText().toString();
                common.lastDate=lastDate.getEditableText().toString();
                common.pickupTime=pickupTime.getEditableText().toString();
                if(quantity.getEditableText().toString().isEmpty())
                {
                    common.quantity=0;
                }
                else {
                    common.quantity = Integer.parseInt(quantity.getEditableText().toString());
                }
                common.optFood=optFood.isChecked();
                common.optNonFood=optNonFood.isChecked();
                common.wantedListing=wantedListing.isChecked();
                if(contentUri!=null) {
                    common.itemUri = contentUri;
                }
                else
                {
                    common.itemUri=null;
                }
                Log.d(AddListtingItem.class.getSimpleName(),"Uri: "+contentUri);
                Intent intent = new Intent(AddListtingItem.this, setItemLocation.class);
                startActivity(intent);
                finish();
        }
        catch (Exception ex)
        {

            Log.d("Exception: ","Exception: "+ex.toString());
        }

    }

    private void addListing()
    {
        try {
                    final String title = itemTitle.getEditableText().toString().trim();
                    final String describtion = desc.getEditableText().toString().trim();
                    final int quan = Integer.parseInt(quantity.getEditableText().toString());
                    final String pTime = pickupTime.getEditableText().toString().trim();
                    final String lDate = lastDate.getEditableText().toString().trim();
                    final String wantListing ;
                    if (wantedListing.isChecked()) {
                        wantListing = "true";
                    } else {
                        wantListing = "false";
                    }
                    String itmType = "Not Set";
                    final String listingDate = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
                    final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    final String itmUri = firebaseImageUri.toString();
                    if (optFood.isChecked() == true) {
                        itmType = "Food";
                    } else if (optNonFood.isChecked() == true) {
                        itmType = "Non-Food";
                    }


                        rootnode = FirebaseDatabase.getInstance();
                        reference = rootnode.getReference("listing");
                        DatabaseReference pushedPostRef = reference.push();

                        String postId = pushedPostRef.getKey();


                        Listing obj = new Listing(title, itmType, describtion, quan, listingDate, pTime, lDate, itmUri, userId,
                                "Listed", 0, 0, 0, wantListing, longitude, latitude);

                        reference.child(postId).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    firebaseImageUri = null;
                                } else {
                                    Toast.makeText(AddListtingItem.this, "Error" + task.getException(), Toast.LENGTH_SHORT).show();
                                    Log.d("error", "Exception: " + task.getException());
                                }
                            }
                        });




        } catch (Exception ex) {
            Log.d("error","Exception"+ex.toString());

            Toast.makeText(this, "Exception: "+ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }
/*    private void uploadImageToFirebase(Uri contentUri) {
        try {
         getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            final ProgressDialog pd= new ProgressDialog(this);
            pd.setTitle("Adding Item...");
            pd.show();

            final String   randomKey=UUID.randomUUID().toString();
            final StorageReference imageRef=FirebaseStorage.getInstance().getReference().child("ListingPic/"+randomKey);
            imageRef.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseImageUri=uri;
                            addListing();
                            pd.dismiss();
                            Snackbar.make(findViewById(android.R.id.content),"Listing Addedd...",Snackbar.LENGTH_LONG).show();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed To Upload",Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent=(100.00*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    pd.setMessage("Percentage: "+(int)progressPercent+"%");
                }
            });
        }
        catch (Exception ex)
        {
            Toast.makeText(getApplicationContext(),"Exception:"+ex.toString(),Toast.LENGTH_LONG).show();
            Log.d("tag","Exception: "+ex.toString());
        }

    }*/
    private void openSuccessDialog()
    {
        try {
            dialog.setContentView(R.layout.success_dialog);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            Button btnOk=dialog.findViewById(R.id.btnOk);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    dialog.dismiss();

                }
            });
            dialog.show();

            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
        }
        catch (Exception ex)
        {
            Log.d("tag","DialogException: "+ex.toString());
            Toast.makeText(getApplicationContext(),"Failed To Upload"+ex.toString(),Toast.LENGTH_LONG).show();
        }




    }
    private void addListingItem(Uri contentUri) {
        try {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            final ProgressDialog pd= new ProgressDialog(this);
            pd.setTitle("Adding Item...");
            pd.show();
            pd.setCanceledOnTouchOutside(false);
            pd.setCancelable(false);
            final String   randomKey=UUID.randomUUID().toString();
            final StorageReference imageRef=FirebaseStorage.getInstance().getReference().child("ListingPic/"+randomKey);
            imageRef.putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            firebaseImageUri=uri;
                            pd.dismiss();
                            addListing();
                            openSuccessDialog();
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Failed To Upload",Toast.LENGTH_LONG).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progressPercent=(100.00*snapshot.getBytesTransferred()/snapshot.getTotalByteCount());
                    pd.setMessage("Percentage: "+(int)progressPercent+"%");
                }
            });;
        }
        catch (Exception ex)
        {
            Log.d("tag","Exception: "+ex.toString());
        }

    }
    private void hideSoftKeyboard()
    {
        try {
            View view=this.getCurrentFocus();
            if(view!=null)
            {
                InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(),0);

            }
        }
        catch (Exception ex)
        {

        }

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case CAMERA_PERM_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                }else {
                    Toast.makeText(this,"Permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
            case GALLERY_PERM_CODE:{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED) {
                    openGalary();
                }else {
                    Toast.makeText(this,"Permission denied...",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void openGalary() {
        if(Build.VERSION.SDK_INT<19) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Choose from gallery"), GALLERY_REQUEST_CODE);
        }
        else
        {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Choose from gallery"), GALLERY_REQUEST_CODE);
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.grin.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode==GALLERY_REQUEST_CODE && resultCode==RESULT_OK && data != null){
            contentUri =data.getData();
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timeStamp + "_";
            imageDisplay.setImageURI(contentUri);

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAMERA_REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    File f=new File(currentPhotoPath);
                    imageDisplay.setImageURI(Uri.fromFile(f));
                    Log.d("tag","Absolute Url of Image is "+Uri.fromFile(f));

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    this.sendBroadcast(mediaScanIntent);

                }
            }
        }

    }




}