package com.example.grin;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grin.adapter.FoodListingAdapter;
import com.example.grin.models.ModalClassFoodListing;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FoodListingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FoodListingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View v;
    RecyclerView recyclerView;
    List<ModalClassFoodListing> modelList;
     FoodListingAdapter foodListingAdapter;

    FirebaseDatabase database;
    DatabaseReference ref;

    FirebaseDatabase rootnode;
    DatabaseReference reference;
    Double userLatitude = -33.8523341, userLongitude = 151.2106085;
    FirebaseAuth fAuth;
    public FoodListingFragment() {
        // Required empty public constructor
    }
    // TODO: Rename and change types and number of parameters
    public static FoodListingFragment newInstance(String param1, String param2) {
        FoodListingFragment fragment = new FoodListingFragment();
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




            modelList=new ArrayList<>();
            String adminImage="https://firebasestorage.googleapis.com/v0/b/grin-ea2c2.appspot.com/o/Users%2Femp-1.jpg?alt=media&token=340f682b-9ff1-4d11-8f6f-cead614ae9c5";
            String image="https://firebasestorage.googleapis.com/v0/b/grin-ea2c2.appspot.com/o/ListingPic%2Fdownload.jpg?alt=media&token=41452134-b72b-42c4-9a1c-eae5c1f4807e";
            modelList.add(new ModalClassFoodListing(image,adminImage,"Table","Fatima Ali","22.5km","180"));
            modelList.add(new ModalClassFoodListing(image,adminImage,"Square Table","Farahnaz","22.5km","180"));
            modelList.add(new ModalClassFoodListing(image,adminImage,"Tea","Samreen Zahra","22.5km","180"));
            modelList.add(new ModalClassFoodListing(image,adminImage,"Water Bottle","Razia","22.5km","180"));
      /*  try {
            database = FirebaseDatabase.getInstance();
            ref = database.getReference("listing");
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        if(snapshot.exists())
                        {
                            ModalClassFoodListing obj=new ModalClassFoodListing();

                            Double itmLatitude, itmLongitude ;
                            itmLongitude = snapshot.child("longitude").getValue(Double.class);
                            itmLatitude = snapshot.child("latitude").getValue(Double.class);
                            String itmName = snapshot.child("itemName").getValue(String.class);

                            Integer views=snapshot.child("noViews").getValue(Integer.class);
                            String userId=snapshot.child("userId").getValue(String.class);
                            String itmUri = snapshot.child("itemUri").getValue(String.class);
                            Double distance=convertToDistance(userLongitude,userLatitude,itmLongitude,itmLatitude);

                            obj.setDistance(distance.toString());
                            obj.setItemUri(itmUri);
                            obj.setTitle(itmName);
                            obj.setUserName("Hamida");
                            obj.setViews(views.toString());
                            obj.setUserImage(R.drawable.bottle);

                            final String[] userName = new String[2];
                            final FirebaseDatabase database = FirebaseDatabase.getInstance();
                           DatabaseReference refUser = database.getReference("users").child(userId);
                            refUser.addListenerForSingleValueEvent(new ValueEventListener() {

                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot1) {

                                    if(dataSnapshot1.exists())
                                    {
                                        userName[0]=dataSnapshot1.child("firstName").getValue(String.class);
                                        userName[1]=dataSnapshot1.child("lastName").getValue(String.class);

                                    }
                                    else
                                    {
                                        Log.d(TAG,"User Not Found");
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("error", error.getMessage());
                                }
                            });
//                            ModalClassFoodListing obj=new ModalClassFoodListing(itmUri,R.drawable.admin_pic,itmName,"Hamida","10km",views.toString());
                           // modelList.add(new ModalClassFoodListing(R.drawable.bottle,R.drawable.admin_pic,itmName,"Hamida",String.format(Locale.US,"%2f k",distance),views.toString()));
                            modelList.add(obj);

                            Log.d(TAG,"Longitude:"+itmLongitude);
                            Log.d(TAG,"Latitude:"+itmLatitude);
                            Log.d(TAG,"Longitude:"+itmLongitude);
                            Log.d(TAG,"Latitude:"+itmLatitude);
                            Log.d(TAG,"Views:"+views);
                            Log.d(TAG,"UserId:"+userId);
                            Log.d(TAG,"Distance:"+distance);


                        }
                        else
                        {
                            Log.d(TAG,"Item Not Found.");
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("error", error.getMessage());
                }
            });
            Log.d(TAG,"modelList size: "+modelList.size());

        }
        catch (Exception e)
        {
            Log.d(TAG,"Date Retrieving Exception: "+e.toString());
        }*/


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment

        v=inflater.inflate(R.layout.fragment_food_listing, container, false);
        recyclerView=v.findViewById(R.id.food_recycler_View);
        foodListingAdapter=new FoodListingAdapter(modelList,getContext());
        recyclerView.setAdapter(foodListingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
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
                            userLongitude = snapshot.child("longitude").getValue(Double.class);
                            userLatitude = snapshot.child("latitude").getValue(Double.class);
                            Log.d(TAG,""+userLongitude+" "+userLatitude);

                        } else {
                            Log.d(TAG,"getUserLocation: Location not set");
                        }
                    } else {
                        Log.d(TAG,"getUserLocation: Location Not Found");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {

            Log.d(TAG,"getUserLocation: User Not Logged In");
        }

    }
    private Double convertToDistance(Double userLongitude, Double userLatitude, Double itmLongitude, Double itmLatitude) {
        try {
            double longDiff=userLongitude-itmLongitude;
            double distance= Math.sin(deg2rad(userLatitude))
                    *Math.sin(deg2rad(itmLatitude))
                    +Math.cos(deg2rad(userLatitude))
                    *Math.cos(deg2rad(itmLatitude))
                    *Math.cos(deg2rad(longDiff));
            distance= Math.acos(distance);

            distance=rad2deg(distance);

            distance=distance*60*1.1515;

            distance=distance*1.609344;
            return distance;
        }
        catch (Exception ex)
        {
            return null;

        }
    }

    private double rad2deg(double distance) {
        return (distance*180.0/Math.PI);
    }

    private double deg2rad(Double latitude) {
        return (latitude*Math.PI/180.0);
    }


}