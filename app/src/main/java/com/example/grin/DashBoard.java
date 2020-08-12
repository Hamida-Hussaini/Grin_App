package com.example.grin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class DashBoard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
    }
    public void logOut(View view)
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
    }
}