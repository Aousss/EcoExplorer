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
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;
// Import your custom Category model
import com.example.ecoexplorer.databinding.FragmentChallengeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChallengeFragment extends Fragment {

    private ChallengeViewModel mViewModel;

    RecyclerView recyclerView_categories, recyclerView_results;
    CategoryAdapter categoryAdapter;
    ResultsAdapter resultAdapter;
    ArrayList<Results> resultLists;
    ArrayList<Category> categoryList;
    DatabaseReference databaseReference;
    TextView none_results;
    CardView animalCategory, plantCategory;
    private FragmentChallengeBinding binding;

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


//        recyclerView_categories = view.findViewById(R.id.recycler_categories);
//        recyclerView_categories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
//
//        categoryList = new ArrayList<>();
//        categoryAdapter = new CategoryAdapter(getContext(), categoryList);
//        recyclerView_categories.setAdapter(categoryAdapter);
//
//        // Connect to Firebase Realtime Database
//        databaseReference = FirebaseDatabase.getInstance().getReference("categories");
//
//        // Listen for data changes
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                categoryList.clear();
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Category category = dataSnapshot.getValue(Category.class);
//                    categoryList.add(category);
//                }
//                categoryAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(getContext(), "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });

        /*-----------------
        * RECYCLER RESULTS
        * -----------------*/
        none_results = view.findViewById(R.id.none_results);
        recyclerView_results = view.findViewById(R.id.recycler_results);
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

        /*----------
        * CATEGORIES
        * ----------*/
//        // Observe the categories LiveData from the ViewModel
//        mViewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
//            // Update the adapter's data when categories change
//            if (categories != null) {
//                categoryAdapter.setCategories(categories);
//            }
//        });

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