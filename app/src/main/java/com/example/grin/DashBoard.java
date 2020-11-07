package com.example.grin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.grin.adapter.ViewPagerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

public class DashBoard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;

    private TabLayout  tabLayout;
    private ViewPager viewPager;
    SpaceNavigationView spaceNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);


        spaceNavigationView=(SpaceNavigationView)findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_listing));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_settings));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_guide));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_messages));

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {

                spaceNavigationView.setCentreButtonSelectable(true);
                finish();
                Intent intent=new Intent(DashBoard.this,AddListtingItem.class);
                startActivity(intent);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {

            }

           @Override
            public void onItemReselected(int itemIndex, String itemName) {
                //Toast.makeText(DashBoard.this, itemIndex + " " + itemName, Toast.LENGTH_SHORT).show();
            }
        });

        tabLayout=(TabLayout)findViewById(R.id.dashboardTablayout);
        viewPager=(ViewPager)findViewById(R.id.dashboardViewPager);


        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.main_toolbar);



        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        navigationView.bringToFront();
        ActionBarDrawerToggle toogle=new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toogle);
        toogle.syncState();
        navigationView.setNavigationItemSelectedListener(this);


        navigationView.setCheckedItem(R.id.nav_home);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new FoodListingFragment(),"Food");
        viewPagerAdapter.addFragment(new NonFoodFragment(),"Non-food");
        viewPager.setAdapter(viewPagerAdapter);

    }
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.top_menu,menu);
        return  true;
    }*/
/*    @Override
    public  boolean onOptionsItemSelected(MenuItem item)
    {
        int id=item.getItemId();
        if(id==R.id.nav_food)
        {
            Toast.makeText(DashBoard.this, "you click food items.", Toast.LENGTH_SHORT).show();
        }
        else if(id==R.id.nav_non_food)
        {
            Toast.makeText(DashBoard.this, "you click  items.", Toast.LENGTH_SHORT).show();

        }
        return  true;

    }*/

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.nav_home:
                break;
            case R.id.nav_location:
                Intent intent=new Intent(DashBoard.this,MyLocation.class);
                startActivity(intent);
                break;
            case R.id.nav_profile:
                Intent profileIntent=new Intent(DashBoard.this,UserProfile.class);
                startActivity(profileIntent);
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
  /*  public void logOut(View view)
    {
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(getApplicationContext(),Login.class);
        startActivity(intent);
        finish();
    }
    public void MyLocation(View view)
    {
        Intent intent=new Intent(getApplicationContext(),MyLocation.class);
        startActivity(intent);
        finish();
    }*/
}