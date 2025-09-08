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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;

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

    private List<String> labels;
    private Uri imageUri;
    private Interpreter tflite;

    private RecyclerView recyclerViewPlants, recyclerViewAnimals;
    private PlantsAdapter plantsAdapter;
    private List<Plants> plantList;

    private AnimalsAdapter animalsAdapter;
    private List<Animals> animalsList;

    private final int IMAGE_SIZE = 32;

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        View root = inflater.inflate(R.layout.identify, container, false);

        recyclerViewPlants = root.findViewById(R.id.recycleView_PlantsIdentify);
        plantsData();

        recyclerViewAnimals = root.findViewById(R.id.recycleView_AnimalIdentify);
        animalsData();

        Button btnGallery = root.findViewById(R.id.btnGallery);
        Button btnCamera = root.findViewById(R.id.btnCamera);

        // Load tflite model
        loadModel();
        labels = loadLabels(requireContext(), "mobilenet_label.txt");
        btnGallery.setOnClickListener(v -> openGallery());
        btnCamera.setOnClickListener(v -> openCamera());

        // Gallery Launcher
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        assert imageUri != null;
                        processImageUri(imageUri);
                    }
                }
        );

        // Camera Launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        processImageUri(imageUri);
                    }
                }
        );

        if (getArguments() != null) {
            String rescanOption = getArguments().getString("rescan_option");
            if ("camera".equals(rescanOption)) {
                openCamera();
            } else if ("gallery".equals(rescanOption)) {
                openGallery();
            }
        }

        return root;
    }

    private void plantsData() {
        recyclerViewPlants.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        // Plants Data
        plantList = new ArrayList<>();
        plantList.add(new Plants("Rose", R.drawable.rose));
        plantList.add(new Plants("Sunflower", R.drawable.sunflower));
        plantList.add(new Plants("Tulip", R.drawable.tulip));

        plantsAdapter = new PlantsAdapter(plantList);
        recyclerViewPlants.setAdapter(plantsAdapter);
    }

    private void animalsData() {
        recyclerViewAnimals.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        // Animals Data
        animalsList = new ArrayList<>();
        animalsList.add(new Animals("Butterfly","Butterfly descriptions", R.drawable.identify_butterfly));
        animalsList.add(new Animals("Dragonfly","Dragonfly descriptions", R.drawable.identify_dragonfly));
        animalsList.add(new Animals("Leafhopper","Leafhopper descriptions", R.drawable.identify_leafhopper));
        animalsList.add(new Animals("Moths","Moths descriptions", R.drawable.identify_moths));
        animalsList.add(new Animals("Weevil","Weevil descriptions", R.drawable.identify_weevil));
        animalsList.add(new Animals("Rhinoceros beetle","Rhizosphere beetle descriptions", R.drawable.identify_rhinoceros));
        animalsList.add(new Animals("Damselfly","Damselflies descriptions", R.drawable.identify_damselfly));
        animalsList.add(new Animals("Cicadas","Cicadas descriptions", R.drawable.identify_cicadas));
        animalsList.add(new Animals("Longhorn beetle","Longhorn beetle descriptions", R.drawable.identify_longhorn));

        animalsAdapter = new AnimalsAdapter(animalsList, animals -> {
           Bundle bundle = new Bundle();
           bundle.putString("name", animals.getName());
           bundle.putString("desc", animals.getDescription());
           bundle.putInt("image", animals.getImageResId());

            NavController navController = NavHostFragment.findNavController(IdentifyFragment.this);
            navController.navigate(R.id.action_navigation_identify_to_details, bundle);
        });

        recyclerViewAnimals.setAdapter(animalsAdapter);
    }

    private void loadModel() {
        try {
            tflite = new Interpreter(loadModelFile(requireContext(), "mobilenet.tflite"));
            Toast.makeText(getContext(), "Model SUCCESSFULLY loaded.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Model FAILED to load.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
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

            // 1) Ask the model what it wants
            int inputIndex = 0;
            int[] inputShape = tflite.getInputTensor(inputIndex).shape();   // e.g. [1, 224, 224, 3]
            DataType inputType = tflite.getInputTensor(inputIndex).dataType(); // FLOAT32 or UINT8

            int height = inputShape[1];
            int width  = inputShape[2];

            // 2) Build a TensorImage with the *model’s* input type
            TensorImage tensorImage = new TensorImage(inputType);

            // 3) Build an image processor:
            org.tensorflow.lite.support.image.ImageProcessor.Builder procBuilder =
                    new org.tensorflow.lite.support.image.ImageProcessor.Builder()
                            .add(new ResizeOp(height, width, ResizeOp.ResizeMethod.BILINEAR));

            if (inputType == DataType.FLOAT32) {
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

            // Use labels.txt if available
            String detected;
            if (labels != null && bestIdx < labels.size()) {
                detected = labels.get(bestIdx);
            } else {
                detected = "Class " + bestIdx;
            }

            // (Optional) Map to label names if you have labels.txt in assets
            String label = (labels != null && bestIdx < labels.size()) ? labels.get(bestIdx) : ("Class " + bestIdx);

            // After finishing detection
            String scoreStr = String.format("%.3f", bestScore);

            // Create bundle
            Bundle bundle = new Bundle();
            bundle.putString("detected_name", detected);
            bundle.putString("detected_score", scoreStr);
            bundle.putString("image_uri", uri.toString()); // send image path

            // Navigate
            NavController navController = NavHostFragment.findNavController(IdentifyFragment.this);
            navController.navigate(R.id.action_navigation_identify_to_identify_result, bundle);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Context context, String modelFileName) throws IOException {
        FileInputStream inputStream = new FileInputStream(context.getAssets().openFd(modelFileName).getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = context.getAssets().openFd(modelFileName).getStartOffset();
        long declaredLength = context.getAssets().openFd(modelFileName).getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private List<String> loadLabels(Context context, String fileName) {
        List<String> labelList = new ArrayList<>();
        try (InputStream is = context.getAssets().open(fileName)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            while ((line = reader.readLine()) != null) {
                labelList.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return labelList;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}