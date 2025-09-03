package com.example.ecoexplorer.ui.identify;

public class Animals {
    private String name;
    private int imageResId;

    public Animals(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}
