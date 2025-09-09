package com.example.ecoexplorer.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.databinding.ExploreBinding;

public class ExploreFragment extends Fragment {

    private Button viewAR1;

    private ExploreBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = ExploreBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewAR1 = view.findViewById(R.id.ar1_button);
        viewAR1.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_arexplore_to_ARexplore);
        });

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}