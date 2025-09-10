package com.example.ecoexplorer.ui.identify;

public class Animals {
    private String name, description, scientificName, ecosystem, habitat, role;
    private int imageResId;

    public Animals(String name, String description, String scientificName, String ecosystem, String habitat, String role, int imageResId) {
        this.name = name;
        this.description = description;
        this.scientificName = scientificName;
        this.ecosystem = ecosystem;
        this.habitat = habitat;
        this.role = role;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {return description;}

    public String getScientificName() {return scientificName;}

    public String getEcosystem() {return ecosystem;}

    public String getHabitat() {return habitat;}

    public String getRole() {return role;}

    public int getImageResId() {
        return imageResId;
    }
}
