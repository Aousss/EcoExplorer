package com.example.ecoexplorer.ui.challenge.animals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ecoexplorer.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AnimalsChallengeQuiz extends Fragment {

    private TextView Animal_recentScore_quiz;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_quiz, container, false);

        /* SET PLANTS RECENT SCORE VISIBILITY - GONE */
        TextView plants_recentScore_quiz = view.findViewById(R.id.Plants_recentScore_quiz);
        plants_recentScore_quiz.setVisibility(View.GONE);
        CardView startQuizPlants = view.findViewById(R.id.Plants_btnStart_quiz);
        startQuizPlants.setVisibility(View.GONE);

        // Back to Challenge Fragment - Animals
        ImageButton backToChallengeAnimals = view.findViewById(R.id.back_to_quiz);
        backToChallengeAnimals.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(AnimalsChallengeQuiz.this);
            navController.popBackStack(); // Goes back to previous fragment in nav graph
        });

        // Start PlantsQuiz Button
        CardView startQuiz = view.findViewById(R.id.Animals_btnStart_quiz);
        startQuiz.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(AnimalsChallengeQuiz.this);
            navController.navigate(R.id.action_quiz_animal_to_quiz_start_animal);
        });

        // Set Animal Recent Score
        Animal_recentScore_quiz = view.findViewById(R.id.Animal_recentScore_quiz);
        mAuth = FirebaseAuth.getInstance();

        loadRecentScore();

        return view;
    }

    private void loadRecentScore() {
        String uid = mAuth.getCurrentUser().getUid();
        String gameType = "quiz";
        String category = "animals";

        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("results");
        resultsRef.child(gameType).child(category).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Long score = snapshot.child("score").getValue(Long.class);
                            Long total = snapshot.child("total").getValue(Long.class);

                            if (score != null && total != null) {
                                Animal_recentScore_quiz.setText(score + "/" + total);
                            } else {
                                Animal_recentScore_quiz.setText("N/A");
                            }
                        } else {
                            Animal_recentScore_quiz.setText("N/A");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Animal_recentScore_quiz.setText("N/A");
                    }
                });
    }
}