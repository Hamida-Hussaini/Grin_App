package com.example.grin;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.grin.adapter.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link view_listing_fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class view_listing_fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = view_listing_fragment.class.getSimpleName();
    private TabLayout tabLayout;
    private ViewPager viewPager;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public view_listing_fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment view_listing_fragment.
     */
    // TODO: Rename and change types and number of parameters
    public static view_listing_fragment newInstance(String param1, String param2) {
        view_listing_fragment fragment = new view_listing_fragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_view_listing_fragment, container, false);
        return  view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            // find views by id
            tabLayout=(TabLayout)view.findViewById(R.id.dashboardFragmentTablayout);
            viewPager=(ViewPager)view.findViewById(R.id.dashboardFragmenViewPager);
            tabLayout.setupWithViewPager(viewPager);
            ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
            // add your fragments
            adapter.addFragment(new FoodListingFragment(),"Food");
            adapter.addFragment(new NonFoodFragment(),"Non-food");
            // set adapter on viewpager
            viewPager.setAdapter(adapter);

        }
        catch (Exception ex)
        {
            Log.d(TAG,"Error: "+ex.toString());
        }

        // set adapter on viewpager
        // viewPager.setAdapter(adapter);
    }
}