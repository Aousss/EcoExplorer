package com.example.ecoexplorer.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {
    private final MutableLiveData<String> username = new MutableLiveData<>();

    public LiveData<String> getUsername() {
        return username;
    }

    public void setUsername(String newUsername) {
        username.setValue(newUsername);
    }
}
