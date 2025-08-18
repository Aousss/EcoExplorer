package com.example.ecoexplorer.ui.explore;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.databinding.FragmentExploreBinding;
import com.example.ecoexplorer.databinding.FragmentHomeBinding;
import com.example.ecoexplorer.ui.profile.ProfileFragment;

import java.util.Arrays;
import java.util.List;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;

    private Button viewAR1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewAR1 = view.findViewById(R.id.ar1_button);
        viewAR1.setOnClickListener(view1 -> {
            NavController navController = NavHostFragment.findNavController(ExploreFragment.this);
            navController.navigate(R.id.action_navigation_profile_to_devInfo);
        });

    }
}