package com.example.ecoexplorer.ui.identify;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.databinding.FragmentCameraIdentifyBinding;
import com.example.ecoexplorer.databinding.FragmentIdentifyBinding;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraIdentifyFragment extends Fragment {

    private FragmentCameraIdentifyBinding binding;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    private static final String TAG = "IdentifyFragment";
    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{android.Manifest.permission.CAMERA};

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentCameraIdentifyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the camera executor.
        cameraExecutor = Executors.newSingleThreadExecutor();

        // Setup for the captureCloseButton.
        binding.captureCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(v).popBackStack();
                Log.d(TAG, "Capture Close Button Clicked!");
            }
        });

        // Setup for the captureButton.
        binding.captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Capture Button Clicked!");
                takePhoto();
            }
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (allPermissionsGranted()) {
            startCamera();
        } else {
            requestPermissions();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Preview use case
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

                // Image capture
                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                        .build();

                // Select back camera as a default
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                try {

                    // Unbind use cases before rebinding
                    cameraProvider.unbindAll();

                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);

                    binding.previewView.setVisibility(View.VISIBLE);

                } catch(Exception exc) {
                    Log.e(TAG, "Use case binding failed", exc);
                    Toast.makeText(requireContext(), "Failed to start camera: "+ exc.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting camera provider", e);
            }
        }, ContextCompat.getMainExecutor(requireContext()));

    }

    private void takePhoto() {
        if(imageCapture == null) {
            Log.e(TAG, "Image capture is null, cannot take a photo.");
            Toast.makeText(requireContext(), "Camera not ready!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Take the picture
        imageCapture.takePicture(
                ContextCompat.getMainExecutor(requireContext()),
                new ImageCapture.OnImageCapturedCallback() {

                    @Override
                    public void onCaptureSuccess(@NonNull ImageProxy image) {
                        Log.d(TAG, "Image captured successfully!" + image.getFormat());

                        // Convert the image to a bitmap
                        Bitmap bitmap = imageProxyToBitmap(image);

                        if (bitmap != null) {
                            // Update the capturedImageView with the bitmap
                            binding.capturedImageView.setImageBitmap(bitmap);
                            binding.capturedImageView.setVisibility(View.VISIBLE);
                            Log.d(TAG, "Bitmap updated successfully!");
                        } else {
                            Log.e(TAG, "Failed to convert ImageProxy to Bitmap.");
                            Toast.makeText(requireContext(), "Failed to convert ImageProxy to Bitmap.", Toast.LENGTH_SHORT).show();
                        }

                        image.close();
                        // Important: Close the ImageProxy to release resources
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, "Image capture failed", exception);
                        Toast.makeText(requireContext(), "Failed to capture image: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private Bitmap imageProxyToBitmap(ImageProxy image) {
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, new BitmapFactory.Options());
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
        );
    }

    // Checks if all required permissions are granted
    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    // Handles the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera();
            } else {
                Toast.makeText(requireContext(),
                        "Permissions not granted by the user.",
                        Toast.LENGTH_SHORT).show();
                // Optionally, you could navigate back or disable camera features here
                Navigation.findNavController(requireView()).popBackStack();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clear the binding object to prevent memory leaks.
        binding = null;
    }
}