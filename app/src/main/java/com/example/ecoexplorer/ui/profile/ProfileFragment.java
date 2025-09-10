package com.example.ecoexplorer.ui.profile;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

// Import NavController
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.ecoexplorer.R;
import com.example.ecoexplorer.UserViewModel;
import com.example.ecoexplorer.databinding.HomeBinding;
import com.example.ecoexplorer.databinding.ProfileBinding;
import com.example.ecoexplorer.ui.home.UserUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ProfileBinding binding;
    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;
    private Uri imageUri;
    private ShapeableImageView profileImage;
    private Button uploadProfilePic;

    TextView usernameText, fullnameText, ageText, emailText, passwordText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = ProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* GET THE USERNAME & DISPLAY */
        binding.username.setText(UserUtils.getCachedUsername(requireContext()));
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserUtils.loadUsername(requireContext(), userViewModel); // Pass ViewModel!

        /* LOGOUT FUNCTIONS */
        mAuth = FirebaseAuth.getInstance();
        LinearLayout logout = binding.btnLogout;
        logout.setOnClickListener(v->{
            mAuth.signOut();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            // finish MainActivity to prevent going back
            requireActivity().finish();
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usernameText = view.findViewById(R.id.profile_username_text);
        fullnameText = view.findViewById(R.id.profile_fullname_text);
        ageText = view.findViewById(R.id.profile_age_text);
        emailText = view.findViewById(R.id.profile_email_text);
//        passwordText = view.findViewById(R.id.profile_password_text);

        profileImage = view.findViewById(R.id.profile_pic);
        uploadProfilePic = view.findViewById(R.id.upload_profile_pic);

        CardView cardViewLogin = view.findViewById(R.id.cardView_to_login);
        CardView profileInfoContainer = view.findViewById(R.id.user_profile_info_container);
        LinearLayout logoutLayout = view.findViewById(R.id.btn_logout);

        /* CHECKING USER ACCOUNT */
        // If login user
        if (mAuth.getCurrentUser() !=null) {
            // Set the cardView to login gone
            cardViewLogin.setVisibility(View.GONE);

            // Set the logout button to visible
            logoutLayout.setVisibility(View.VISIBLE);

            uploadProfilePic.setOnClickListener( v -> openFileChooser());
            userViewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
                binding.username.setText("@"+username);
            });
            loadUserInfo();

        } else {
            // if guests
            // Set the cardView to login visible
            cardViewLogin.setVisibility(View.VISIBLE);

            // Set the logout button to gone
            logoutLayout.setVisibility(View.GONE);

            // Set the profile info container to gone
            profileInfoContainer.setVisibility(View.GONE);

            userViewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
                binding.username.setText("Guest");
            });
        }

        TextView openLogin = view.findViewById(R.id.btn_login);
        openLogin.setOnClickListener(view1 ->  {
            NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
            navController.navigate(R.id.action_navigation_profile_to_login);
        });

        ImageView arrowHelpAndFeedback = view.findViewById(R.id.arrowHelpFeedback);
        LinearLayout helpAndFeedback = view.findViewById(R.id.help_and_feedback_container);
        LinearLayout expandHelpAndFeedback = view.findViewById(R.id.expand_help_and_feedback);
        helpAndFeedback.setOnClickListener(v-> {
            if (expandHelpAndFeedback.getVisibility() == View.GONE) {
                expandHelpAndFeedback.setVisibility(View.VISIBLE);
                arrowHelpAndFeedback.setRotation(90);
            } else {
                expandHelpAndFeedback.setVisibility(View.GONE);
                arrowHelpAndFeedback.setRotation(0);
            }
        });

        LinearLayout devInfo = view.findViewById(R.id.dev_info_container);
        devInfo.setOnClickListener(view1 -> {
            NavController navController = NavHostFragment.findNavController(ProfileFragment.this);
            navController.navigate(R.id.action_navigation_profile_to_devInfo);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri);
            uploadImageToFirebase();
        }
    }

    private void loadUserInfo() {
        String uid = mAuth.getCurrentUser().getUid();

        DatabaseReference usersInfo = FirebaseDatabase.getInstance().getReference("users").child(uid);
        usersInfo.keepSynced(true);
        usersInfo.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Extract values from database
                    String username = snapshot.child("username").getValue(String.class);
                    String fullname = snapshot.child("fullname").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    Long age = snapshot.child("age").getValue(Long.class);
//                    String password = snapshot.child("password").getValue(String.class);

                    usernameText.setText(username);
                    fullnameText.setText(fullname);
                    ageText.setText(String.valueOf(age));
                    emailText.setText(email);

//                    if (password != null) {
//                        passwordText.setText(password);
//                        passwordText.setTransformationMethod(new android.text.method.PasswordTransformationMethod());
//                    }

                    String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);
                    Glide.with(requireContext())
                            .load(profileImageUrl)
                            .placeholder(R.drawable.ic_person_profile) // default icon
                            .error(R.drawable.ic_close)       // fallback if failed
                            .into(profileImage);
                    profileImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserInfo", "Error loading user info: " + error.getMessage());
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void uploadImageToFirebase() {
        if (imageUri != null) {
            String uid = mAuth.getCurrentUser().getUid();
            StorageReference fileRef = FirebaseStorage.getInstance()
                    .getReference("profile_pictures")
                    .child(uid + ".jpg");

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save URL to Realtime Database
                        FirebaseDatabase.getInstance().getReference("users")
                                .child(uid)
                                .child("profileImageUrl")
                                .setValue(uri.toString());

                        Toast.makeText(getContext(), "Upload successful", Toast.LENGTH_SHORT).show();
                    }))
                    .addOnFailureListener(e ->
                            Toast.makeText(getContext(), "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }
}