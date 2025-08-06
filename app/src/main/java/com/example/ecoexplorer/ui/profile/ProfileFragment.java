package com.example.ecoexplorer.ui.profile;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

// Import NavController
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.UserViewModel;
import com.example.ecoexplorer.databinding.FragmentHomeBinding;
import com.example.ecoexplorer.databinding.FragmentProfileBinding;
import com.example.ecoexplorer.ui.home.UserUtils;
import com.google.firebase.auth.FirebaseAuth;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private UserViewModel userViewModel;
    private FirebaseAuth mAuth;

    ConstraintLayout noUserAccount, LoginedUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* GET THE USERNAME & DISPLAY */
        binding.username.setText(UserUtils.getCachedUsername(requireContext()));
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserUtils.loadUsername(requireContext(), userViewModel); // Pass ViewModel!

        userViewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            binding.username.setText("@"+username);
        });

        /* LOGOUT FUNCTIONS */
        mAuth = FirebaseAuth.getInstance();
        Button logout = binding.btnLogout;
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

        noUserAccount = view.findViewById(R.id.noUserAccount);
        LoginedUser = view.findViewById(R.id.LoginedUser);

        // Check if the user is logged in
        if (mAuth.getCurrentUser() !=null) {
            noUserAccount.setVisibility(View.GONE);
            LoginedUser.setVisibility(View.VISIBLE);
        } else {
            noUserAccount.setVisibility(View.VISIBLE);
            LoginedUser.setVisibility(View.GONE);
        }

        Button openLogin;
        openLogin = view.findViewById(R.id.btn_to_login);

        // Navigate to LoginActivity when the button is clicked
        openLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}