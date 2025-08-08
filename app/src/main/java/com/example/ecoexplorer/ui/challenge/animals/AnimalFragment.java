package com.example.ecoexplorer.ui.challenge;

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
import androidx.fragment.app.FragmentTransaction;
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

    RecyclerView animalsRecyclerView;
    List<BaseCategory> animalsCategoryList;
    CategoryAdapter categoryAdapter;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_animals, container, false);

        // Initialize RecyclerView
        animalsRecyclerView = view.findViewById(R.id.recycler_animals_categories);
        animalsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize list and adapter
        animalsCategoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), animalsCategoryList);
        animalsRecyclerView.setAdapter(categoryAdapter);


        // Connect to Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("animals_categories");

        // Load plants categories from Firebase
        loadAnimalsCategories();

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

    private void loadAnimalsCategories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                animalsCategoryList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    AnimalCategory category = dataSnapshot.getValue(AnimalCategory.class);
                    if (category != null) {
                        animalsCategoryList.add(category); // AnimalCategory implements BaseCategory
                    }
                }
                categoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
