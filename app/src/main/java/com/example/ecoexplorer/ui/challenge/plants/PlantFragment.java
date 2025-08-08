package com.example.ecoexplorer.ui.challenge.plants;

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
import com.example.ecoexplorer.ui.challenge.BaseCategory;
import com.example.ecoexplorer.ui.challenge.CategoryAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PlantFragment extends Fragment {

    RecyclerView plantsRecyclerView;
    List<BaseCategory> plantsCategoryList;
    CategoryAdapter categoryAdapter;
    DatabaseReference databaseReference;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.challenge_plants, container, false);

        // Initialize RecyclerView
        plantsRecyclerView = view.findViewById(R.id.recycler_plants_categories);
        plantsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize list and adapter
        plantsCategoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(getContext(), plantsCategoryList);
        plantsRecyclerView.setAdapter(categoryAdapter);

        // Connect to Realtime Database
        databaseReference = FirebaseDatabase.getInstance().getReference("plants_categories");

        // Load plants categories from Firebase
        loadPlantsCategories();

        // Back to Challenge Fragment
        ImageView backToChallenge = view.findViewById(R.id.back_to_challenge);
        backToChallenge.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PlantFragment.this);
            navController.popBackStack(); // Goes back to previous fragment in nav graph
        });

        // NAVIGATE TO QUIZ
        CardView quizChallenge = view.findViewById(R.id.quiz_challenge);
        quizChallenge.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(PlantFragment.this);
            navController.navigate(R.id.action_navigation_plants_to_quiz_plants);
        });

        return view;
    }

    private void loadPlantsCategories() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                plantsCategoryList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PlantsCategory category = dataSnapshot.getValue(PlantsCategory.class);
                    if (category != null) {
                        plantsCategoryList.add(category); // PlantsCategory implements BaseCategory
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
