package com.example.grin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

public class LoginUser extends AppCompatActivity {
    Button callSignUp, login_btn;
    ImageView btnBack;
    TextView error;
    TextInputLayout email, password;
    ProgressBar progressBar;
    FirebaseAuth fAuth;
    Toolbar toolbar;
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
         } else {
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
         try {
             super.onCreate(savedInstanceState);
             setContentView(R.layout.activity_login_user);
             toolbar = findViewById(R.id.main_toolbar);
             toolbar.setTitle("Login");
             setSupportActionBar(toolbar);


             email = findViewById(R.id.txtLoginEmail);
             password = findViewById(R.id.txtLoginPwd);
             login_btn = findViewById(R.id.btnLogin);
             error = findViewById(R.id.txtLoginError);
             progressBar = findViewById(R.id.loginProgress);
             error.setVisibility(View.INVISIBLE);

             toolbar = findViewById(R.id.main_toolbar);
             toolbar.setTitle("Login");
             setSupportActionBar(toolbar);

             btnBack = findViewById(R.id.btnBack);
             btnBack.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     Intent intent = new Intent(LoginUser.this, MainActivity.class);
                     startActivity(intent);
                     finish();


                 }
             });

             FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
             if (user != null) {
                 Intent intent = new Intent(getApplicationContext(), DashBoard.class);
                 startActivity(intent);
                 finish();

             }
             login_btn.setOnClickListener(new View.OnClickListener() {
                 public void onClick(View view) {
                     hideSoftKeyboard();
                     loginUser();
                 }
             });
         } catch (Exception ex) {
             progressBar.setVisibility(View.INVISIBLE);

             error.setVisibility(View.VISIBLE);
             error.setText("Sorry, something went wrong. Please try again!");
         }


    }
    private void hideSoftKeyboard()
    {
        View view=this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);

        }
    }
    public void loginUser() {
        try {
            if (!validatePassword() | !validateEmail()) {
                return;

            } else {

                fAuth = FirebaseAuth.getInstance();
                progressBar.setVisibility(View.VISIBLE);
                error.setVisibility(View.INVISIBLE);
                String em = email.getEditText().getText().toString().trim();
                String pwd = password.getEditText().getText().toString().trim();
                fAuth.signInWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginUser.this, "Logged in successfully.", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(getApplicationContext(), DashBoard.class);
                            startActivity(intent);
                            finish();

                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            error.setVisibility(View.VISIBLE);
                            error.setText("Sorry, something went wrong. Please try again!");
                        }
                    }
                });


            }
        } catch (Exception ex) {
            progressBar.setVisibility(View.INVISIBLE);

            error.setVisibility(View.VISIBLE);
            error.setText("Sorry, something went wrong. Please try again!");
        }
    }
}