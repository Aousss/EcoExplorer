package com.example.ecoexplorer.ui.identify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ecoexplorer.R;

public class DetailsFragment extends Fragment {

    private TextView speciesName, speciesDesc, speciesSciName, speciesEcosystem, speciesHabitat, speciesRole;
    private ImageView speciesImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.identify_details, container, false);

        speciesName = view.findViewById(R.id.identify_speciesName);
        speciesImage = view.findViewById(R.id.identify_speciesImage);
        speciesDesc = view.findViewById(R.id.identify_speciesDesc);
        speciesSciName = view.findViewById(R.id.species_sciName);
        speciesEcosystem = view.findViewById(R.id.species_ecosystem);
        speciesHabitat = view.findViewById(R.id.species_habitat);
        speciesRole = view.findViewById(R.id.species_role);


        if (getArguments() != null) {
            String name = getArguments().getString("name");
            int image = getArguments().getInt("image");
            String desc = getArguments().getString("desc");
            String sciName = getArguments().getString("sciName");
            String ecosystem = getArguments().getString("ecosystem");
            String habitat = getArguments().getString("habitat");
            String role = getArguments().getString("role");

            speciesName.setText(name);
            speciesImage.setImageResource(image);
            speciesDesc.setText(desc);
            speciesSciName.setText(sciName);
            speciesEcosystem.setText(ecosystem);
            speciesHabitat.setText(habitat);
            speciesRole.setText(role);

        }

        ImageView backToIdentify = view.findViewById(R.id.back_to_identify);
        backToIdentify.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(DetailsFragment.this);
            navController.popBackStack();
        });

        return view;
    }
}
