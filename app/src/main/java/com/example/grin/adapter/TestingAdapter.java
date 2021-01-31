package com.example.grin.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.grin.R;
import com.example.grin.models.ModalClassNonFoodListing;
import com.example.grin.models.TestingModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;

public class TestingAdapter extends FirebaseRecyclerAdapter<TestingModel,TestingAdapter.MyViewHolder> {


    public TestingAdapter(@NonNull FirebaseRecyclerOptions<TestingModel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull TestingModel model) {
        try {
            Glide.with(holder.itemImage.getContext()).load(model.getImage()).into(holder.itemImage);
            Glide.with(holder.userImage.getContext()).load(model.getImage()).into(holder.userImage);
            //holder.itemImage.setImageResource(mList.get(position).getItemUri());
            //  holder.itemImage.setImageURI(mList.get(position).getItemUri());
            holder.Distance.setText("123");
            holder.name.setText(model.getName());
            holder.User.setText(model.getQualification());
            holder.Views.setText("100");
        } catch (Exception ex) {

        }

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_card_view, parent, false);
        return null;
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
            itemImage = itemView.findViewById(R.id.item_image);
            userImage = itemView.findViewById(R.id.item_profile_picture);
            name = itemView.findViewById(R.id.item_name);
            User = itemView.findViewById(R.id.item_user);
            Distance = itemView.findViewById(R.id.item_location);
            Views = itemView.findViewById(R.id.item_views);
        }
    }
}