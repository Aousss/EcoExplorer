package com.example.ecoexplorer.ui.challenge;

public class Results {
    private String subject;
    private String description;

    public Results() {
        // Required for Firebase
    }

    public Results(String subject, String description) {
        this.subject = subject;
        this.description = description;
    }

    public String getSubject() {
        return subject;
    }

    public String getDescription() {
        return getDescription();
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
