package com.example.ecoexplorer.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoexplorer.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Random;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button sendCodeButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        sendCodeButton = findViewById(R.id.btn_send_code);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.back_previous_login).setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });

        setupSendResetEmail();
    }

    private void setupSendResetEmail() {
        sendCodeButton.setOnClickListener(v -> {
            String inputEmail = ((TextInputEditText) findViewById(R.id.register_email))
                    .getText().toString().trim();

            if (inputEmail.isEmpty()) {
                Toast.makeText(ForgotPasswordActivity.this,
                        "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(inputEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // ✅ Firebase actually sends the reset email
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Password reset email sent to " + inputEmail,
                                    Toast.LENGTH_LONG).show();

                            LinearLayout resetPassword = findViewById(R.id.fp_insert_email);
                            resetPassword.setVisibility(View.GONE);

                            LinearLayout verifyCode = findViewById(R.id.fp_check_email);
                            verifyCode.setVisibility(View.VISIBLE);

                            Button closeButton = findViewById(R.id.btn_close);
                            closeButton.setOnClickListener(v1 -> {
                                Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                            });

                        } else {
                            String errorMsg = task.getException() != null
                                    ? task.getException().getMessage()
                                    : "Unknown error";
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "Error: " + errorMsg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
