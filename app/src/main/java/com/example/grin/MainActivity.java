package com.example.grin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.grin.Classes.User;

public class MainActivity extends AppCompatActivity {
    private  static int SPLASH_SCREEN=5000;
    Animation topAnim,bottomAnim;
    ImageView image;
    TextView logo,slogan;
    Button callSignUp,login_btn;
    User user1=new User();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_main);

            callSignUp = findViewById(R.id.btnMainSignUp);
            login_btn=findViewById(R.id.btnMainLogin);
            if(user1.checkIfUserExist())
            {
                Intent intentDashboard=new Intent(getApplicationContext(),DashBoard.class);
                startActivity(intentDashboard);
                finish();
            }
            callSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, Signup.class);
                    startActivity(intent);
                    finish();


                }
            });
            login_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MainActivity.this, LoginUser.class);
                    startActivity(intent);
                    finish();


                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }





    }
}