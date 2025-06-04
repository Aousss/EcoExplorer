package com.example.ecoexplorer;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<String> username = new MutableLiveData<>();

    public void setUsername(String name) {
        username.setValue(name);
    }

    public LiveData<String> getUsername() {
        return username;
    }
}
