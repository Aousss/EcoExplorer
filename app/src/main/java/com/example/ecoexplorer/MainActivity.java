package com.example.ecoexplorer;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.ecoexplorer.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private BottomNavigationView bottomNavigationView;

    private final Map<Integer, Integer> graphMap = new HashMap<>() {{
        put(R.id.navigation_home, R.navigation.nav_home);
        put(R.id.navigation_arexplore, R.navigation.nav_explore);
        put(R.id.navigation_challenge, R.navigation.nav_challenge);
        put(R.id.navigation_profile, R.navigation.nav_profile);
        put(R.id.navigation_identify, R.navigation.nav_identify);
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        FirebaseApp.initializeApp(this);
        ActivityMainBinding binding;

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // REMOVE ACTION BAR
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Set white status bar background + dark icons
        Window window = getWindow();
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.green_primary));
        WindowCompat.setDecorFitsSystemWindows(window, false);

        WindowInsetsControllerCompat insetsController =
                new WindowInsetsControllerCompat(window, window.getDecorView());
        insetsController.setAppearanceLightStatusBars(true);  // dark icons on light background

        // Handle padding - layout follow system windows (status bar, nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, systemBars.top, 0,0);  // apply status bar & nav bar insets as padding
            return insets;
        });

        bottomNavigationView = binding.navView;

        // Remove the background (bugs remove)
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setBackground(null);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment_activity_main);
        navController = navHostFragment.getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);

        /*----------------------------------
         * BOTTOM MARGIN REMOVED
         * ----------------------------------*/
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            View fragmentView = findViewById(R.id.nav_host_fragment_activity_main);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fragmentView.getLayoutParams();

            if (destination.getId() == R.id.navigation_camera_identify) {
                // Remove bottom margin for this fragment
                params.bottomMargin = 30;
                binding.navView.setVisibility(View.GONE); // Hide bottom nav
                findViewById(R.id.fab_camera).setVisibility(View.GONE); // Hide FAB (optional)
            } else {
                // Restore bottom margin when other fragments are active
                params.bottomMargin = getResources().getDimensionPixelSize(R.dimen.fragment_bottom_margin);
                binding.navView.setVisibility(View.VISIBLE);
                findViewById(R.id.fab_camera).setVisibility(View.VISIBLE);
            }

            fragmentView.setLayoutParams(params);
        });

        /*----------------------------------
        * FLOATING BUTTON ACTION - NAVIGATE
        * ----------------------------------*/
        FloatingActionButton fab = findViewById(R.id.fab_camera);
        fab.setOnClickListener(v -> {
            binding.navView.setSelectedItemId(R.id.navigation_identify);
        });

        /*---------------------------
        * HIDE THE BOTTOM NAVIGATION
        * ---------------------------*/
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.navigation_camera_identify) {
                binding.navView.setVisibility(View.GONE);
                findViewById(R.id.fab_camera).setVisibility(View.GONE); // Optional: hide FAB too
            } else {
                binding.navView.setVisibility(View.VISIBLE);
                findViewById(R.id.fab_camera).setVisibility(View.VISIBLE);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Integer graphId = graphMap.get(item.getItemId());
            if (graphId != null) {
                navController.setGraph(graphId);  // automatically loads startDestination
                return true;
            }
            return false;
        });

        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    public void switchToTab(int tabID) {
        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setSelectedItemId(tabID);
    }
}