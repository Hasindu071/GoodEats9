package com.example.goodeats9;


public class DataClass {
    private String imageUri, name;

    public DataClass(){

    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageURL) {
        this.imageUri = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String caption) {
        this.name = caption;
    }

    public DataClass(String imageUri, String caption) {
        this.imageUri = imageUri;
        this.name = caption;
    }
}