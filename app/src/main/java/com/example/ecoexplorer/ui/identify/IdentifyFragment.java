package com.example.ecoexplorer.ui.identify;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.databinding.FragmentIdentifyBinding;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

public class IdentifyFragment extends Fragment {

    private FragmentIdentifyBinding binding;

    private ImageView imageView;
    private TextView textResult;

    private List<String> labels;
    private Uri imageUri;
    private Interpreter tflite;

    private final int IMAGE_SIZE = 32;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = FragmentIdentifyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        /* NAVIGATE OPEN THE CAMERA */
//        binding.identifyNow.setOnClickListener(v -> {
//            NavController navController = Navigation.findNavController(v);
//            navController.navigate(R.id.action_navigation_identify_to_navigation_camera_identify);
//        });

        Button btnGallery = root.findViewById(R.id.btnGallery);
        Button btnCamera = root.findViewById(R.id.btnCamera);
        imageView = root.findViewById(R.id.imageViewIdentify);
        textResult = root.findViewById(R.id.textResult);

//        // Load tflite model
        try {
            tflite = new Interpreter(loadModelFile(requireContext(), "models/insects.tflite"));
            Toast.makeText(getContext(), "Model SUCCESSFULLY loaded.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Model FAILED to load.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        // Gallery Picker
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        processImageUri(imageUri);
                    }
                }
        );

        // Camera Capture
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        processImageUri(imageUri);
                    }
                }
        );

        btnGallery.setOnClickListener(v -> openGallery());
        btnCamera.setOnClickListener(v -> openCamera());

        return root;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        cameraLauncher.launch(intent);
    }

    @SuppressLint("Unknown")
    private void processImageUri(@NonNull Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);

            // 1) Ask the model what it wants
            int inputIndex = 0;
            int[] inputShape = tflite.getInputTensor(inputIndex).shape();   // e.g. [1, 224, 224, 3]
            DataType inputType = tflite.getInputTensor(inputIndex).dataType(); // FLOAT32 or UINT8

            int height = inputShape[1];
            int width  = inputShape[2];

            // 2) Build a TensorImage with the *model’s* input type
            TensorImage tensorImage = new TensorImage(inputType);

            // 3) Build an image processor:
            //    - Always resize to [H, W]
            //    - Only normalize if the model expects FLOAT32
            org.tensorflow.lite.support.image.ImageProcessor.Builder procBuilder =
                    new org.tensorflow.lite.support.image.ImageProcessor.Builder()
                            .add(new ResizeOp(height, width, ResizeOp.ResizeMethod.BILINEAR));

            if (inputType == DataType.FLOAT32) {
                // Change these if your notebook used different mean/std.
                // Common patterns:
                //   - [0,1] scaling: mean=0.0f, std=1/255.0f
                //   - ImageNet style: mean=127.5f, std=127.5f
                procBuilder.add(new NormalizeOp(0.0f, 1.0f / 255.0f));
            }
            org.tensorflow.lite.support.image.ImageProcessor imageProcessor = procBuilder.build();

            tensorImage.load(bitmap);
            tensorImage = imageProcessor.process(tensorImage);

            // 4) Prepare output buffer based on model’s output tensor
            int outputIndex = 0;
            int[] outputShape = tflite.getOutputTensor(outputIndex).shape();   // e.g. [1, numClasses]
            DataType outputType = tflite.getOutputTensor(outputIndex).dataType();

            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType);

            // 5) Run inference
            tflite.run(tensorImage.getBuffer(), outputBuffer.getBuffer().rewind());

            // 6) Read results (handle both FLOAT32 and UINT8)
            float[] probs;
            if (outputType == DataType.FLOAT32) {
                probs = outputBuffer.getFloatArray();
            } else {
                // UINT8 -> convert to float probs in [0..1]
                int[] ints = outputBuffer.getIntArray(); // returns 0..255 for UINT8
                probs = new float[ints.length];
                for (int i = 0; i < ints.length; i++) probs[i] = ints[i] / 255.0f;
            }

            // 7) Argmax
            int bestIdx = 0;
            float bestScore = -1f;
            for (int i = 0; i < probs.length; i++) {
                if (probs[i] > bestScore) { bestScore = probs[i]; bestIdx = i; }
            }

            // Option A: If you know your classes manually
            String[] classes = {
                    "butterflies",
                    "cicadas",
                    "damselflies",
                    "dragonflies",
                    "leafhopper",
                    "longhorn beetle",
                    "moths",
                    "rhinoceros beetle",
                    "weevil"
            };

//            String[] classes = {
//                    "apple",
//                    "banana",
//                    "orange"
//            };

            String detected = (bestIdx < classes.length) ? classes[bestIdx] : "Class " + bestIdx;

//            float THRESHOLD = 0.1f; // 60%
//            if (bestScore<THRESHOLD) {
//                textResult.setText("Detected: UNKNOWN");
//                return;
//            }

            // (Optional) Map to label names if you have labels.txt in assets
//            String label = (labels != null && bestIdx < labels.size()) ? labels.get(bestIdx) : ("Class " + bestIdx);

            textResult.setText("Detected: " + detected + " (score: " + String.format("%.3f", bestScore) + ")");

        } catch (Exception e) {
            e.printStackTrace();
            textResult.setText("Error: " + e.getMessage());
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        FileInputStream inputStream = new FileInputStream(context.getAssets().openFd(modelFileName).getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelFileName).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelFileName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void loadLabelsFromAssets(String filename) {
        labels = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(requireContext().getAssets().open(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                labels.add(line.trim());
            }
        } catch (Exception e) {
            e.printStackTrace();
            labels = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}