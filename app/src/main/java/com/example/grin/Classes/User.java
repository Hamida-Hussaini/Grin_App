package com.example.grin.Classes;

import android.app.Application;
import android.content.Intent;
import android.text.BoringLayout;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.grin.DashBoard;
import com.example.grin.SignUp;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class User {
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String errorMsg;
    String firstName,lastName,email,mobile,password,userId,location;
    boolean mobileVerified,emailVerified;
    double longitude,latitude;
    public User() {
    }

    public User(String firstName, String LastName,String email, String mobile) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = LastName;
        this.mobile = mobile;
    }
    public User(Double longitude,Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("longitude", longitude);
        result.put("latitude", latitude);
        return result;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public boolean isMobileVerified() {
        return mobileVerified;
    }
    public void setMobileVerified(boolean mobileVerified) {
        this.mobileVerified = mobileVerified;
    }
    public boolean isEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getMobile() {
        return mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    //================check if user already exist
    public boolean checkIfUserExist() throws Exception {
        try {
            final boolean[] check = new boolean[1];
            fAuth = FirebaseAuth.getInstance();
            FirebaseUser user=fAuth.getCurrentUser();
            if(user!=null)
            {
                check[0]= true;
            }
            else
            {
                check[0]= false;
            }
            return check[0];

        }catch (Exception ex) {
            throw new  Exception(ex.toString());
        }
        finally {
        }
    }
    //==================create user account

    public boolean createAccount() throws Exception{

         try {
                 fAuth = FirebaseAuth.getInstance();
                 fStore=FirebaseFirestore.getInstance();
                 fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         if(task.isSuccessful())
                         {
                             try {
                                 insertUserDetail();
                             } catch (Exception e) {
                                 e.printStackTrace();
                             }
                         }
                         else
                         {
                             ErrorMEssages.errorMsg=task.getException().toString();
                             ErrorMEssages.check=false;
                         }
                     }
                 });
                 return ErrorMEssages.check;

         }
         catch (Exception ex) {
            throw new Exception(ex.getMessage());
         } finally {

        }
     }

     public void insertUserDetail() throws Exception{
        try {
            userId = fAuth.getCurrentUser().getUid();

            DocumentReference documentReference = fStore.collection("Users").document(userId);

            Map<String, Object> user = new HashMap<>();
            user.put("FirstName", firstName);
            user.put("LastName", lastName);
            user.put("Mobile", mobile);
            user.put("Email", email);
            documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        ErrorMEssages.errorMsg=null;
                        ErrorMEssages.check=false;

                    } else {

                        ErrorMEssages.errorMsg="Error: "+task.getException().toString();
                        ErrorMEssages.check=false;

                    }
                }



            });


        }
        catch (Exception ex)
        {
            throw new Exception(ex.toString());
        } finally {

        }

     }
}
