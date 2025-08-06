package com.example.ecoexplorer.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ecoexplorer.MainActivity;
import com.example.ecoexplorer.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextInputEditText email;
    private TextInputEditText password;
    private TextView loginForgotPassword;
    private Button loginBtn;
    private TextView registerBtn;
    private CardView loginAsGuest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);

        mAuth = FirebaseAuth.getInstance();

        /*--------------------------
        * REMOVE THE TOP ACTION BAR
        * -------------------------*/
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        /* Login Button */
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> loginUser());

        /* Forgot Password Button */
        loginForgotPassword = findViewById(R.id.forgotPassword);
        loginForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        /* Register Button */
        registerBtn = findViewById(R.id.goto_register);
        registerBtn.setOnClickListener(v2 -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
        return capabilities != null &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
    }

    public void loginUser() {

        String email = ((TextInputEditText) findViewById(R.id.loginEmail)).getText().toString().trim();
        String password = ((TextInputEditText) findViewById(R.id.loginPassword)).getText().toString().trim();

        // Check if email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter all the blank fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null) {
                            String userId = user.getUid();

                            // Fetch username from Realtime Database
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(userId);
                            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists() && dataSnapshot.hasChild("username")) {
                                        String username = dataSnapshot.child("username").getValue(String.class);

                                        // Save username in SharedPreferences
                                        SharedPreferences prefs = getSharedPreferences("EcoPrefs", MODE_PRIVATE);
                                        prefs.edit().putString("username", username).apply();

                                        // Go to MainActivity
                                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Username not found in database", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(LoginActivity.this, "Database error", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed: " +
                                task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}