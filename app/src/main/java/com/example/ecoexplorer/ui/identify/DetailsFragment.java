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

    private TextView speciesName, speciesDesc;
    private ImageView speciesImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.identify_details, container, false);

        speciesName = view.findViewById(R.id.identify_speciesName);
        speciesImage = view.findViewById(R.id.identify_speciesImage);
        speciesDesc = view.findViewById(R.id.identify_speciesDesc);

        if (getArguments() != null) {
            String name = getArguments().getString("name");
            int image = getArguments().getInt("image");
            String desc = getArguments().getString("desc");

            speciesName.setText(name);
            speciesImage.setImageResource(image);
            speciesDesc.setText(desc);

        }

        ImageView backToIdentify = view.findViewById(R.id.back_to_identify);
        backToIdentify.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(DetailsFragment.this);
            navController.popBackStack();
        });

        return view;
    }
}
