package com.example.grin.models;

import android.util.Log;

public class ModalClassFoodListing {
    String itemUri;
    //Uri itemUri;
    String userImage;
    String title;
    String userName;
    String distance;
    String views;

    public String getItemUri() {
        return itemUri;
    }

    public void setItemUri(String itemUri) {
        this.itemUri = itemUri;
    }

    public ModalClassFoodListing(String itmUri, String userImage, String title, String userName, String distance, String views) {
        this.itemUri = itemUri;
        this.userImage = userImage;
        this.title = title;
        this.userName = userName;
        this.distance = distance;
        this.views = views;
        Log.d("","Data Added");
    }
    public ModalClassFoodListing() {

    }
   /* public ModalClassFoodListing(Uri itemUri, int userImage, String title, String userName, String distance, String views) {
        this.itemUri = itemUri;
        UserImage = userImage;
        this.title = title;
        this.userName = userName;
        this.distance = distance;
        this.views = views;
    }

    public Uri getItemUri() {
        return itemUri;
    }

    public void setItemUri(Uri itemUri) {
        this.itemUri = itemUri;
    }*/


    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }


}
