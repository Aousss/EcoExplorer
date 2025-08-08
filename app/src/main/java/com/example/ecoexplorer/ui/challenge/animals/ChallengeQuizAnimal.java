package com.example.ecoexplorer.ui.challenge.animals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ecoexplorer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChallengeQuizAnimal extends Fragment {

    private TextView tvQuestionNumber, quizQuestions, quizDescription;
    private RadioGroup radioGroupChoices;
    private RadioButton rbChoice1, rbChoice2, rbChoice3, rbChoice4;
    private Button btnPrevious, btnNext, btnSubmit;

    private List<Quiz> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;
    private int score = 0;

    private DatabaseReference quizRef, userRef;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_quiz_questions, container, false);

        tvQuestionNumber = view.findViewById(R.id.tvQuestionNumber);
        quizQuestions = view.findViewById(R.id.quiz_questions);
        quizDescription = view.findViewById(R.id.quiz_description);
        radioGroupChoices = view.findViewById(R.id.radioGroupChoices);

        rbChoice1 = view.findViewById(R.id.rbChoice1);
        rbChoice2 = view.findViewById(R.id.rbChoice2);
        rbChoice3 = view.findViewById(R.id.rbChoice3);
        rbChoice4 = view.findViewById(R.id.rbChoice4);

        btnPrevious = view.findViewById(R.id.btnPrevious);
        btnNext = view.findViewById(R.id.btnNext);
        btnSubmit = view.findViewById(R.id.btnSubmit);

        quizRef = FirebaseDatabase.getInstance().getReference("quizzes/animals");
        userRef = FirebaseDatabase.getInstance().getReference("users");

        loadQuestions();

        return view;
    }

    private void loadQuestions() {
        quizRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot questionSnapshot : snapshot.getChildren()) {
                    String questionText = questionSnapshot.child("questionText").getValue(String.class);
                    List<String> choices = new ArrayList<>();
                    for (DataSnapshot choiceSnapshot : questionSnapshot.child("choices").getChildren()) {
                        choices.add(choiceSnapshot.getValue(String.class));
                    }
                    String answer = questionSnapshot.child("answer").getValue(String.class);
                    questionList.add(new Quiz(questionText, choices, answer));
                }
                showQuestion(currentQuestionIndex);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void showQuestion(int index) {
        Quiz q = questionList.get(index);
        tvQuestionNumber.setText("Question " + (index + 1) + "/" + questionList.size());
        quizQuestions.setText(q.getQuestionText());
        rbChoice1.setText(q.getChoices().get(0));
        rbChoice2.setText(q.getChoices().get(1));
        rbChoice3.setText(q.getChoices().get(2));
        rbChoice4.setText(q.getChoices().get(3));

        radioGroupChoices.clearCheck();

        btnNext.setVisibility(index == questionList.size() - 1 ? View.GONE : View.VISIBLE);
        btnPrevious.setVisibility(index == 0 ? View.GONE : View.VISIBLE);
        btnNext.setOnClickListener(v -> checkAnswerAndNext());
        btnPrevious.setOnClickListener(v -> previousAnswer());

        btnSubmit.setVisibility(index == questionList.size() - 1 ? View.VISIBLE : View.GONE);
        btnSubmit.setOnClickListener(v -> checkAnswerAndSubmit());
    }

    private void checkAnswerAndNext() {
        checkAnswer();
        currentQuestionIndex++;
        showQuestion(currentQuestionIndex);
    }

    private void previousAnswer() {
        checkAnswer();
        currentQuestionIndex--;
        showQuestion(currentQuestionIndex);
    }

    private void checkAnswerAndSubmit() {
        checkAnswer();
        saveScoreToUser();
    }

    private void checkAnswer() {
        int selectedId = radioGroupChoices.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadioButton = getView().findViewById(selectedId);
            String selectedAnswer = selectedRadioButton.getText().toString();
            if (selectedAnswer.equals(questionList.get(currentQuestionIndex).getAnswer())) {
                score++;
            }
        }
    }

    private void saveScoreToUser() {
        String uid = mAuth.getCurrentUser().getUid();
        String quizId = "animal_quiz";

        Map<String, Object> resultData = new HashMap<>();
        resultData.put("score", score);
        resultData.put("total", questionList.size());
        resultData.put("date", new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date()));
        resultData.put("time", new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date()));

        userRef.child(uid).child("quizResults").child(quizId).setValue(resultData)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(getContext(), "Score saved! You got " + score + "/" + questionList.size(), Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(getContext(), "Failed to save score", Toast.LENGTH_SHORT).show()
                );
    }
}