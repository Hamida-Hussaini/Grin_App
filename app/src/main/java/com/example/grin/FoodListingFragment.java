package com.example.grin;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.grin.adapter.FoodListingAdapter;
import com.example.grin.models.ModalClassFoodListing;

import java.util.ArrayList;
import java.util.List;

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
    List<ModalClassFoodListing> modelList;
     FoodListingAdapter foodListingAdapter;
    public FoodListingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FoodListingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FoodListingFragment newInstance(String param1, String param2) {
        FoodListingFragment fragment = new FoodListingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        modelList=new ArrayList<>();
        modelList.add(new ModalClassFoodListing(R.drawable.vase,R.drawable.admin_pic,"Vase","Hamida Hussaini","22.5Km","180"));
        modelList.add(new ModalClassFoodListing(R.drawable.table,R.drawable.admin_pic,"Table","Fatima Ali","22.5km","180"));
        modelList.add(new ModalClassFoodListing(R.drawable.table_young,R.drawable.admin_pic,"Square Table","Farahnaz","22.5km","180"));
        modelList.add(new ModalClassFoodListing(R.drawable.tea,R.drawable.admin_pic,"Tea","Samreen Zahra","22.5km","180"));
        modelList.add(new ModalClassFoodListing(R.drawable.bottle,R.drawable.admin_pic,"Water Bottle","Razia","22.5km","180"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v=inflater.inflate(R.layout.fragment_food_listing, container, false);
        recyclerView=v.findViewById(R.id.food_recycler_View);
        foodListingAdapter=new FoodListingAdapter(modelList,getContext());
        recyclerView.setAdapter(foodListingAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return v;
    }

}