package com.example.ecoexplorer.ui.explore;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.ui.challenge.ChallengeFragment;

public class WebARFragmentSimulation extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private WebView webView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explorer_arscene_layout, container, false);

        webView = view.findViewById(R.id.webarWebView);

        // Request Camera Permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            loadWebAR();
        }

        ImageView backButton = view.findViewById(R.id.back_to_explore);
        backButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(WebARFragmentSimulation.this);
            navController.navigate(R.id.action_ARexplore_to_navigation_arexplore);
        });

        ImageView guideButton = view.findViewById(R.id.guide);
        guideButton.setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("AR Guide")
                    .setMessage("Please find the Image downloaded, then point the camera to that image.")
                    .setNegativeButton("Close", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        return view;
    }

    private void loadWebAR() {
        // Check camera permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, 1);
        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Ensure links stay inside WebView
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        // Needed for camera & WebRTC in WebView
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                requireActivity().runOnUiThread(() ->
                        request.grant(request.getResources())
                );
            }
        });
        WebView.setWebContentsDebuggingEnabled(true);

        // Load your 8thWall project
        webView.loadUrl("https://ecoexplorer.8thwall.app/exoexplorer/");
    }
}
