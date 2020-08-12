package com.example.grin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

public class Login extends AppCompatActivity {
    Button callSignUp,login_btn;
    ImageView image;
    TextView logo,slogan,error;
    TextInputLayout email,password;
    ProgressBar progressBar;
    FirebaseAuth fAuth;

    String userId;
    private Boolean validateEmail() {
        String val = email.getEditText().getText().toString();
        String emailPattren = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if (val.isEmpty()) {
            email.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(emailPattren)) {
            email.setError("invalid Email Address.");
            return false;
        }
        else
        {
            email.setError(null);
            email.setErrorEnabled(false);
            return true;
        }
    }
    private Boolean validatePassword() {
        String val = password.getEditText().getText().toString();

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try
        {
                super.onCreate(savedInstanceState);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                setContentView(R.layout.activity_login);

                callSignUp = findViewById(R.id.btnCreateAcccount);
                image = findViewById(R.id.logo_image);
                logo = findViewById(R.id.logo_name);
                slogan = findViewById(R.id.slogan_name);
                email = findViewById(R.id.txtLoginEmail);
                password = findViewById(R.id.txtLoginPwd);
                login_btn = findViewById(R.id.btnLogin);
                error = findViewById(R.id.txtLoginError);
                progressBar = findViewById(R.id.loginProgress);
                error.setVisibility(View.INVISIBLE);

                callSignUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Login.this, SignUp.class);
                        startActivity(intent);
                        finish();


                    }
                });
                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
                if(user!=null)
                {
                    Intent intent=new Intent(getApplicationContext(),DashBoard.class);
                    startActivity(intent);
                    finish();

                }
                login_btn.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        loginUser();
                    }
                });
        }
         catch (Exception ex)
        {
            progressBar.setVisibility(View.INVISIBLE);

            error.setVisibility(View.VISIBLE);
            error.setText(ex.toString());
        }
    }
    public void loginUser()
    {
        try {
            if(!validatePassword() | !validateEmail())
            {
                return;

            }
            else {

                fAuth = FirebaseAuth.getInstance();
                progressBar.setVisibility(View.VISIBLE);
                error.setVisibility(View.INVISIBLE);
                String em = email.getEditText().getText().toString().trim();
                String pwd = password.getEditText().getText().toString().trim();
                fAuth.signInWithEmailAndPassword(em,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(Login.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();

                            Intent intent=new Intent(getApplicationContext(),DashBoard.class);
                            startActivity(intent);
                            finish();

                        }
                        else
                        {
                            progressBar.setVisibility(View.INVISIBLE);
                            error.setVisibility(View.VISIBLE);
                            error.setText(task.getException().toString());
                        }
                    }
                });


            }
        }
        catch (Exception ex)
        {
            progressBar.setVisibility(View.INVISIBLE);

            error.setVisibility(View.VISIBLE);
            error.setText(ex.toString());
        }



    }
}