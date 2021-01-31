package com.example.grin.models;

public class TestingModel {
    String Image;
    String Name;
    String Qualification;
    public TestingModel(String image, String name, String qualification) {
        Image = image;
        Name = name;
        Qualification = qualification;
    }

    public TestingModel() {
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getQualification() {
        return Qualification;
    }

    public void setQualification(String qualification) {
        Qualification = qualification;
    }

}
