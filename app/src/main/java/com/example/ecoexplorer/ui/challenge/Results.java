package com.example.ecoexplorer.ui.challenge;

public class Results {
    private String quizName;
    private int score;
    private int total;
    private String date;

    public Results() {} // Firebase needs empty constructor

    public Results(String quizName, int score, int total, String date) {
        this.quizName = quizName;
        this.score = score;
        this.total = total;
        this.date = date;
    }

    public String getQuizName() {
        return quizName;
    }

    public int getScore() {
        return score;
    }

    public int getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }
}

