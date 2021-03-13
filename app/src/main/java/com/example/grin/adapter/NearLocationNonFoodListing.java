package com.example.grin.adapter;

import android.content.Intent;
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
import com.example.grin.ItemRequest;
import com.example.grin.R;
import com.example.grin.models.ModalClassListing;
import com.example.grin.my_item;
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

public class NearLocationNonFoodListing extends FirebaseRecyclerAdapter<ModalClassListing,NearLocationNonFoodListing.MyViewHolder> {
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    Double longitude=null;Double latitude=null;
    DatabaseReference ref ;
    String userName ;
    String userImage;
    public NearLocationNonFoodListing(@NonNull FirebaseRecyclerOptions<ModalClassListing> options) {

        super(options);
    }
    @Override
    protected void onBindViewHolder(final NearLocationNonFoodListing.MyViewHolder holder, final int position, final ModalClassListing model) {
        try
        {
            getUserLocaton();
            getSettingData(holder,model);


        }
        catch (Exception ex)
        {
            Log.d(TAG,""+ex.toString());
        }
    }
    public void getSettingData(final NearLocationNonFoodListing.MyViewHolder holder, final ModalClassListing model) {
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
                        String showAll = snapshot.child("showAll").getValue(String.class);
                        Double distance= snapshot.child("distance").getValue(Double.class);
                        setData(wantListing,showAll,sortBy,distance,holder,model);
                    } else {
                        setData("false","false","Closest",0.5,holder,model);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        else
        {
            setData("false","false","Closest",0.5,holder,model);
        }

    }
    public void setData(String wantList, final String showAll, String sort, final Double dis, final NearLocationNonFoodListing.MyViewHolder holder, final ModalClassListing model)
    {
        if (model.getWantedList().equals(wantList) && model.getStatus().equals("Listed")) {

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
                        if(showAll.equals("false")){

                            if(kilometer<=dis) {
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
                        }
                        else {
                            holder.itemView.setVisibility(VISIBLE);
                            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
                            params.height=ViewGroup.LayoutParams.WRAP_CONTENT;
                            params.width= ViewGroup.LayoutParams.MATCH_PARENT;
                            holder.itemView.requestLayout();
                            onDataRetrieved(userName, userImage, holder, model, distance);
                        }

                    } else {
                        Log.d(TAG, "User Not Found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } else {
            holder.itemView.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            params.height=0;
            params.width= 0;
            holder.itemView.requestLayout();
        }


    }
    @Override
    public NearLocationNonFoodListing.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_card_view, parent, false);
        return new NearLocationNonFoodListing.MyViewHolder(view);
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
    public void onDataRetrieved(final String userName, final String userImage, NearLocationNonFoodListing.MyViewHolder holder, final ModalClassListing model, float distance) {
        try {
            this.userImage = userImage;
            this.userName = userName;


            //  holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            Glide.with(holder.itemImage.getContext()).load(model.getItemUri()).into(holder.itemImage);
            Glide.with(holder.userImage.getContext()).load(userImage).into(holder.userImage);
            holder.Distance.setText(String.format("%.2f km", distance / 1000));
            holder.name.setText(model.getItemName());
            holder.User.setText(userName);
            holder.Views.setText(Integer.toString(model.getNoViews()));
            holder.itemImage.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    if(model.getUserId()!= fAuth.getCurrentUser().getUid()) {
                        Intent intent = new Intent(v.getContext(), ItemRequest.class);
                        intent.putExtra("key", model.getKeyId()); //you can name the keys whatever you like
                        intent.putExtra("itemUri", model.getItemUri()); //you can name the keys whatever you like
                        intent.putExtra("itemName", model.getItemName()); //you can name the keys whatever you like
                        intent.putExtra("itemDescribtion", model.getDescribtion()); //you can name the keys whatever you like
                        intent.putExtra("latitude", model.getLatitude().toString()); //you can name the keys whatever you like
                        intent.putExtra("longitude", model.getLongitude().toString()); //you can name the keys whatever you like
                        intent.putExtra("listingDate", model.getListingDate()); //you can name the keys whatever you like
                        intent.putExtra("userId", model.getUserId()); //you can name the keys whatever you like
                        intent.putExtra("noLikes", Integer.toString(model.getNoLike())); //you can name the keys whatever you like
                        intent.putExtra("pickupTime", model.getPickupTime()); //you can name the keys whatever you like
                        intent.putExtra("userName", userName); //you can name the keys whatever you like
                        intent.putExtra("userImage", userImage); //you can name the keys whatever you like
                        v.getContext().startActivity(intent);
                    }
                    else {
                        Intent intent = new Intent(v.getContext(), my_item.class);
                        intent.putExtra("key", model.getKeyId()); //you can name the keys whatever you like
                        intent.putExtra("itemUri", model.getItemUri()); //you can name the keys whatever you like
                        intent.putExtra("itemName", model.getItemName()); //you can name the keys whatever you like
                        intent.putExtra("itemDescribtion", model.getDescribtion()); //you can name the keys whatever you like
                        intent.putExtra("latitude", model.getLatitude().toString()); //you can name the keys whatever you like
                        intent.putExtra("longitude", model.getLongitude().toString()); //you can name the keys whatever you like
                        intent.putExtra("listingDate", model.getListingDate()); //you can name the keys whatever you like
                        intent.putExtra("userId", model.getUserId()); //you can name the keys whatever you like
                        intent.putExtra("noLikes", Integer.toString(model.getNoLike())); //you can name the keys whatever you like
                        intent.putExtra("pickupTime", model.getPickupTime()); //you can name the keys whatever you like
                        intent.putExtra("userName", userName); //you can name the keys whatever you like
                        intent.putExtra("userImage", userImage); //you can name the keys whatever you like
                        v.getContext().startActivity(intent);
                    }
                }
            });
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
