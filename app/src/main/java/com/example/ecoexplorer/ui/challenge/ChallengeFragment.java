// ChallengeFragment.java
package com.example.ecoexplorer.ui.challenge;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.databinding.ChallengeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChallengeFragment extends Fragment {

    private ChallengeViewModel mViewModel;

    RecyclerView recyclerView_results;
    ResultsAdapter resultAdapter;
    ArrayList<Results> resultLists;
    TextView none_results, seeResult;
    CardView animalCategory, plantCategory;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference databaseReference;

    private ChallengeBinding binding;

    public static ChallengeFragment newInstance() {
        return new ChallengeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = ChallengeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        /* -------------------
        * CATEGORIES
        * --------------------*/
        animalCategory = view.findViewById(R.id.cl_animals_categories);
        plantCategory = view.findViewById(R.id.cl_plants_categories);

        animalCategory.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_challenge_to_navigation_animals);
        });

        plantCategory.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_challenge_to_navigation_plants);
        });

        /*-----------------
        * RECYCLER RESULTS
        * -----------------*/
        none_results = view.findViewById(R.id.none_results);
        recyclerView_results = view.findViewById(R.id.recycler_results);
        recyclerView_results.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        displayRecentResults();

        return view;
    }

    private void displayRecentResults() {
        resultLists = new ArrayList<>();
        resultAdapter = new ResultsAdapter(getContext(), resultLists);
        recyclerView_results.setAdapter(resultAdapter);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            showNoAccountState();
            return;
        }

        String userID = currentUser.getUid();
        String gameType = "quiz";

        // 🔹 Now point to users → uid → results → gameType
        DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userID)
                .child("results")
                .child(gameType);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resultLists.clear();

                if (!snapshot.exists()) {
                    none_results.setVisibility(View.VISIBLE);
                    recyclerView_results.setVisibility(View.GONE);
                    return;
                }

                // 🔹 Loop through each category under quiz
                for (DataSnapshot categorySnapshot : snapshot.getChildren()) {
                    String categoryName = categorySnapshot.getKey();

                    Integer score = categorySnapshot.child("score").getValue(Integer.class);

                    if (score != null) {
                        Results result = new Results(categoryName, score);
                        resultLists.add(result);
                    }
                }

                resultAdapter.notifyDataSetChanged();
                none_results.setVisibility(View.GONE);
                recyclerView_results.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showNoAccountState() {

        // Show a message on screen
        none_results.setVisibility(View.VISIBLE);
        none_results.setText("⚠ Please log in or check your internet connection.");

        recyclerView_results.setVisibility(View.GONE);

        // Disable category clicks
        animalCategory.setOnClickListener(v -> showErrorState());
        plantCategory.setOnClickListener(v -> showErrorState());
    }

    private void showErrorState() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Login Required")
                .setMessage("You need to log in to continue. \nPlease log in to use this feature.")
                .setPositiveButton("Login", (dialog, which) -> {
                    NavController navController = NavHostFragment.findNavController(ChallengeFragment.this);
                    navController.navigate(R.id.action_navigation_challenge_to_login);
                })
                .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*--------
        * RESULTS
        * -------*/
        resultAdapter.notifyDataSetChanged();
    }

    public void onCancelled(@NonNull DatabaseError error) {
        Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
    }
}