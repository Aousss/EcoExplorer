package com.example.ecoexplorer.ui.challenge.animals;

import com.example.ecoexplorer.ui.challenge.BaseCategory;

public class AnimalCategory implements BaseCategory {
    private String name;
    private String imageUrl;

    public AnimalCategory() {}

    public AnimalCategory(String name, String imageUrl) {
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
