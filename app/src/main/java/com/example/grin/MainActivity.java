package com.example.grin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grin.Classes.User;

public class MainActivity extends AppCompatActivity {
    private  static int SPLASH_SCREEN=5000;
    Animation topAnim,bottomAnim;
    ImageView image;
    TextView logo,slogan;
    User user1=new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);


        try {
            Intent intent=new Intent(getApplicationContext(),DashBoard.class);
            startActivity(intent);
            finish();
           /* if(user1.checkIfUserExist())
            {
                Intent intent=new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Intent intent=new Intent(MainActivity.this,Login.class);
                startActivity(intent);
                finish();

            }*/
        } catch (Exception e) {

        }

    }
}