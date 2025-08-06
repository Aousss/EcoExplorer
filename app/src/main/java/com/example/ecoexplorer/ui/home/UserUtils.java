package com.example.ecoexplorer.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecoexplorer.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserUtils {

    public static void loadUsername(Context context, UserViewModel viewModel) {
        SharedPreferences prefs = context.getSharedPreferences("EcoPrefs", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "Username");

        if (viewModel != null) {
            viewModel.setUsername(username); // Update ViewModel supaya UI terpapar
        }
    }

    public static String getCachedUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("EcoPrefs", Context.MODE_PRIVATE);
        return prefs.getString("username", "Username");
    }

    // To clear on logout
    public static void clearCachedUsername(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        prefs.edit().remove("cached_username").apply();
    }
}
