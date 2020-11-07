package com.example.grin.models;

public class ModalClassFoodListing {
    int image;
    int UserImage;
    String title;
    String userName;
    String distance;
    String views;
    public ModalClassFoodListing(int image, int userImage, String title, String userName, String distance, String views) {
        this.image = image;
        UserImage = userImage;
        this.title = title;
        this.userName = userName;
        this.distance = distance;
        this.views = views;
    }
    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getUserImage() {
        return UserImage;
    }

    public void setUserImage(int userImage) {
        UserImage = userImage;
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
