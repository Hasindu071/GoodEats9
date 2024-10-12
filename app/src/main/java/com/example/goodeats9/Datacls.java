package com.example.goodeats9;

public class Datacls {

    private String recipeId; // New field for recipe ID
    private String videoUri;
    private String name;
    private String description;
    private String userName;
    private String imageUri;

    // Empty constructor required for Firebase
    public Datacls() {}

    public Datacls(String recipeId, String videoUri, String name, String description, String userName, String imageUri) {
        this.recipeId = recipeId; // Add recipe ID to the constructor
        this.videoUri = videoUri;
        this.name = name;
        this.description = description;
        this.userName = userName;
        this.imageUri = imageUri;
    }

    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    public String getVideoUri() {
        return videoUri;
    }

    public void setVideoUri(String videoUri) {
        this.videoUri = videoUri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}