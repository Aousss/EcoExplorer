package com.example.ecoexplorer.ui.challenge;

public class Category {
    private String name;
    private String imageUrl;

    public Category() {
        // Required for Firebase
    }

    public Category(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
