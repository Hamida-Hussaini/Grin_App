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
import com.example.grin.adapter.FurtherFoodListingAdapter;
import com.example.grin.adapter.NearLocationListingAdapter;
import com.example.grin.models.ModalClassListing;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

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
    RecyclerView recyclerView,recyclerView1;
    TextView lblWantedList;
    NearLocationListingAdapter nearLocationListingAdapter;
    FurtherFoodListingAdapter furtherFoodListingAdapter;
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
            nearLocationListingAdapter.startListening();
          //  furtherFoodListingAdapter.startListening();
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
            nearLocationListingAdapter.stopListening();
           // furtherFoodListingAdapter.stopListening();
        }
        catch (Exception ex)
        {

        }
    }
    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            v=inflater.inflate(R.layout.fragment_food_listing, container, false);
            ImageView btnSetting=v.findViewById(R.id.btnFilter);
            lblWantedList=v.findViewById(R.id.lblListingHeading);
            btnSetting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    new foodBottomSheetDailogFragment().show(getChildFragmentManager(),"Dailog");

                    fAuth = FirebaseAuth.getInstance();
                    if(fAuth.getCurrentUser()!=null) {
                        final String userId = fAuth.getCurrentUser().getUid();
                        rootnode = FirebaseDatabase.getInstance();
                        reference = rootnode.getReference("FoodSetting").child(userId);
                        reference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    String wantListing = snapshot.child("wantedList").getValue(String.class);
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
            recyclerView=v.findViewById(R.id.food_recycler_View);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            ref=FirebaseDatabase.getInstance().getReference().child("listing");
            Query query = ref.orderByChild("itamType").equalTo("Food");

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
            nearLocationListingAdapter = new NearLocationListingAdapter(options);
            recyclerView.setAdapter(nearLocationListingAdapter);
//            TextView txtMsg=v.findViewById(R.id.txtNearListing);
//            ViewGroup.LayoutParams params = txtMsg.getLayoutParams();
//            if(nearLocationListingAdapter==null)
//            {
//                params.width = ViewGroup.LayoutParams.MATCH_PARENT;
//                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//                txtMsg.requestLayout();
//                txtMsg.setVisibility(View.VISIBLE);
//            }
//            else
//            {
//                params.width = 0;
//                params.height = 0;
//                txtMsg.requestLayout();
//                txtMsg.setVisibility(View.INVISIBLE);
//            }
////            recyclerView1=v.findViewById(R.id.further_food_recycler_View);
////            recyclerView1.setLayoutManager(new LinearLayoutManager(getContext()));
////            furtherFoodListingAdapter=new FurtherFoodListingAdapter(options);
////            recyclerView1.setAdapter(furtherFoodListingAdapter);
        }
        catch (Exception ex)
        {
            Log.d(TAG,""+ex.toString());
        }
        return v;
    }

}