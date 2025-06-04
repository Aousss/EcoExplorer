package com.example.ecoexplorer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class UserInfoActivity extends AppCompatActivity {

    private Button saveButton;
    private TextInputEditText userName;
    private TextInputEditText userAge;
    private AutoCompleteTextView userRole;
    // Add more fields as needed

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AutoCompleteTextView roleDropdown = findViewById(R.id.user_role);

        String[] roles = new String[] {"Student", "Teacher", "Tutor", "Parent"};

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                roles
        );

        roleDropdown.setAdapter(adapter);

        saveButton = findViewById(R.id.btn_save);
        saveButton.setOnClickListener(v -> {
            String name = userName.getText().toString();
            String age = userAge.getText().toString();
            String role = userRole.getText().toString();
            // Add more fields as needed

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            if (firebaseUser != null) {
                String uid = firebaseUser.getUid();
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                Map<String, Object> userData = new HashMap<>();
                userData.put("name", name);
                userData.put("age", age);
                userData.put("role", role);
                // Add more fields

                db.collection("users").document(uid)
                        .set(userData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(UserInfoActivity.this, "Info saved successfully", Toast.LENGTH_SHORT).show();
                            // Redirect to main screen or dashboard
                            startActivity(new Intent(UserInfoActivity.this, MainActivity.class));
                            finish();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(UserInfoActivity.this, "Failed to save: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });


    }
}
