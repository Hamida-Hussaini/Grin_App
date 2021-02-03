package com.example.grin.adapter;

import android.content.Context;
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
import com.example.grin.models.ModalClassFoodListing;
import com.example.grin.models.ModalClassNonFoodListing;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.List;

import static android.content.ContentValues.TAG;
import static android.view.View.VISIBLE;

public class FoodListingAdapter extends FirebaseRecyclerAdapter<ModalClassFoodListing,FoodListingAdapter.MyViewHolder> {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Double longitude=null;Double latitude=null;
    DatabaseReference ref ;
    String userName ;
    String userImage;
    public FoodListingAdapter(@NonNull FirebaseRecyclerOptions<ModalClassFoodListing> options) {

        super(options);
    }
    @Override
    protected void onBindViewHolder(final MyViewHolder holder, final int position, final ModalClassFoodListing model) {

        //holder.itemImage.setImageResource(mList.get(position).getItemUri());
        //  holder.itemImage.setImageURI(mList.get(position).getItemUri());
        String userId = model.getUserId();
        ref = database.getReference("users").child(userId);
        getUserLocaton();
        float[] result = new float[1];
        final float distance;
        int kilometer;
        String itemType=model.getItamType();
        if (model.getWantedList().equals("false") && model.getStatus().equals("Listed")) {
            if (longitude != null && latitude != null) {
                Double itemLatitude = model.getLatitude();
                Double itemLongitude = model.getLongitude();
                Log.d(TAG, "User Location: " + longitude + " " + latitude);
                Log.d(TAG, "Item Location: " + itemLongitude + " " + itemLatitude);

                Location.distanceBetween(latitude, longitude, itemLatitude, itemLongitude, result);
                distance = result[0];
                Log.d(TAG, "Distance: " + distance);
                kilometer = (int) distance / 1000;
            } else {
                distance = 0;
            }
            if((distance/1000)<10) {

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
                            onDataRetrieved(userName, userImage, holder, model,distance);
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
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
        } else {
            holder.itemView.setVisibility(View.GONE);
            holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
        }



    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card_view, parent, false);
        return new MyViewHolder(view);
    }
    public void onDataChanged() {
        // Called each time there is a new data snapshot. You may want to use this method
        // to hide a loading spinner or check for the "no documents" state and update your UI.
        // ...
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
    public void onDataRetrieved(String userName,String userImage,MyViewHolder holder,ModalClassFoodListing model,float distance) {
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
    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;
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
    public void saveLocation(Double longitude,Double latiutude)
    {
        this.latitude=latiutude;
        this.longitude=longitude;
    }
}