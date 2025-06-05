package com.example.ecoexplorer.ui.profile;

import android.content.Intent;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

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

        // REMOVE THE TOP ACTION BAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Login Button Logic
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> loginUser());

        /* Login as Guest Button Logic */
        loginAsGuest = findViewById(R.id.login_as_guest);
        loginAsGuest.setOnClickListener(v -> guestUser());

        // Forgot Password Button Logic
        loginForgotPassword = findViewById(R.id.forgotPassword);
        loginForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        /* Register Button Logic */
        registerBtn = findViewById(R.id.goto_register);
        registerBtn.setOnClickListener(v2 -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public void guestUser() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Generate a random username
                        String guestUsername = "username_guest";
                        if(!isValidUsername(guestUsername)) {
                            guestUsername = "user_" + UUID.randomUUID().toString().substring(0, 6);
                        }

                        // Optionally save to Firestore or Realtime DB
                        Toast.makeText(LoginActivity.this, "Logged in as " + guestUsername, Toast.LENGTH_SHORT).show();

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String, Object> guestData = new HashMap<>();
                        guestData.put("username", guestUsername);
                        guestData.put("guest", true);

                        db.collection("users").document(user.getUid())
                                .set(guestData)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("FIREBASE", "Guest username saved.");
                                })
                                .addOnFailureListener(e -> {
                                    Log.w("FIREBASE", "Error saving guest username", e);
                                });

                        if (user != null) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", guestUsername); // optional
                            startActivity(intent);
                            finish(); // Close login screen

                            // Navigate to main/home screen
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();

                        } else {
                            Toast.makeText(LoginActivity.this, "Guest login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void loginUser() {

        String email = ((TextInputEditText) findViewById(R.id.loginEmail)).getText().toString().trim();
        String password = ((TextInputEditText) findViewById(R.id.loginPassword)).getText().toString().trim();

        // Check if email and password are not empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Enter all the blank fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Authenticate user with Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();

                        // Navigate to main/home screen
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();

                    } else {
                        Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public boolean isValidUsername(String username) {
        if(username == null) return false;

        if(username.length() < 3 || username.length() > 10) return false;
        return username.matches("[a-zA-Z0-9_]+");
    }
}