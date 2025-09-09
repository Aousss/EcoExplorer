package com.example.ecoexplorer.ui.profile;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoexplorer.MainActivity;
import com.example.ecoexplorer.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 100;

    private TextInputEditText email;
    private TextInputEditText password;
    private TextView loginForgotPassword;
    private Button loginBtn;
    private TextView registerBtn;
    private LinearLayout loginAsGuest;
    private ImageView loginWithGoogle;

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

        /*---------------
         * LOGIN BUTTON
         * ------------- */
        loginBtn = findViewById(R.id.btn_login);
        loginBtn.setOnClickListener(v -> loginUser());

        /*---------------
         * FORGOT BUTTON
         * ---------------*/
        loginForgotPassword = findViewById(R.id.forgotPassword);
        loginForgotPassword.setOnClickListener(view -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        /*----------------
         * REGISTER BUTTON
         * ----------------*/
        registerBtn = findViewById(R.id.goto_register);
        registerBtn.setOnClickListener(v2 -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        /*------------------
         * LOGIN AS GUEST
         * -----------------*/
        loginAsGuest = findViewById(R.id.login_as_guest);
        loginAsGuest.setOnClickListener(v3 -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RC_SIGN_IN) {
//            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
//            try {
//                GoogleSignInAccount account = task.getResult();
//                if (account != null) {
//                    firebaseAuthWithGoogle(account);
//                }
//            } catch (Exception e) {
//                Toast.makeText(this, "Google Sign-In failed", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
//        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
//        mAuth.signInWithCredential(credential)
//                .addOnCompleteListener(this, task -> {
//                    if (task.isSuccessful()) {
//                        FirebaseUser user = mAuth.getCurrentUser();
//                        Toast.makeText(this, "Sign-In successful: " + (user != null ? user.getEmail() : ""), Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                        startActivity(intent);
//                        finish();
//                    } else {
//                        Toast.makeText(this, "Authentication failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                });
//    }

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
                                    Log.e(TAG, "Database error", databaseError.toException());
                                }
                            });

                            // Update password in Realtime Database
                            ref.child("password").setValue(password)
                                    .addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {
                                            Log.d(TAG, "Password updated in database");
                                        } else {
                                            Log.e(TAG, "Error updating password in database", updateTask.getException());
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