package com.example.grin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.grin.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Signup extends AppCompatActivity {
    Button btnSignup, btnLogin;
    ImageView image;
    TextView logo, slogan, error;
    TextInputLayout firstName, lastName, email, mobile, password;
    ProgressBar progressBar;
    User user1 = new User();
    FirebaseAuth fAuth;
    FirebaseDatabase rootnode;
    DatabaseReference reference;

    private Boolean validateFirstName() {
        String val = firstName.getEditText().getText().toString();
        String noWhiteSpace = "\\A\\w{3,20}\\z";
        if (val.isEmpty()) {
            firstName.setError("Field cannot be empty");
            return false;
        } else if (val.length() >= 20 || val.length() < 3) {
            firstName.setError("First Name should be between 3-20 characters");
            return false;
        } else if (!val.matches(noWhiteSpace)) {
            firstName.setError("White Spaces are not allowed");
            return false;
        } else {
            firstName.setError(null);
            firstName.setErrorEnabled(false);
            return true;
        }
    }

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
        String passwordVal = "^" +
                //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +      //any letter
                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                "(?=\\S+$)" +           //no white spaces
                ".{4,}" +               //at least 4 characters
                "$";

        if (val.isEmpty()) {
            password.setError("Field cannot be empty");
            return false;
        } else if (!val.matches(passwordVal)) {
            password.setError("Password is too weak");
            return false;
        } else {
            password.setError(null);
            password.setErrorEnabled(false);
            return true;
        }
    }

    ImageView btnBack;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_signup);
            toolbar = findViewById(R.id.main_toolbar);
            toolbar.setTitle("Signup");
            setSupportActionBar(toolbar);


            btnBack = findViewById(R.id.btnBack);
            btnBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Signup.this, MainActivity.class);
                    startActivity(intent);
                    finish();


                }
            });
            logo = findViewById(R.id.title);
            btnSignup = findViewById(R.id.btnSignupRegister);
            firstName = findViewById(R.id.txtSignupFirstName);
            lastName = findViewById(R.id.txtSignupLastName);
            email = findViewById(R.id.txtSignupEmail);
            password = findViewById(R.id.txtSignupPassword);
            progressBar = findViewById(R.id.sigupProgress);
            error = findViewById(R.id.txtSignupError);


            error.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

            if (user1.checkIfUserExist()) {
                Intent intent = new Intent(getApplicationContext(), DashBoard.class);
                startActivity(intent);
                finish();
            }

            btnSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    insertNewUser();
                }
            });
        } catch (Exception ex) {
            progressBar.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
            error.setText("Sorry, something went wrong. Please try again!");
           /* error.setVisibility(View.VISIBLE);
            error.setText(ex.toString());*/
        }
    }

    public void insertNewUser() {
        try {

            if (!validateFirstName() | !validateEmail() | !validatePassword()) {
                return;

            } else {

                progressBar.setVisibility(View.VISIBLE);
                error.setVisibility(View.INVISIBLE);

                final String em = email.getEditText().getText().toString().trim();
                final String pwd = password.getEditText().getText().toString().trim();
                final String fName = firstName.getEditText().getText().toString().trim();
                final String lName = lastName.getEditText().getText().toString().trim();
              //  final String phone = mobile.getEditText().getText().toString().trim();
                fAuth = FirebaseAuth.getInstance();
                fAuth.createUserWithEmailAndPassword(em, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String userId;
                            userId = fAuth.getCurrentUser().getUid();
                            rootnode = FirebaseDatabase.getInstance();
                            reference = rootnode.getReference("users");
                            User obj = new User(fName, lName, em);
                            reference.child(userId).setValue(obj);
                            progressBar.setVisibility(View.INVISIBLE);

                            Toast.makeText(Signup.this, "User account created successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Signup.this, DashBoard.class);
                            startActivity(intent);
                            finish();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            error.setVisibility(View.VISIBLE);
                            error.setText("Sorry, something went wrong. Please try again!");
                           /*error.setVisibility(View.VISIBLE);
                            error.setText(task.getException().toString());*/
                        }
                    }
                });


            }
        } catch (Exception ex) {
            progressBar.setVisibility(View.INVISIBLE);
            error.setVisibility(View.VISIBLE);
            error.setText("Sorry, something went wrong. Please try again!");
            //error.setText(""+ex.toString());

        }
    }
}