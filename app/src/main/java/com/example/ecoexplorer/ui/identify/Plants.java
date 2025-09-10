package com.example.ecoexplorer.ui.identify;

public class Plants {
    private String name, description;
    private int imageResId;

    public Plants(String name, String description, int imageResId) {
        this.name = name;
        this.description = description;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() { return description;}

    public int getImageResId() {
        return imageResId;
    }
}
