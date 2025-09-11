package com.example.ecoexplorer.ui.challenge;

public class Results {
    private String quizName;
    private int score;

    public Results() {} // Firebase needs empty constructor

    public Results(String quizName, int score) {
        this.quizName = quizName;
        this.score = score;
    }

    public String getQuizName() {
        return quizName;
    }

    public int getScore() {
        return score;
    }
}

