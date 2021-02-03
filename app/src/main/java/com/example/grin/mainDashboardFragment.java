package com.example.grin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link mainDashboardFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class mainDashboardFragment extends Fragment {
    private static final String TAG = mainDashboardFragment.class.getSimpleName();
    SpaceNavigationView spaceNavigationView;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public mainDashboardFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment mainDashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static mainDashboardFragment newInstance(String param1, String param2) {
        mainDashboardFragment fragment = new mainDashboardFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @SuppressLint("ResourceType")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        view_listing_fragment viewfragment=new view_listing_fragment();
        FragmentTransaction transition=getChildFragmentManager().beginTransaction();
        transition.replace(R.layout.fragment_main_dashboard,viewfragment);
    }

    @SuppressLint("ResourceType")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main_dashboard, container, false);
        view_listing_fragment listfragmet=new view_listing_fragment();
        FragmentTransaction ft=getChildFragmentManager().beginTransaction();
        ft.replace(R.id.childFragmentContainer,listfragmet);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        try {
            super.onViewCreated(view, savedInstanceState);
            final AppCompatActivity activity=(AppCompatActivity)view.getContext();

            // find views by id
            spaceNavigationView=(SpaceNavigationView)view.findViewById(R.id.dashboardSpace);
            spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
            spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_listing));
            spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_settings));
            spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_guide));
            spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_messages));
            spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
                @Override
                public void onCentreButtonClick() {

                    spaceNavigationView.setCentreButtonSelectable(true);
                    Intent intent=new Intent(getContext(),AddListtingItem.class);
                    startActivity(intent);
                }

                @Override
                public void onItemClick(int itemIndex, String itemName) {
                    if(itemIndex==0)
                    {
                        view_listing_fragment viewlistfragmet=new view_listing_fragment();
                        FragmentTransaction ft=getChildFragmentManager().beginTransaction();
                        ft.replace(R.id.childFragmentContainer,viewlistfragmet);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                    else if(itemIndex==1)
                    {
                        addListingFragment listfragmet=new addListingFragment();
                        FragmentTransaction ft=getChildFragmentManager().beginTransaction();
                        ft.replace(R.id.childFragmentContainer,listfragmet);
                        ft.addToBackStack(null);
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                        ft.commit();
                    }
                    Toast.makeText(getContext(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onItemReselected(int itemIndex, String itemName) {
                    //  Toast.makeText(getContext(), itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
                    //
                }
            });

        }
        catch (Exception ex)
        {
            Log.d(TAG,"Error: "+ex.toString());
        }

        // set adapter on viewpager
        // viewPager.setAdapter(adapter);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }
}