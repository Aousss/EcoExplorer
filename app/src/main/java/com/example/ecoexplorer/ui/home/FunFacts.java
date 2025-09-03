package com.example.ecoexplorer.ui.home;

public class FunFacts {
    public String text;
    public String description;
    public String imageUrl;
    public String source;

    public FunFacts() {} // required for Firebase

    public FunFacts(String text, String description, String imageUrl, String source) {
        this.text = text;
        this.description = description;
        this.imageUrl = imageUrl;
        this.source = source;
    }
}
