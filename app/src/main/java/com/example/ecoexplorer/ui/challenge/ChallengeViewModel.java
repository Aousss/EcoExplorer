package com.example.ecoexplorer.ui.challenge;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChallengeViewModel extends ViewModel {

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