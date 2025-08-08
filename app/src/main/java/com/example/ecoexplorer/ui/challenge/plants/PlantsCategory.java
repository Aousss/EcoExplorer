package com.example.ecoexplorer.ui.challenge;

public class PlantsCategory implements BaseCategory {
    private String name;
    private String imageUrl;

    public PlantsCategory() {}

    public PlantsCategory(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }
}
