package com.example.ecoexplorer.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ecoexplorer.MainActivity;
import com.example.ecoexplorer.R;
import com.example.ecoexplorer.UserViewModel;
import com.example.ecoexplorer.databinding.HomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private HomeBinding binding;
    private UserViewModel userViewModel;
    LinearLayout noAccount, loginAccount;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener authListener;

    private TextView funFacts;
    private List<String> funFactsList = new ArrayList<>();
    private List<FunFacts> funFactsObjects = new ArrayList<>();
    private Random random = new Random();

    private Button learntMore;
    private FunFacts currentFact; // store the fact currently shown

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = HomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /*------------------------
        NAVIGATE TO IDENTIFY PAGE
        -------------------------*/
        binding.identifyCard.setOnClickListener(view -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.navigation_identify); // or navigation_challenge etc.
            }
        });

        /*------------------
        NAVIGATE TO AR PAGE
        --------------------*/
        binding.exploreCard.setOnClickListener(view -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.navigation_arexplore); // or navigation_challenge etc.
            }
        });

        /*------------------
        NAVIGATE TO CHALLENGE PAGE
        --------------------*/
        binding.challengeCard.setOnClickListener(view -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).switchToTab(R.id.navigation_challenge); // or navigation_challenge etc.
            }
        });

        // Set cached username immediately
        binding.username.setText(UserUtils.getCachedUsername(requireContext()));

        // Then load from Firebase in background
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        UserUtils.loadUsername(requireContext(), userViewModel);

        userViewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            binding.username.setText(username);
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        funFacts = view.findViewById(R.id.content_funfacts);
        learntMore = view.findViewById(R.id.btn_ff_learn_more);
        learntMoreButton();

        loadFunFacts();

        // Initialize the layouts
        noAccount = view.findViewById(R.id.noAccountGreet);
        loginAccount = view.findViewById(R.id.registeredAccountGreet);

        authListener = firebaseAuth -> {
            // Check if the user is logged in
            if (mAuth.getCurrentUser() !=null) {
                noAccount.setVisibility(View.GONE);
                loginAccount.setVisibility(View.VISIBLE);
            } else {
                noAccount.setVisibility(View.VISIBLE);
                loginAccount.setVisibility(View.GONE);
            }
        };

        mAuth.addAuthStateListener(authListener);

        // Initialize the connection status
        updateConnectionStatus();
    }

    /* -------------------------------------------
    CHECK IF DEVICE IS CONNECTED TO THE INTERNET
    --------------------------------------------- */
    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            return capabilities != null &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    /* --------------------------------------------
    UPDATE THE STATUS OF THE CONNECTION INDICATOR
    --------------------------------------------- */
    private void updateConnectionStatus() {
        if (isConnected(requireContext())) {
            binding.connectionIndicator.setText("online");
            binding.connectionIndicator.setBackgroundResource(R.drawable.el_status_indicator_online);
        } else {
            binding.connectionIndicator.setText("offline");
            binding.connectionIndicator.setBackgroundResource(R.drawable.el_status_indicator_offline);
        }
    }

    /* ------------------------------------------------------
    TO CREATE A REAL-TIME INDICATOR OF THE CONNECTION STATUS
    -------------------------------------------------------- */
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
        if (authListener != null && mAuth != null) {
            mAuth.removeAuthStateListener(authListener);
        }
        binding = null;
    }

    private void loadFunFacts() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("funfacts");
        databaseRef.keepSynced(true);
        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                funFactsObjects.clear();

                for (DataSnapshot factSnapshot : snapshot.getChildren()) {
                    FunFacts fact = factSnapshot.getValue(FunFacts.class);
                    if (fact != null) {
                        funFactsObjects.add(fact); // store full object
                    }
                }

                Log.d("FunFacts", "Fun facts size: " + funFactsObjects.size());
                displayFunfacts();
            }

            private void displayFunfacts() {
                if (!funFactsObjects.isEmpty()) {
                    currentFact = funFactsObjects.get(random.nextInt(funFactsObjects.size())); // pick a random object
                    funFacts.setText(currentFact.text); // show text
                } else {
                    funFacts.setText("No fun facts found.");
                    currentFact = null;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                funFacts.setText("Failed to load fun facts.");
            }
        });
    }

    private void learntMoreButton() {
        learntMore.setOnClickListener(v -> {
            if (currentFact != null) {
                Bundle bundle = new Bundle();
                bundle.putString("fact_text", currentFact.text);
                bundle.putString("fact_description", currentFact.description);
                bundle.putString("fact_image", currentFact.imageUrl);
                bundle.putString("fact_source", currentFact.source);

                NavController navController = Navigation.findNavController(v);
                navController.navigate(R.id.action_navigation_home_to_fun_facts_detail, bundle);
            }
        });
    }
}