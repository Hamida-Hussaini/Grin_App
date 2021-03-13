package com.example.grin.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grin.R;
import com.example.grin.adapter.NearLocationListingAdapter;
import com.example.grin.adapter.NearLocationNonFoodListing;
import com.example.grin.models.ModalClassListing;
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

import java.text.SimpleDateFormat;
import java.util.Date;

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
    TextView lblWantedList;

    NearLocationNonFoodListing nearLocationNonFoodListing;
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
    DatabaseReference ref ;

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


    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        try {
            v=inflater.inflate(R.layout.fragment_non_food, container, false);
            ImageView btnSetting=v.findViewById(R.id.btnFilter);
            lblWantedList=v.findViewById(R.id.lblListingHeading);
            btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   
                    new nonFoodBottomDailogFragment().show(getChildFragmentManager(),"Dailog");

                    fAuth = FirebaseAuth.getInstance();
                    if(fAuth.getCurrentUser()!=null) {
                        final String userId = fAuth.getCurrentUser().getUid();
                        rootnode = FirebaseDatabase.getInstance();
                        reference = rootnode.getReference("NonFoodSetting").child(userId);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String sortBy = snapshot.child("sortBy").getValue(String.class);
                                    String wantListing = snapshot.child("wantedList").getValue(String.class);
                                    Double distance= snapshot.child("distance").getValue(Double.class);
                                    if(wantListing=="true") {
                                        lblWantedList.setText("Wanted Listing");
                                    }
                                    else {
                                        lblWantedList.setText("");
                                    }
                                }
                                else {

                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }

                }
            });



            recyclerView=v.findViewById(R.id.nonfood_recycler_View);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ref=FirebaseDatabase.getInstance().getReference().child("listing");
            Query query = ref.orderByChild("itamType").equalTo("Non-Food");

          //  Query query= reference1.orderByChild("wantedList").equalTo(false);
            FirebaseRecyclerOptions<ModalClassListing> options =
                    new FirebaseRecyclerOptions.Builder<ModalClassListing>()
                            .setQuery(query, new SnapshotParser<ModalClassListing>() {
                                @NonNull
                                @Override
                                public ModalClassListing parseSnapshot(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()) {
                                        return new ModalClassListing(
                                                snapshot.getKey(),
                                                snapshot.child("itemName").getValue().toString(),
                                                snapshot.child("itamType").getValue().toString(),
                                                snapshot.child("describtion").getValue().toString(),
                                                snapshot.child("pickupTime").getValue().toString(),
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
            nearLocationNonFoodListing = new NearLocationNonFoodListing(options);
            recyclerView.setAdapter(nearLocationNonFoodListing);
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
            nearLocationNonFoodListing.startListening();
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
            nearLocationNonFoodListing.stopListening();
        }
        catch (Exception ex)
        {

        }
    }

}