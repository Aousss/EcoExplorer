package com.example.ecoexplorer.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.ui.explore.WebARFragmentLearn;

public class Feedback extends Fragment {

    private WebView formWebView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.profile_feedback, container, false);

        formWebView = view.findViewById(R.id.feedback_form);
        loadFeedbackForm();

        ImageView backButton = view.findViewById(R.id.back_to_profile);
        backButton.setOnClickListener(v -> {
            NavController navController = NavHostFragment.findNavController(Feedback.this);
            navController.navigate(R.id.action_feedback_to_navigation_profile);
        });

        return view;
    }

    private void loadFeedbackForm() {
        WebSettings webSettings = formWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Ensure links stay inside WebView
        formWebView.setWebViewClient(new WebViewClient());
        formWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);

        // Needed for camera & WebRTC in WebView
        formWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                requireActivity().runOnUiThread(() ->
                        request.grant(request.getResources())
                );
            }
        });
        WebView.setWebContentsDebuggingEnabled(true);

        // Load your 8thWall project
        formWebView.loadUrl("https://docs.google.com/forms/d/e/1FAIpQLSeEp7wElg_GSNcLCfLZoIlJdkqP5cZeFoxBOHbV_dOWvsw6Kw/viewform?usp=dialog");
    }
}