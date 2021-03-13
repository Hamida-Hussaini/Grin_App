package com.example.grin.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.grin.AddListtingItem;
import com.example.grin.Classes.Listing;
import com.example.grin.Classes.Settings;
import com.example.grin.R;
import com.example.grin.Signup;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class foodBottomSheetDailogFragment extends BottomSheetDialogFragment {
    Button btnApply;
    ImageView btnClose;
    RadioButton rd_0_5,rd_1,rd_2,rd_5,rd_10,rd_25,rd_Newest,rd_Closest; // initiate a radio button
    Switch wantedListing,showAllList;
    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference,checkReference;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.food_setting,container,false);
        btnApply=v.findViewById(R.id.btnApply);
        rd_0_5 = (RadioButton)v.findViewById(R.id.rd_0_5); // initiate a radio button
        rd_1 = (RadioButton)v.findViewById(R.id.rd_1); // initiate a radio button
        rd_2 = (RadioButton)v.findViewById(R.id.rd_2); // initiate a radio button
        rd_5 = (RadioButton) v.findViewById(R.id.rd_5); // initiate a radio button
        rd_10 = (RadioButton) v.findViewById(R.id.rd_10); // initiate a radio button
        rd_25 = (RadioButton) v.findViewById(R.id.rd_25); // initiate a radio button
        rd_Newest = (RadioButton) v.findViewById(R.id.rd_Newest); // initiate a radio button
        rd_Closest = (RadioButton) v.findViewById(R.id.rd_Closest); // initiate a radio button
        showAllList=v.findViewById(R.id.showAll);
        wantedListing=v.findViewById(R.id.checkType);
        getSettingData();
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                insertSettings();
                dismiss();
            }
        });
        ImageView btnClose=v.findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                dismiss();
            }
        });
        return v;
    }

    public void getSettingData() {
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null) {
            final String userId = fAuth.getCurrentUser().getUid();
            rootnode = FirebaseDatabase.getInstance();
            reference = rootnode.getReference("FoodSetting").child(userId);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String sortBy = snapshot.child("sortBy").getValue(String.class);
                        String wantListing = snapshot.child("wantedList").getValue(String.class);
                        String showAll = snapshot.child("showAll").getValue(String.class);
                        Double distance= snapshot.child("distance").getValue(Double.class);
                        setValue(sortBy,wantListing,distance,showAll);


                    } else {
                        Log.d(getTag(),"getFoodSetting2: Setting Not Found.");
                        setValue("Newest","false",0.5,"false");

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            Log.d(getTag(),"getFoodSetting3: Setting Not Found.");
            setValue("Newest","false",0.5,"false");
        }

    }
    public void setValue(String sort,String wanted,Double distance,String showAll) {
        if(wanted.equals("true"))
        {
            wantedListing.setChecked(true);
        }
        else if(wanted.equals("false"))
        {
            wantedListing.setChecked(false);
        }
        if(showAll.equals("true"))
        {
            showAllList.setChecked(true);
        }
        else if(showAll.equals("false"))
        {
            showAllList.setChecked(false);
        }

        if(sort.equals("Newest"))
        {
            rd_Newest.setChecked(true);
        }
        else if(sort.equals("Closest"))
        {
            rd_Closest.setChecked(true);
            Toast.makeText(getContext(), "sortBy: "+sort, Toast.LENGTH_SHORT).show();
        }

        if (distance==1) {
            rd_1.setChecked(true);
        } else if (distance==0.5) {
            rd_0_5.setChecked(true);
        }
        else if (distance==2) {
            rd_2.setChecked(true);
        }
        else if (distance==5) {
            rd_5.setChecked(true);
        }
        else if (distance==10) {
            rd_10.setChecked(true);
        }
        else if (distance==25) {
            rd_25.setChecked(true);
        }

    }
    public void insertSettings() {
        try {

            final String wantListing ;
            final String showAll ;
            if (wantedListing.isChecked()) {
                wantListing = "true";
            } else {
                wantListing = "false";
            }
            if (showAllList.isChecked()) {
                showAll = "true";
            } else {
                showAll = "false";
            }
            final String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            double distance=0;

            if (rd_1.isChecked()) {
                distance=1;
            } else if (rd_0_5.isChecked()) {
                distance=0.5;
            }
            else if (rd_2.isChecked()) {
                distance=2;
            }
            else if (rd_5.isChecked()) {
                distance=5;
            }
            else if (rd_10.isChecked()) {
                distance=10;
            }
            else if (rd_25.isChecked()) {
                distance=25;
            }
            String sortBy = null;
            if (rd_Newest.isChecked()) {
                sortBy="Newest";
            } else if (rd_Closest.isChecked()) {
                sortBy="Closest";
            }

            rootnode = FirebaseDatabase.getInstance();
            checkReference= rootnode.getReference("FoodSetting").child(userId);
            reference = rootnode.getReference("FoodSetting");

            final Settings obj = new Settings(sortBy.trim(),wantListing.trim(),distance,showAll.trim());

            final String finalSortBy = sortBy;
            final double finalDistance = distance;
            checkReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                Map<String, Object> hopperUpdates = new HashMap<>();
                                hopperUpdates.put("sortBy", finalSortBy.trim());
                                hopperUpdates.put("wantedList", wantListing.trim());
                                hopperUpdates.put("distance", finalDistance);
                                hopperUpdates.put("showAll", showAll.trim());
                                checkReference.updateChildren(hopperUpdates);
                                Toast.makeText(getContext(), "Food filter is updated successfully.", Toast.LENGTH_SHORT).show();

                            } else {
                                reference.child(userId).setValue(obj).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(getContext(), "Food filter set successfully.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        } catch (Exception ex) {
            Log.d("error","Exception"+ex.toString());
        }

    }
}
