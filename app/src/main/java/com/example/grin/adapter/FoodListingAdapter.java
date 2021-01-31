package com.example.grin.adapter;

import android.content.Context;
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

import org.w3c.dom.Text;

import java.util.List;

import static android.content.ContentValues.TAG;
public class FoodListingAdapter extends RecyclerView.Adapter<FoodListingAdapter.MyViewHolder> {
//public class FoodListingAdapter extends FirebaseRecylerAdapter<ModalClassFoodListing,FoodListingAdapter.MyViewHolder> {
    List<ModalClassFoodListing> mList;
    Context context;

    public FoodListingAdapter(List<ModalClassFoodListing> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.product_card_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            Glide.with(holder.itemImage).load(mList.get(position).getItemUri()).into(holder.itemImage);
            Glide.with(holder.userImage).load(mList.get(position).getItemUri()).into(holder.userImage);
            //holder.itemImage.setImageResource(mList.get(position).getItemUri());
            //  holder.itemImage.setImageURI(mList.get(position).getItemUri());
            holder.Distance.setText(mList.get(position).getDistance());
            holder.name.setText(mList.get(position).getTitle());
            holder.User.setText(mList.get(position).getUserName());
            holder.Views.setText(mList.get(position).getViews());
        }
        catch (Exception ex)
        {
            Log.d(TAG,"Binding Error: "+ex.toString());
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
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
}
