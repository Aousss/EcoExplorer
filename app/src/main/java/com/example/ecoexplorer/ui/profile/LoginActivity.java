package com.example.ecoexplorer.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.ecoexplorer.MainActivity;
import com.example.ecoexplorer.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

        // Google Sign-In
        loginWithGoogle = findViewById(R.id.login_with_Google);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Found in google-services.json
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        loginWithGoogle.setOnClickListener(v -> signInWithGoogle());

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

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            try {
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data)
                        .getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String uid = user.getUid();

                            DatabaseReference ref = FirebaseDatabase.getInstance()
                                    .getReference("users")
                                    .child(uid);

                            // Check if username exists
                            ref.child("username").get().addOnSuccessListener(snapshot -> {
                                if (snapshot.exists()) {
                                    // Save locally
                                    SharedPreferences prefs = getSharedPreferences("EcoPrefs", MODE_PRIVATE);
                                    prefs.edit().putString("username", snapshot.getValue(String.class)).apply();

                                    // Go to main
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }
//                                else {
//                                    // No username → go to username setup
//                                    Intent intent = new Intent(LoginActivity.this, SetUsernameActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
                            });
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed", Toast.LENGTH_SHORT).show();
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