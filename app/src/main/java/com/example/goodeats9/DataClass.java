package com.example.goodeats9;


public class DataClass {
    private String imageUri;
    private String name;
    private String cookTime;
    private String description;
    private String serves ;
    private String UserName;

    private String videoUri;

    public DataClass(String imageUri, String name, String cookTime, String description, String serves , String UserName , String videoUri) {
        this.imageUri = imageUri;
        this.name = name;
        this.cookTime = cookTime;
        this.description = description;
        this.serves = serves;
        this.UserName = UserName;
        this.videoUri = videoUri;
    }

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServes() {
        return serves;
    }

    public void setServes(String serves) {
        this.serves = serves;
    }

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

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getVideoUri() {return videoUri;}

    public void setVideoUri(String videoUri) {this.videoUri = videoUri;}
}