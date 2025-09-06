package com.example.ecoexplorer.ui.identify;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ecoexplorer.R;

public class IdentifyResults extends Fragment {

    private ImageView imageFound;
    private ImageView imageOriginal;
    private TextView speciesName;
    private TextView speciesSciName;

    private TextView errorIdentifyMessage;
    private TextView successIdentifyMessage;

    private Button scanIdentifyAgain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_identify_results, container, false);

        imageFound = view.findViewById(R.id.foundImage);
        imageOriginal = view.findViewById(R.id.originalImage);
        speciesName = view.findViewById(R.id.species_nonSciName);
        speciesSciName = view.findViewById(R.id.species_sciName);

        errorIdentifyMessage = view.findViewById(R.id.error_identify);
        successIdentifyMessage = view.findViewById(R.id.success_identify);

        scanIdentifyAgain = view.findViewById(R.id.scanIdentify_again);
        scanIdentifyAgain.setOnClickListener(v -> identifyAgain());

        // Show Results
        showIdentifyResult();

        ImageView backToIdentify = view.findViewById(R.id.back_to_identify);
        backToIdentify.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(IdentifyResults.this);
            navController.navigate(R.id.action_identify_result_to_navigation_identify);
        });

        return view;
    }

    private void showIdentifyResult() {

        if (getArguments() !=null) {
            String foundImage = getArguments().getString("image_uri");
            String originalImage = getArguments().getString("originalImage");
            String speciesNameText = getArguments().getString("detected_name");
            String scoreStr = getArguments().getString("detected_score");
            String speciesSciNameText = getArguments().getString("speciesSciName");

            errorIdentifyMessage.setVisibility(View.GONE);
            successIdentifyMessage.setVisibility(View.VISIBLE);

            float THRESHOLD = 0.5f;
            float score = 0f;
            try {
                score = Float.parseFloat(scoreStr); // convert string to float
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }

            if (score < THRESHOLD) {
                // Show error message
                errorIdentifyMessage.setVisibility(View.VISIBLE);
                successIdentifyMessage.setVisibility(View.GONE);

                speciesName.setVisibility(View.GONE);
                speciesSciName.setVisibility(View.GONE);

                if (foundImage != null) {
                    imageFound.setImageURI(Uri.parse(foundImage));
                }
                return;
            }

            // Success case
            errorIdentifyMessage.setVisibility(View.GONE);
            successIdentifyMessage.setVisibility(View.VISIBLE);

            speciesName.setText(speciesNameText);
            if (foundImage != null) {
                imageFound.setImageURI(Uri.parse(foundImage));
            }
        } else {
            String foundImage = getArguments().getString("image_uri");
            imageFound.setImageURI(Uri.parse(foundImage));

            errorIdentifyMessage.setVisibility(View.VISIBLE);
            successIdentifyMessage.setVisibility(View.GONE);
        }

    }

    private void identifyAgain() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Identify Again")
                .setItems(new CharSequence[]{"Camera", "Gallery"}, (dialog, which) -> {
                    NavController navController = NavHostFragment.findNavController(IdentifyResults.this);

                    Bundle bundle = new Bundle();
                    if (which == 0) {
                        // Camera selected
                        bundle.putString("rescan_option", "camera");
                    } else {
                        // Gallery selected
                        bundle.putString("rescan_option", "gallery");
                    }
                    navController.navigate(R.id.navigation_identify, bundle);
                })
                .show();
    }

    /* TO BE ADDED */
    // add the review button for each detection to check the accuracy of the detection.
    // the data (images, output, input:actual_species)
}