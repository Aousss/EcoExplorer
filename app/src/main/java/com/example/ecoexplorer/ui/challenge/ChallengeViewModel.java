package com.example.ecoexplorer.ui.challenge;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChallengeViewModel extends ViewModel {
    // TODO: Implement the ViewModel

    private MutableLiveData<String> mText;

    public ChallengeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is challenge fragment");
    }

    public MutableLiveData<String> getText() {
        return mText;
    }
}