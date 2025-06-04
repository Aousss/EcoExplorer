package com.example.ecoexplorer.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.UserViewModel;
import com.example.ecoexplorer.databinding.FragmentHomeBinding;
import com.example.ecoexplorer.ui.identify.IdentifyFragment;

import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* NAVIGATE TO IDENTIFY PAGE */
        binding.identifyCard.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_home_to_navigation_identify);
        });

        /* NAVIGATE TO AR PAGE */
        binding.exploreCard.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.navigation_home_to_navigation_arexplore);
        });

        /* GET THE USERNAME & DISPLAY */
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        userViewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            binding.username.setText(username);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize the connection status
        updateConnectionStatus();
    }

    /* CHECK IF DEVICE IS CONNECTED TO THE INTERNET */
    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    /* UPDATE THE STATUS OF THE CONNECTION INDICATOR */
    private void updateConnectionStatus() {
        if (isConnected(requireContext())) {
            binding.connectionIndicator.setText("online");
            binding.connectionIndicator.setBackgroundResource(R.drawable.el_status_indicator_online);
        } else {
            binding.connectionIndicator.setText("offline");
            binding.connectionIndicator.setBackgroundResource(R.drawable.el_status_indicator_offline);
        }
    }

    /* TO CREATE A REAL-TIME INDICATOR OF THE CONNECTION STATUS */
    private final BroadcastReceiver connectionReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (binding == null) return;

            if (isConnected(context)) {
                binding.connectionIndicator.setText("online");
                binding.connectionIndicator.setBackgroundResource(R.drawable.el_status_indicator_online);
            } else {
                binding.connectionIndicator.setText("offline");
                binding.connectionIndicator.setBackgroundResource(R.drawable.el_status_indicator_offline);
            }
        }
    };

    /* TO REGISTER AND UNREGISTER THE CONNECTION RECEIVER */
    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        requireContext().registerReceiver(connectionReceiver, filter);
    }

    /* TO REGISTER AND UNREGISTER THE CONNECTION RECEIVER */
    @Override
    public void onPause() {
        super.onPause();
        requireContext().unregisterReceiver(connectionReceiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}