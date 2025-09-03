package com.example.ecoexplorer.ui.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.ecoexplorer.R;

public class FunFactsDetailFragment extends Fragment {

    private TextView factDetail;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fun_fact_details, container, false);

        TextView factText = view.findViewById(R.id.factDetailText);
        TextView factDescription = view.findViewById(R.id.factDetailDescription);
        ImageView factImage = view.findViewById(R.id.factDetailImage);
        TextView factSource = view.findViewById(R.id.factDetailSource);

        if (getArguments() != null) {
            String text = getArguments().getString("fact_text");
            String description = getArguments().getString("fact_description");
            String imageUrl = getArguments().getString("fact_image");
            String source = getArguments().getString("fact_source");

            factText.setText(text);
            factDescription.setText(description);
            factSource.setText(source);

            if (imageUrl != null && !imageUrl.isEmpty()) {
                Glide.with(this).load(imageUrl).into(factImage);
            }
        }

        return view;
    }
}