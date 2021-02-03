package com.example.grin;

import android.location.Location;
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

import com.example.grin.Classes.common;
import com.example.grin.adapter.FoodListingAdapter;
import com.example.grin.adapter.NonFoodListingAdaptor;
import com.example.grin.models.ModalClassFoodListing;
import com.example.grin.models.ModalClassNonFoodListing;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    FoodListingAdapter foodListingAdapter;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref ;
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
    String userName, userImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }
        catch (Exception ex)
        {
            Log.d("","Loading Exception: "+ex.toString());
        }
    }
    @Override
    public void onStart() {
        try {
            super.onStart();
            foodListingAdapter.startListening();
        }
        catch (Exception ex)
        {
            Log.d(TAG,"Error: "+ex.toString());
        }

    }
    @Override
    public void onStop() {
        try {
            super.onStop();
            foodListingAdapter.stopListening();
        }
        catch (Exception ex)
        {

        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            v=inflater.inflate(R.layout.fragment_food_listing, container, false);
            recyclerView=v.findViewById(R.id.food_recycler_View);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ref=FirebaseDatabase.getInstance().getReference().child("listing");
            Query query = ref.orderByChild("itamType").equalTo("Food");

            //  Query query= reference1.orderByChild("wantedList").equalTo(false);
            FirebaseRecyclerOptions<ModalClassFoodListing> options =
                    new FirebaseRecyclerOptions.Builder<ModalClassFoodListing>()
                            .setQuery(query, new SnapshotParser<ModalClassFoodListing>() {
                                @NonNull
                                @Override
                                public ModalClassFoodListing parseSnapshot(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        return new ModalClassFoodListing(
                                                snapshot.child("itemName").getValue().toString(),
                                                snapshot.child("itamType").getValue().toString(),
                                                snapshot.child("itemUri").getValue().toString(),
                                                snapshot.child("lastDate").getValue().toString(),
                                                snapshot.child("listingDate").getValue().toString(),
                                                snapshot.child("status").getValue().toString(),
                                                snapshot.child("userId").getValue().toString(),
                                                snapshot.child("noViews").getValue(Integer.class),
                                                snapshot.child("latitude").getValue(Double.class),
                                                snapshot.child("longitude").getValue(Double.class),
                                                snapshot.child("wantedList").getValue().toString());
                                    }
                                    else
                                    {
                                        return null;
                                    }
                                }
                            })
                            .build();
            foodListingAdapter = new FoodListingAdapter(options);
            recyclerView.setAdapter(foodListingAdapter);
        }
        catch (Exception ex)
        {

        }
        return v;
    }

}