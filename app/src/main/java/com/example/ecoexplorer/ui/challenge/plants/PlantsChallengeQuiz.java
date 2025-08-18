package com.example.ecoexplorer.ui.challenge.plants;

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

public class PlantsChallengeQuiz extends Fragment {

    private TextView Plants_recentScore_quiz;

    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_quiz, container, false);

        /* SET PLANTS RECENT SCORE VISIBILITY - GONE */
        TextView animal_recentScore_quiz = view.findViewById(R.id.Animal_recentScore_quiz);
        animal_recentScore_quiz.setVisibility(View.GONE);
        CardView startQuizAnimals = view.findViewById(R.id.Animals_btnStart_quiz);
        startQuizAnimals.setVisibility(View.GONE);

        // Back to Challenge Fragment - Plants
        ImageButton backToChallengePlants = view.findViewById(R.id.back_to_quiz);
        backToChallengePlants.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PlantsChallengeQuiz.this);
            navController.popBackStack(); // Goes back to previous fragment in nav graph
        });

        // Start PlantsQuiz Button - Plants
        CardView startQuiz = view.findViewById(R.id.Plants_btnStart_quiz);
        startQuiz.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PlantsChallengeQuiz.this);
            navController.navigate(R.id.action_quiz_plants_to_quiz_start_plants);
        });

        // Set Plants Recent Score
        Plants_recentScore_quiz = view.findViewById(R.id.Plants_recentScore_quiz);
        mAuth = FirebaseAuth.getInstance();

        loadRecentScore();

        return view;
    }

    private void loadRecentScore() {
        String uid = mAuth.getCurrentUser().getUid();
        String gameType = "quiz";
        String category = "plants";

        DatabaseReference resultsRef = FirebaseDatabase.getInstance().getReference("results");
        resultsRef.child(gameType).child(category).child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Long score = snapshot.child("score").getValue(Long.class);
                            Long total = snapshot.child("total").getValue(Long.class);

                            if (score != null && total != null) {
                                Plants_recentScore_quiz.setText(score + "/" + total);
                            } else {
                                Plants_recentScore_quiz.setText("N/A");
                            }
                        } else {
                            Plants_recentScore_quiz.setText("N/A");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Plants_recentScore_quiz.setText("N/A");
                    }
                });
    }
}
