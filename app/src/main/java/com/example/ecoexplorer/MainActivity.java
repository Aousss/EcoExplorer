package com.example.ecoexplorer;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.ecoexplorer.ui.home.HomeFragment;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.ecoexplorer.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private UserViewModel userViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        final BottomAppBar bottomAppBar = binding.bottomAppBar;

        // REMOVE ACTION BAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        navView.setBackground(null);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
                R.id.navigation_arexplore,
                R.id.navigation_camera_identify,
                R.id.navigation_challenge,
                R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);

        // to hide/show the navigation bar based on the current destination
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull androidx.navigation.NavDestination destination,
                                             @Nullable Bundle arguments) {
                if (destination.getId() == R.id.navigation_camera_identify) {
                    // If the current destination is IdentifyFragment, hide the BottomAppBar and FAB
                    bottomAppBar.setVisibility(View.GONE);
                    binding.fabCamera.setVisibility(View.GONE);
                } else {
                    // For other destinations, show the BottomAppBar and FAB
                    bottomAppBar.setVisibility(View.VISIBLE);
                    binding.fabCamera.setVisibility(View.VISIBLE);
                }
            }
        });

        FloatingActionButton fab = binding.fabCamera;
        // Set up click listener for the Floating Action Button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to the IdentifyFragment
                navController.navigate(R.id.navigation_camera_identify); // Use the ID you defined in mobile_navigation.xml
            }
        });


        // Initialize the shared ViewModel
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // Get username from intent
        String username = getIntent().getStringExtra("username");
        if (username != null) {
            userViewModel.setUsername(username);
        }

    }
}