package com.example.ecoexplorer.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ecoexplorer.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    ViewFlipper viewFlipper;
    Button saveButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        viewFlipper = findViewById(R.id.viewFlipper_forgot_password);

        /* Navigate between layout */
        findViewById(R.id.btn_send_code).setOnClickListener(v -> viewFlipper.showNext());
        findViewById(R.id.back_previous_email).setOnClickListener(v -> viewFlipper.showPrevious());

        findViewById(R.id.btn_verify_code).setOnClickListener(v -> viewFlipper.showNext());
        findViewById(R.id.back_previous_code).setOnClickListener(v -> viewFlipper.showPrevious());

        findViewById(R.id.btn_save_newPassword).setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            Toast toast = Toast.makeText(ForgotPasswordActivity.this, "Password have changed!", Toast.LENGTH_SHORT);
            toast.show();
        });

        findViewById(R.id.back_previous_login).setOnClickListener(v -> {
            Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
