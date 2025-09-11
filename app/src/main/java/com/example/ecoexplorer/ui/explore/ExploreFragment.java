package com.example.ecoexplorer.ui.explore;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DownloadManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.net.Uri;
import android.os.Environment;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.databinding.ExploreBinding;

public class ExploreFragment extends Fragment {

    private Button viewAR1, viewARlearn;
    private ExploreBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = ExploreBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        viewAR1 = view.findViewById(R.id.btn_ar_watercycle);
        viewAR1.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_arexplore_to_ARexplore);
        });

        viewARlearn = view.findViewById(R.id.btn_arLearn);
        viewARlearn.setOnClickListener(v -> {

            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.action_navigation_arexplore_to_ARexplore2);
        });

        Button downloadAssets = view.findViewById(R.id.btnDownload);
        downloadAssets.setOnClickListener(v -> {
            String url = "https://drive.google.com/file/d/18MoffeCKcQU4FO8jeEJWue9o4m_TipO_/view?usp=sharing";
            downloadFile(url, "image.pdf");
        });

        return view;
    }

    private void downloadFile(String url, String fileName) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle("Downloading " + fileName);
        request.setDescription("Please wait...");
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

        Toast.makeText(requireContext(), "Downloading...", Toast.LENGTH_SHORT).show();

        DownloadManager manager = (DownloadManager) requireContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }
}