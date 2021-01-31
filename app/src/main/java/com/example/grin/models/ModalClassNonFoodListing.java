package com.example.grin.models;

public class ModalClassNonFoodListing {
    //Uri itemUri;
    String describtion;
    String itamType;
    String itemName;
    String itemUri;
    String lastDate;
    Double latitude;
    String listingDate;

    Double longitude;
    int noLike;
    int noRequest;
    int noViews;
    String pickupTime;
    int quantity;
    String status;
    String userId;
    String userImage;
    String userName;
    String wantedList;

    public ModalClassNonFoodListing(String describtion, String itamType, String itemName, String itemUri, String lastDate, String listingDate,   String pickupTime, String status, String userId, String userImage, String userName) {
        this.describtion = describtion;
        this.itamType = itamType;
        this.itemName = itemName;
        this.itemUri = itemUri;
        this.lastDate = lastDate;
        this.listingDate = listingDate;
        this.pickupTime = pickupTime;
        this.status = status;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
    }

    public ModalClassNonFoodListing(String describtion, String itamType, String itemName, String itemUri, String lastDate, Double latitude, String listingDate, Double longitude, Integer noLike, Integer noRequest, Integer noViews, String pickupTime, Integer quantity, String status, String userId, String userImage, String userName, String wantedList) {
        this.describtion = describtion;
        this.itamType = itamType;
        this.itemName = itemName;
        this.itemUri = itemUri;
        this.lastDate = lastDate;
        this.latitude = latitude;
        this.listingDate = listingDate;
        this.longitude = longitude;
        this.noLike = noLike;
        this.noRequest = noRequest;
        this.noViews = noViews;
        this.pickupTime = pickupTime;
        this.quantity = quantity;
        this.status = status;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.wantedList = wantedList;
    }

    public ModalClassNonFoodListing(String describtion, String itamType, String itemName, String itemUri, String lastDate, String listingDate, String pickupTime, String status, String userId, String userImage, String userName, Integer noViews,Boolean wantedList) {
        this.describtion = describtion;
        this.itamType = itamType;
        this.itemName = itemName;
        this.itemUri = itemUri;
        this.lastDate = lastDate;
        this.listingDate = listingDate;
        this.pickupTime = pickupTime;
        this.status = status;
        this.userId = userId;
        this.userImage = userImage;
        this.userName = userName;
        this.noViews=noViews;
    }

    public ModalClassNonFoodListing(String itemName,String itamType, String itemUri, String lastDate, String listingDate, String status, String userId, Integer noViews, Double latitude, Double longitude,String wantedList) {
        this.itamType = itamType;
        this.itemName = itemName;
        this.itemUri = itemUri;
        this.lastDate = lastDate;
        this.listingDate = listingDate;
        this.status = status;
        this.userId = userId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.noViews=noViews;
        this.wantedList=wantedList;

    }

    public String getDescribtion() {
        return describtion;
    }

    public void setDescribtion(String describtion) {
        this.describtion = describtion;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public String getItamType() {
        return itamType;
    }

    public void setItamType(String itamType) {
        this.itamType = itamType;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemUri() {
        return itemUri;
    }

    public void setItemUri(String itemUri) {
        this.itemUri = itemUri;
    }

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public String getListingDate() {
        return listingDate;
    }

    public void setListingDate(String listingDate) {
        this.listingDate = listingDate;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getNoLike() {
        return noLike;
    }

    public void setNoLike(int noLike) {
        this.noLike = noLike;
    }

    public int getNoRequest() {
        return noRequest;
    }

    public void setNoRequest(int noRequest) {
        this.noRequest = noRequest;
    }

    public int getNoViews() {
        return noViews;
    }

    public void setNoViews(int noViews) {
        this.noViews = noViews;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getWantedList() {
        return wantedList;
    }

    public void setWantedList(String wantedList) {
        this.wantedList = wantedList;
    }



}
