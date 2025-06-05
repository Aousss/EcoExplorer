package com.example.ecoexplorer.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoexplorer.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseAuth mAuth;

    private Button registerBtn;
    private LinearLayout loginBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // REMOVE THE TOP ACTION BAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        registerBtn = findViewById(R.id.btn_register);
        loginBtn = findViewById(R.id.back_to_login);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        registerBtn.setOnClickListener( view -> registerUser());

    }

    /* Register User */
    private void registerUser() {
        String email = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.register_email)).getText()).toString().trim();
        String password = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.register_password)).getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.register_confirm_password)).getText()).toString().trim();
        String username = Objects.requireNonNull(((TextInputEditText) findViewById(R.id.register_username)).getText()).toString().trim();
        int age = Integer.parseInt(Objects.requireNonNull(((TextInputEditText) findViewById(R.id.user_age)).getText()).toString().trim());
        String role = Objects.requireNonNull(((AutoCompleteTextView) findViewById(R.id.user_role)).getText()).toString().trim();

        AutoCompleteTextView roleDown = findViewById(R.id.user_role);
        String[] roles = new String[] {"Student", "Teacher", "Tutor", "Parent"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                roles
        );
        roleDown.setAdapter(adapter);

        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Enter all the blank fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "The passwords do not match.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Check if username already exists
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                // Check if the query result is empty
                if (!task.getResult().isEmpty()) {

                    // Username already exists
                    Toast.makeText(RegisterActivity.this, "Username already taken", Toast.LENGTH_SHORT).show();
                } else {

                    // Username is available, proceed with registration
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(authTask -> {

                                // Check if registration was successful
                                if (authTask.isSuccessful()) {
                                    String uid = mAuth.getCurrentUser().getUid();

                                    // Save user data to Firestore
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("email", email);
                                    user.put("password", password);
                                    user.put("username", username);
                                    user.put("age", age);
                                    user.put("role", role);
                                    user.put("uid", uid);
                                    user.put("createdAt", System.currentTimeMillis());

                                    db.collection("users")
                                            .document(username)
                                            .set(user)
                                            .addOnSuccessListener(unused -> {
                                                Toast.makeText(RegisterActivity.this, "Register Successful", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                                finish();
                                            })
                                            .addOnFailureListener(e -> {
                                                // Registration failed
                                                Log.e("FirestoreSave", "Failed to save user: ", e);
                                                Toast.makeText(RegisterActivity.this, "Error saving user: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                            });

                                } else {
                                    // Registration failed
                                    Toast.makeText(RegisterActivity.this, "Register failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }
            }
        });
    }


    // User data model
    public static class User {
        public String email;

        public User(String email) {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String userID, String password) {
            this.email = userID;
        }
    }
}
