// ChallengeFragment.java
package com.example.ecoexplorer.ui.challenge;

import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;
// Import your custom PlantsCategory model
import com.example.ecoexplorer.databinding.FragmentChallengeBinding;
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
    DatabaseReference databaseReference;
    TextView none_results, seeResult;
    CardView animalCategory, plantCategory;

    public static ChallengeFragment newInstance() {
        return new ChallengeFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_challenge, container, false);

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
        seeResult = view.findViewById(R.id.cl_results_seeAll);
        recyclerView_results.setLayoutManager(new LinearLayoutManager(getContext()));

        resultLists = new ArrayList<>();
        resultAdapter = new ResultsAdapter(getContext(), resultLists);
        recyclerView_results.setAdapter(resultAdapter);

        databaseReference = FirebaseDatabase.getInstance().getReference("results");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                resultLists.clear();

                if (!snapshot.exists() || snapshot.getChildrenCount() == 0) {
                    none_results.setVisibility(View.VISIBLE);
                    recyclerView_results.setVisibility(View.GONE);
                    seeResult.setVisibility(View.GONE);
                    return;
                }

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Results result = dataSnapshot.getValue(Results.class);
                    if (result != null) {
                        resultLists.add(result);
                    }
                }

                // If results found, show the RecyclerView and hide the message
                none_results.setVisibility(View.GONE);
                recyclerView_results.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ChallengeViewModel.class);

        /*--------
        * RESULTS
        * -------*/
        mViewModel.getResults().observe(getViewLifecycleOwner(), results -> {
            if (results != null) {
                resultAdapter.setResults(results);
            }
        });
    }
}