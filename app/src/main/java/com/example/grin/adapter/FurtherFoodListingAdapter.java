package com.example.grin.adapter;

import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grin.R;
import com.example.grin.models.ModalClassListing;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;

public class FurtherFoodListingAdapter extends FirebaseRecyclerAdapter<ModalClassListing,FurtherFoodListingAdapter.MyViewHolder> {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Double longitude=null;Double latitude=null;
    DatabaseReference ref ;
    String userName ;
    String userImage;
    public FurtherFoodListingAdapter(@NonNull FirebaseRecyclerOptions<ModalClassListing> options) {

        super(options);
    }
    @Override
    protected void onBindViewHolder(final FurtherFoodListingAdapter.MyViewHolder holder, final int position, final ModalClassListing model) {
        try {
            getUserLocaton();
            getSettingData(holder,model);

        }
        catch (Exception ex)
        {

            Log.d(TAG,""+ex.toString());
        }




    }
    public void getSettingData(final FurtherFoodListingAdapter.MyViewHolder holder, final ModalClassListing model) {
        fAuth = FirebaseAuth.getInstance();
        if(fAuth.getCurrentUser()!=null) {
            final String userId = fAuth.getCurrentUser().getUid();
            rootnode = FirebaseDatabase.getInstance();
            reference = rootnode.getReference("FoodSetting").child(userId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String sortBy = snapshot.child("sortBy").getValue(String.class);
                        String wantListing = snapshot.child("wantedList").getValue(String.class);
                        Double distance= snapshot.child("distance").getValue(Double.class);
                        setData(wantListing,sortBy,distance,holder,model);
                    } else {
                        setData("false","Closest",0.5,holder,model);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            setData("false","Closest",0.5,holder,model);
        }

    }
    public void setData(String wantList, String sort, final Double dis, final FurtherFoodListingAdapter.MyViewHolder holder, final ModalClassListing model)
    {

        if (model.getWantedList().equals(wantList) && model.getStatus().equals("Listed")) {
            float[] result = new float[1];
            final float distance;
            float kilometer= (float) 0.0;
            if (longitude != null && latitude != null) {
                Double itemLatitude = model.getLatitude();
                Double itemLongitude = model.getLongitude();
                Location.distanceBetween(latitude, longitude, itemLatitude, itemLongitude, result);
                distance = result[0];
                kilometer = (float) distance / 1000;
            } else {
                distance = 0;
            }
            if(kilometer>=dis) {
                String userId = model.getUserId();
                ref = database.getReference("users").child(userId);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            String userName = snapshot.child("firstName").getValue().toString() + " " + snapshot.child("lastName").getValue().toString();
                            String userImage;
                            if (snapshot.child("userImage").exists()) {
                                userImage = snapshot.child("userImage").getValue().toString();
                            } else {
                                userImage = "https://firebasestorage.googleapis.com/v0/b/grin-ea2c2.appspot.com/o/Users%2Femp-1.jpg?alt=media&token=340f682b-9ff1-4d11-8f6f-cead614ae9c5";
                            }
                            float[] result = new float[1];
                            final float distance;
                            float kilometer= (float) 0.0;
                            if (longitude != null && latitude != null) {
                                Double itemLatitude = model.getLatitude();
                                Double itemLongitude = model.getLongitude();
                                Location.distanceBetween(latitude, longitude, itemLatitude, itemLongitude, result);
                                distance = result[0];
                                kilometer = (float) distance / 1000;
                            } else {
                                distance = 0;
                            }
                            if(kilometer>=dis) {
                                holder.itemView.setVisibility(VISIBLE);
                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                params.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                                params.width= ViewGroup.LayoutParams.MATCH_PARENT;
                                holder.itemView.requestLayout();
                                onDataRetrieved(userName, userImage, holder, model, distance);
                            }
                            else {
                                holder.itemView.setVisibility(View.INVISIBLE);
                                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                                params.height=0;
                                params.width= 0;
                                holder.itemView.requestLayout();
                            }
                        } else {
                            Log.d(TAG, "User Not Found");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            else
            {
                holder.itemView.setVisibility(View.INVISIBLE);
                ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                params.height=0;
                params.width= 0;
                holder.itemView.requestLayout();
            }
        } else {
            holder.itemView.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height=0;
            params.width= 0;
            holder.itemView.requestLayout();
        }


    }

    @Override
    public FurtherFoodListingAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card_view, parent, false);
        return new FurtherFoodListingAdapter.MyViewHolder(view);
    }
    public void onDataChanged(final FurtherFoodListingAdapter.MyViewHolder holder, final int position, final ModalClassListing model) {

    }

    @Override
    public void onError(DatabaseError e) {
        Log.d(TAG,"Data fetching Error: "+e.getMessage());
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView itemImage;
        ImageView userImage;
        TextView name;
        TextView User;
        TextView Distance;
        TextView Views;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemImage=itemView.findViewById(R.id.item_image);
            userImage=itemView.findViewById(R.id.item_profile_picture);
            name=itemView.findViewById(R.id.item_name);
            User=itemView.findViewById(R.id.item_user);
            Distance=itemView.findViewById(R.id.item_location);
            Views=itemView.findViewById(R.id.item_views);
        }
    }
    public void onDataRetrieved(String userName, String userImage, FurtherFoodListingAdapter.MyViewHolder holder, ModalClassListing model, float distance) {
        try {
            this.userImage = userImage;
            this.userName = userName;

            holder.itemView.setVisibility(VISIBLE);
            //  holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Glide.with(holder.itemImage.getContext()).load(model.getItemUri()).into(holder.itemImage);
            Glide.with(holder.userImage.getContext()).load(userImage).into(holder.userImage);
            holder.Distance.setText(String.format("%.2f km", distance / 1000));
            holder.name.setText(model.getItemName());
            holder.User.setText(userName);
            holder.Views.setText(Integer.toString(model.getNoViews()));
        }
        catch (Exception ex)
        {
            Log.d(TAG,"Exception: "+ex.toString());
        }

    }
    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
    public void getUserLocaton() {

        try {
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
                                Double longitude = snapshot.child("longitude").getValue(Double.class);
                                Double latitude = snapshot.child("latitude").getValue(Double.class);
                                saveLocation(longitude,latitude);
                            } else {
                                Log.d(TAG,"getUserLocation: Location Not Found.");

                            }
                        } else {
                            Log.d(TAG,"getUserLocation: User Not Found");

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
        catch (Exception ex)
        {
            Log.d(TAG,""+ex.toString());
        }

    }
    public void saveLocation(Double longitude,Double latiutude)
    {
        this.latitude=latiutude;
        this.longitude=longitude;
    }
}
