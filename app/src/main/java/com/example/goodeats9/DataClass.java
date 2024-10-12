package com.example.goodeats9;


import java.util.List;

public class DataClass {
    private String imageUri;
    private String name;
    private String cookTime;
    private String description;
    private String serves ;
    private String UserName;
    private String userEmail;
    private String category;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public String getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(String recipeId) {
        this.recipeId = recipeId;
    }

    private String recipeId;

    private String videoUri;

    public DataClass(String imageUri, String name) {
        this.imageUri = imageUri;
        this.name = name;
    }

    public List<String> getStepList() {
        return StepList;
    }

    public void setStepList(List<String> stepList) {
        StepList = stepList;
    }

    public List<String> getIngredientList() {
        return IngredientList;
    }

    public void setIngredientList(List<String> ingredientList) {
        IngredientList = ingredientList;
    }

    private List<String> StepList;
    private List<String> IngredientList;

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public DataClass(String imageUri, String name, String cookTime, String description, String serves, String username, String videoUri, String recipeId , String email , String category) {
        this.imageUri = imageUri;
        this.name = name;
        this.cookTime = cookTime;
        this.description = description;
        this.serves = serves;
        this.UserName = username;
        this.videoUri = videoUri;
        this.recipeId = recipeId;
        this.userEmail = email;
        this.category = category;
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