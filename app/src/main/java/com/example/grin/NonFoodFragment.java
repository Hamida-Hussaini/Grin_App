package com.example.grin;

import android.animation.ValueAnimator;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.accessibility.AccessibilityViewCommand;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.BoringLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.grin.adapter.FoodListingAdapter;
import com.example.grin.adapter.NonFoodListingAdaptor;
import com.example.grin.adapter.TestingAdapter;
import com.example.grin.models.ModalClassFoodListing;
import com.example.grin.models.ModalClassNonFoodListing;
import com.example.grin.models.TestingModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.firebase.ui.database.paging.FirebaseDataSource;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NonFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NonFoodFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    View v;
    RecyclerView recyclerView;
    NonFoodListingAdaptor nonfoodListingAdapter;
    public NonFoodFragment() {
        // Required empty public constructor
    }

    public static NonFoodFragment newInstance(String param1, String param2) {
        NonFoodFragment fragment = new NonFoodFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("listing");

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            v=inflater.inflate(R.layout.fragment_non_food, container, false);
            recyclerView=v.findViewById(R.id.nonfood_recycler_View);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ref=FirebaseDatabase.getInstance().getReference().child("listing");
            Query query = ref.orderByChild("itamType").equalTo("Non-Food");

          //  Query query= reference1.orderByChild("wantedList").equalTo(false);
            FirebaseRecyclerOptions<ModalClassNonFoodListing> options =
                    new FirebaseRecyclerOptions.Builder<ModalClassNonFoodListing>()
                            .setQuery(query, new SnapshotParser<ModalClassNonFoodListing>() {
                                @NonNull
                                @Override
                                public ModalClassNonFoodListing parseSnapshot(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        return new ModalClassNonFoodListing(
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
            nonfoodListingAdapter = new NonFoodListingAdaptor(options);
            recyclerView.setAdapter(nonfoodListingAdapter);
        }
        catch (Exception ex)
        {

        }
        return v;
    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            nonfoodListingAdapter.startListening();
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
            nonfoodListingAdapter.stopListening();
        }
        catch (Exception ex)
        {

        }
    }

}