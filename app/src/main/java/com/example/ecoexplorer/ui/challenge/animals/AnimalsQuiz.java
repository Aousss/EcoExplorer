package com.example.ecoexplorer.ui.challenge.animals;

import java.util.List;

public class AnimalsQuiz {

    private String questionText;
    private List<String> choices;
    private String answer;

    public AnimalsQuiz(String questionText, List<String> choices, String answer) {
        this.questionText = questionText;
        this.choices = choices;
        this.answer = answer;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getChoices() {
        return choices;
    }

    public String getAnswer() {
        return answer;
    }
}