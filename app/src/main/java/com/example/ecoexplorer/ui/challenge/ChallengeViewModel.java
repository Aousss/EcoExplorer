package com.example.ecoexplorer.ui.challenge;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecoexplorer.ui.challenge.plants.PlantsCategory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChallengeViewModel extends ViewModel {

    /*--------------
    * CATEGORIES
    * --------------*/
    private MutableLiveData<List<PlantsCategory>> categories = new MutableLiveData<>();

    public LiveData<List<PlantsCategory>> getCategories() {
        if (categories.getValue() == null) {
            loadCategories();
        }
        return categories;
    }

    private void loadCategories() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("categories");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<PlantsCategory> plantsCategoryList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    // Use your custom PlantsCategory.class here
                    PlantsCategory plantsCategory = snap.getValue(PlantsCategory.class);
                    if (plantsCategory != null) {
                        plantsCategoryList.add(plantsCategory);
                    }
                }
                categories.setValue(plantsCategoryList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // handle error, e.g., Log.e("ChallengeViewModel", "Failed to load categories", error.toException());
            }
        });
    }

    /*----------------
    * RESULTS
    * ----------------*/
    private MutableLiveData<List<Results>> results = new MutableLiveData<>();

    public LiveData<List<Results>> getResults() {
        if (results.getValue() == null) {
            loadResults();
        }
        return results;
    }

    private void loadResults() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("results");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Results> resultsList = new ArrayList<>();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Results result = snap.getValue(Results.class);
                    if (result != null) {
                        resultsList.add(result);
                    }
                }
                results.setValue(resultsList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // handle error, e.g., Log.e("ChallengeViewModel", "Failed to load results", error.toException());
            }
        });
    }
}