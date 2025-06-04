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

        loginForgotPassword = findViewById(R.id.forgotPassword);
        loginBtn = findViewById(R.id.btn_login);
        registerBtn = findViewById(R.id.goto_register);

        // REMOVE THE TOP ACTION BAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        loginForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        // Login Button Logic
        loginBtn.setOnClickListener(v -> {
            String email = ((TextInputEditText) findViewById(R.id.loginEmail)).getText().toString().trim();
            String password = ((TextInputEditText) findViewById(R.id.loginPassword)).getText().toString().trim();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                            // TODO: Navigate to main/home screen
                        } else {
                            Toast.makeText(this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        loginAsGuest = findViewById(R.id.login_as_guest);
        loginAsGuest.setOnClickListener(v -> {
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

                                // TODO: Proceed to home/main screen with guestUsername
                            } else {
                                Toast.makeText(LoginActivity.this, "Guest login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        registerBtn.setOnClickListener(v2 -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    public boolean isValidUsername(String username) {
        if(username == null) return false;

        if(username.length() < 3 || username.length() > 10) return false;
        return username.matches("[a-zA-Z0-9_]+");
    }
}