package com.example.grin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.example.grin.adapter.ViewPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class MainContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    SpaceNavigationView spaceNavigationView;
    private AppBarConfiguration mAppBarConfiguration;
    private static final String TAG = MainContainer.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main_container);
            Toolbar toolbar = findViewById(R.id.main_toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
            NavigationView navigationView = findViewById(R.id.main_nav_view);

            navigationView.bringToFront();
            ActionBarDrawerToggle toogle=new ActionBarDrawerToggle(MainContainer.this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
            drawerLayout.setDrawerListener(toogle);
            toogle.syncState();
            navigationView.setNavigationItemSelectedListener(this);


            navigationView.setCheckedItem(R.id.nav_home);
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
//            mAppBarConfiguration = new AppBarConfiguration.Builder(
//                    R.id.nav_home, R.id.nav_my_location, R.id.nav_add_listing)
//                    .setDrawerLayout(drawer)
//                    .build();
//            NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//            NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//            NavigationUI.setupWithNavController(navigationView, navController);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        catch (Exception ex)
        {
            Log.d(TAG,"Error: "+ex.toString());
        }

    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        View parentFragment=findViewById(R.id.mainFrameLayout);
        switch (menuItem.getItemId()){
            case R.id.nav_home:
//                if(parentFragment!=null)
//                {
//                    mainDashboardFragment mainFragment=new mainDashboardFragment();
//                    FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
//                    ft.replace(R.id.mainFrameLayout,mainFragment);
//                    ft.addToBackStack(null);
//                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//                    ft.commit();
//                }
                break;
          case R.id.nav_my_location:
              if(parentFragment!=null)
              {
                  MyLocationFragment myLocationFragment=new MyLocationFragment();
                  FragmentTransaction ftt=getSupportFragmentManager().beginTransaction();
                  ftt.replace(R.id.mainFrameLayout,myLocationFragment);
                  ftt.addToBackStack(null);
                  ftt.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                  ftt.commit();
              }
              break;
            case R.id.nav_profile:

                break;
            case R.id.nav_logout:
                FirebaseAuth.getInstance().signOut();
                Intent loginIntent=new Intent(getApplicationContext(),LoginUser.class);
                startActivity(loginIntent);
                finish();


        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
//    @Override
//    public boolean onSupportNavigateUp() {
//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
//                || super.onSupportNavigateUp();
//    }



}