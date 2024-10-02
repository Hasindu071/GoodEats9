package com.example.goodeats9;

public class Recipe {
    private String title;
    private String description;
    private String serves;
    private String cookTime;
    private String imageUrl; // New field for image URL

    // Default constructor required for calls to DataSnapshot.getValue(Recipe.class)
    public Recipe() {
    }

    public Recipe(String title, String description, String serves, String cookTime, String imageUrl) {
        this.title = title;
        this.description = description;
        this.serves = serves;
        this.cookTime = cookTime;
        this.imageUrl = imageUrl; // Initialize image URL
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public String getCookTime() {
        return cookTime;
    }

    public void setCookTime(String cookTime) {
        this.cookTime = cookTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
