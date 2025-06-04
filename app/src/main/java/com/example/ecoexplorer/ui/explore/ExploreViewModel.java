package com.example.ecoexplorer.ui.explore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ExploreViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ExploreViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is explore fragment");
    }

    public MutableLiveData<String> getText() {
        return mText;
    }

}