package com.example.ecoexplorer.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.UserInfoActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class RegisterActivity extends AppCompatActivity {

    FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private TextInputEditText email;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;
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

        registerBtn = findViewById(R.id.btn_register);
        loginBtn = findViewById(R.id.back_to_login);

        loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        registerBtn.setOnClickListener( view -> {
            String email = ((TextInputEditText) findViewById(R.id.register_email)).getText().toString().trim();
            String password = ((TextInputEditText) findViewById(R.id.register_password)).getText().toString().trim();
            String confirmPassword = ((TextInputEditText) findViewById(R.id.register_confirm_password)).getText().toString().trim();

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                            Toast.makeText(RegisterActivity.this, "Register successful", Toast.LENGTH_SHORT).show();

                            if (firebaseUser != null) {
                                // Immediately navigate to UserInfoActivity to fill in details
                                Intent intent = new Intent(RegisterActivity.this, UserInfoActivity.class);
                                intent.putExtra("uid", firebaseUser.getUid()); // pass UID if needed
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Toast.makeText(RegisterActivity.this, "Register failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        });

    }
}
