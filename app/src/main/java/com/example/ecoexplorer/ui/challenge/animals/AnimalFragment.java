package com.example.ecoexplorer.ui.challenge.animals;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AnimalFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_animals, container, false);

        // BACK TO CHALLENGE FRAGMENT
        ImageView backToChallenge = view.findViewById(R.id.back_to_challenge);
        backToChallenge.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(AnimalFragment.this);
            navController.popBackStack(); // Goes back to previous fragment in nav graph
        });

        // NAVIGATE TO QUIZ
        CardView quizChallenge = view.findViewById(R.id.quiz_challenge);
        quizChallenge.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(AnimalFragment.this);
            navController.navigate(R.id.action_navigation_animals_to_quiz_animal);
        });

        return view;
    }
}
