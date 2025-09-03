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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.ecoexplorer.R;

public class WebARFragment extends Fragment {

    private static final int CAMERA_PERMISSION_REQUEST = 101;
    private WebView webView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ar_scene_layout, container, false);

        webView = view.findViewById(R.id.webarWebView);

        // Request Camera Permission
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            loadWebAR();
        }

        return view;
    }

    private void loadWebAR() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Ensure links stay inside WebView
//        webView.setWebViewClient(new WebViewClient());

        // Needed for camera & WebRTC in WebView
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                requireActivity().runOnUiThread(() -> request.grant(request.getResources()));
            }
        });

        // Load your 8thWall project
        webView.loadUrl("https://8th.io/3fbrf");
    }
}
