package com.example.ecoexplorer.ui.explore;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ecoexplorer.databinding.FragmentExploreBinding;
import com.example.ecoexplorer.databinding.FragmentHomeBinding;

import java.util.Arrays;
import java.util.List;

public class ExploreFragment extends Fragment {

    private FragmentExploreBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

}