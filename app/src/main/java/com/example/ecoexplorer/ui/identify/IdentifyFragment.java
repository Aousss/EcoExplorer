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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;
import com.example.ecoexplorer.Utils;
import com.example.ecoexplorer.databinding.IdentifyBinding;
import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

public class IdentifyFragment extends Fragment {

    private Uri imageUri;

    private RecyclerView recyclerViewPlants, recyclerViewAnimals;
    private PlantsAdapter plantsAdapter;
    private List<Plants> plantList;

    private AnimalsAdapter animalsAdapter;
    private List<Animals> animalsList;

    private IdentifyBinding binding;

    private Module module;

    // Fastai ResNet input size
    private static final int IMAGE_SIZE = 224;
    private static final String[] CLASS_LABELS = {"Butterfly", "Cicadas", "Coconut", "Daisy", "Damselfly", "Dragonfly", "Leafhopper", "Longhorn Beetle", "Moths", "Oil Palm", "Pinang (Areca Palm / Betel Nut Palm)", "Rhinoceros Beetle", "Sunflower", "Torch Ginger", "Turmeric", "Weevil"};

    private static final Map<String, String[]> SPECIES_INFO = new HashMap<String, String[]>() {{
        put("Butterfly", new String[]{
                "Order Lepidoptera (many species, e.g., Danaus plexippus – Monarch)",
                "Forests, grasslands, gardens, wetlands",
                "Areas with flowering plants and host plants for caterpillars",
                "Pollinators, food source for birds/reptiles, bioindicators of ecosystem health"});

        put("Cicadas", new String[]{
                "Family Cicadidae",
                "Forests, grasslands, agricultural areas",
                "Trees, shrubs (adults), underground near roots (nymphs)",
                "Aerate soil (nymphs), recycle nutrients, food source for birds/mammals"});

        put("Coconut", new String[]{
                "Cocos Nucifera",
                "Coastal, tropical regions",
                "Sandy soils near coasts, plantations",
                "Coastal protection, food & materials for humans/wildlife, prevents erosion"});

        put("Daisy", new String[]{
                "Bellis perennis",
                "Grasslands, meadows, gardens",
                "Open sunny areas with moist to well-drained soils",
                "Pollinator support, ground cover preventing erosion"});

        put("Damselfly", new String[]{
                "Suborder Zygoptera",
                "Freshwater wetlands, rivers, ponds",
                "Aquatic vegetation near slow-moving water",
                "Predator of mosquitoes and small insects, indicator of freshwater quality"});

        put("Dragonfly", new String[]{
                "Suborder Anisoptera",
                "Wetlands, rivers, ponds, lakes",
                "Aquatic (larvae), near freshwater vegetation (adults)",
                "Predator of mosquitoes/insects, freshwater ecosystem health indicator"});

        put("Leafhopper", new String[]{
                "Family Cicadellidae",
                "Grasslands, agricultural fields, forests",
                "On leaves of grasses, shrubs, crops",
                "Plant feeders (sap-suckers), prey for predators, sometimes crop pests"});

        put("Longhorn Beetle", new String[]{
                "Family Cerambycidae",
                "Forests, woodlands",
                "Dead/decaying wood, tree trunks, sometimes crops",
                "Decomposers (wood breakdown), nutrient recyclers, some species are pests"});

        put("Moths", new String[]{
                "Order Lepidoptera (many families, e.g., Noctuidae)",
                "Forests, grasslands, urban gardens",
                "Near host plants, light sources at night",
                "Nocturnal pollinators, prey for bats and birds, indicators of ecosystem health"});

        put("Oil Palm", new String[]{
                "Elaeis guineensis",
                "Tropical rainforests, plantations",
                "Humid lowland tropics",
                "Source of palm oil, carbon sink, habitat for some wildlife (though plantations reduce biodiversity)"});

        put("Pinang (Areca Palm / Betel Nut Palm)", new String[]{
                "Areca catechu",
                "Tropical/subtropical forests, cultivated areas",
                "Moist, well-drained soils in lowland areas",
                "Source of betel nut, shade tree, part of cultural practices, habitat for small fauna"});

        put("Rhinoceros Beetle", new String[]{
                "Family Scarabaeidae (subfamily Dynastinae)",
                "Forests, plantations, gardens",
                "Decaying wood, soil, coconut/oil palm plantations",
                "Decomposers, soil aerators, some species are agricultural pests (damage palms)"});

        put("Sunflower", new String[]{
                "Helianthus Annuus",
                "Grasslands, cultivated fields",
                "Well-drained soils in sunny open areas",
                "Pollinator attractor, food for birds/insects, soil health improvement (phytoremediation)"});

        put("Torch Ginger", new String[]{
                "Etlingera Elatior",
                "Tropical rainforests, gardens, plantations",
                "Moist, shaded tropical soils",
                "Ornamental plant, edible flower buds, supports pollinators (bees, birds)"});

        put("Turmeric", new String[]{
                "Curcuma longa",
                "Tropical forests, cultivated farmlands",
                "Warm, humid soils, shaded areas",
                "Medicinal and culinary use, soil enrichment, habitat for small insects"});

        put("Weevil", new String[]{
                "Family Curculionidae",
                "Forests, grasslands, agricultural lands",
                "On plants, seeds, stored grains",
                "Seed dispersers, decomposers, crop pests (some species destructive to stored food/crops)"});
    }};

    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<Intent> galleryLauncher;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate layout using ViewBinding
        binding = IdentifyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerViewPlants = root.findViewById(R.id.recycleView_PlantsIdentify);
        plantsData();

        recyclerViewAnimals = root.findViewById(R.id.recycleView_AnimalIdentify);
        animalsData();

        CardView btnGallery = root.findViewById(R.id.btnGallery);
        CardView btnCamera = root.findViewById(R.id.btnCamera);

        // Load tflite model
        loadModel();

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
        plantList.add(new Plants("Coconut",
                "A tall tropical palm producing coconuts, valued for food, oil, and fiber. Often called the 'Tree of Life.'",
                "Cocos Nucifera",
                "Coastal, tropical regions",
                "Sandy soils near coasts, plantations",
                "Coastal protection, food & materials for humans/wildlife, prevents erosion",
                R.drawable.identify_coconut));

        plantList.add(new Plants("Daisy",
                "Small white-petaled flowering plant with a yellow center, symbolizing innocence and purity.",
                "Bellis perennis",
                "Grasslands, meadows, gardens",
                "Open sunny areas with moist to well-drained soils",
                "Pollinator support, ground cover preventing erosion",
                R.drawable.identify_daisy));

        plantList.add(new Plants("Oil Palm",
                "A tropical palm cultivated for palm oil, a major global commodity used in food, cosmetics, and fuel.",
                "Elaeis guineensis",
                "Tropical rainforests, plantations",
                "Humid lowland tropics",
                "Source of palm oil, carbon sink, habitat for some wildlife (though plantations reduce biodiversity)",
                R.drawable.identify_oilpalm));

        plantList.add(new Plants("Pinang",
                "Slender palm tree producing areca nuts, chewed with betel leaves in many Asian cultures.",
                "Areca catechu",
                "Tropical/subtropical forests, cultivated areas",
                "Moist, well-drained soils in lowland areas",
                "Source of betel nut, shade tree, part of cultural practices, habitat for small fauna",
                R.drawable.identify_pinang));

        plantList.add(new Plants("Sunflower",
                "Tall plant with large yellow flower heads that follow the sun (heliotropism). Seeds are rich in oil and nutrients.",
                "Helianthus Annuus",
                "Grasslands, cultivated fields",
                "Well-drained soils in sunny open areas",
                "Pollinator attractor, food for birds/insects, soil health improvement (phytoremediation)",
                R.drawable.identify_sunflower));

        plantList.add(new Plants("Turmeric",
                "A spice and medicinal plant with rhizomes producing the yellow pigment curcumin, used in food and traditional medicine.",
                "Curcuma longa",
                "Tropical forests, cultivated farmlands",
                "Warm, humid soils, shaded areas",
                "Medicinal and culinary use, soil enrichment, habitat for small insects",
                R.drawable.identify_tumeric));

        plantList.add(new Plants("Torch Ginger",
                "Ornamental tropical plant with tall stems and striking pink-red flowers, also used in Southeast Asian cuisine.",
                "Etlingera Elatior",
                "Tropical rainforests, gardens, plantations",
                "Moist, shaded tropical soils",
                "Ornamental plant, edible flower buds, supports pollinators (bees, birds)",
                R.drawable.identify_torchginger));

        plantsAdapter = new PlantsAdapter(plantList, plants -> {
            Bundle bundle = new Bundle();
            bundle.putString("name", plants.getName());
            bundle.putString("desc", plants.getDescription());
            bundle.putInt("image", plants.getImageResId());
            bundle.putString("sciName", plants.getScientificName());
            bundle.putString("ecosystem", plants.getEcosystem());
            bundle.putString("habitat", plants.getHabitat());
            bundle.putString("role", plants.getRole());

            NavController navController = NavHostFragment.findNavController(IdentifyFragment.this);
            navController.navigate(R.id.action_navigation_identify_to_details, bundle);
        });
        recyclerViewPlants.setAdapter(plantsAdapter);
    }

    private void animalsData() {
        recyclerViewAnimals.setLayoutManager(new LinearLayoutManager(
                getContext(),
                LinearLayoutManager.HORIZONTAL,
                false));

        // Animals Data
        animalsList = new ArrayList<>();
        animalsList.add(new Animals("Butterfly",
                "Colorful winged insects known for metamorphosis (egg → caterpillar → pupa → adult). Symbols of biodiversity and beauty.",
                "Order Lepidoptera (many species, e.g., Danaus plexippus – Monarch)",
                "Forests, grasslands, gardens, wetlands",
                "Areas with flowering plants and host plants for caterpillars",
                "Pollinators, food source for birds/reptiles, bioindicators of ecosystem health",
                R.drawable.identify_butterfly));

        animalsList.add(new Animals("Dragonfly",
                "Fast-flying insects with large eyes and strong wings. Ancient lineage with fossils dating back 300 million years.",
                "Suborder Anisoptera",
                "Wetlands, rivers, ponds, lakes",
                "Aquatic (larvae), near freshwater vegetation (adults)",
                "Predator of mosquitoes/insects, freshwater ecosystem health indicator",
                R.drawable.identify_dragonfly));

        animalsList.add(new Animals("Leafhopper",
                "Small, wedge-shaped insects that jump quickly from plant to plant, feeding on sap.",
                "Family Cicadellidae",
                "Grasslands, agricultural fields, forests",
                "On leaves of grasses, shrubs, crops",
                "Plant feeders (sap-suckers), prey for predators, sometimes crop pests",
                R.drawable.identify_leafhopper));

        animalsList.add(new Animals("Moths",
                "Mostly nocturnal relatives of butterflies, with diverse patterns and colors. Some mimic leaves or bark for camouflage.",
                "Order Lepidoptera (many families, e.g., Noctuidae)",
                "Forests, grasslands, urban gardens",
                "Near host plants, light sources at night",
                "Nocturnal pollinators, prey for bats and birds, indicators of ecosystem health",
                R.drawable.identify_moths));

        animalsList.add(new Animals("Weevil",
                "Small beetles with long snouts. Many are agricultural pests, feeding on seeds, grains, and stored products.",
                "Family Curculionidae",
                "Forests, grasslands, agricultural lands",
                "On plants, seeds, stored grains",
                "Seed dispersers, decomposers, crop pests (some species destructive to stored food/crops)",
                R.drawable.identify_weevil));

        animalsList.add(new Animals("Rhinoceros Beetle",
                "Large beetles with horn-like structures on males. Known for their strength, capable of lifting many times their body weight.",
                "Family Scarabaeidae (subfamily Dynastinae)",
                "Forests, plantations, gardens",
                "Decaying wood, soil, coconut/oil palm plantations",
                "Decomposers, soil aerators, some species are agricultural pests (damage palms)",
                R.drawable.identify_rhinoceros));

        animalsList.add(new Animals("Damselfly",
                "Slender-bodied insects similar to dragonflies but fold their wings when resting.",
                "Suborder Zygoptera",
                "Freshwater wetlands, rivers, ponds",
                "Aquatic vegetation near slow-moving water",
                "Predator of mosquitoes and small insects, indicator of freshwater quality",
                R.drawable.identify_damselfly));

        animalsList.add(new Animals("Cicadas",
                "Insects famous for their loud buzzing sounds made by males. Life cycles range from annual to 13–17 years.",
                "Family Cicadidae",
                "Forests, grasslands, agricultural areas",
                "Trees, shrubs (adults), underground near roots (nymphs)",
                "Aerate soil (nymphs), recycle nutrients, food source for birds/mammals",
                R.drawable.identify_cicadas));

        animalsList.add(new Animals("Longhorn Beetle",
                "Beetles with long antennae, often longer than their bodies. Many species have wood-boring larvae.",
                "Family Cerambycidae",
                "Forests, woodlands",
                "Dead/decaying wood, tree trunks, sometimes crops",
                "Decomposers (wood breakdown), nutrient recyclers, some species are pests",
                R.drawable.identify_longhorn));

        animalsAdapter = new AnimalsAdapter(animalsList, animals -> {
           Bundle bundle = new Bundle();
           bundle.putString("name", animals.getName());
           bundle.putString("desc", animals.getDescription());
           bundle.putInt("image", animals.getImageResId());
           bundle.putString("sciName", animals.getScientificName());
           bundle.putString("ecosystem", animals.getEcosystem());
           bundle.putString("habitat", animals.getHabitat());
           bundle.putString("role", animals.getRole());

            NavController navController = NavHostFragment.findNavController(IdentifyFragment.this);
            navController.navigate(R.id.action_navigation_identify_to_details, bundle);
        });

        recyclerViewAnimals.setAdapter(animalsAdapter);
    }

    private void loadModel() {
//        try {
//            tflite = new Interpreter(loadModelFile(requireContext(), "model.pt"));
//            Toast.makeText(getContext(), "Model SUCCESSFULLY loaded.", Toast.LENGTH_SHORT).show();
//        } catch (Exception e) {
//            Toast.makeText(getContext(), "Model FAILED to load.", Toast.LENGTH_SHORT).show();
//            e.printStackTrace();
//        }
        try {
            module = Module.load(Utils.assetFilePath(requireContext(), "model.pt"));
//            Toast.makeText(getContext(), "Model SUCCESSFULLY loaded.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Model FAILED to load.", Toast.LENGTH_SHORT).show();
        }
    }

    private Bundle classifyImage(Bitmap bitmap) {

        // Resize bitmap to 224x224
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true);

        // Convert bitmap to Tensor with ImageNet normalization
        Tensor inputTensor = TensorImageUtils.bitmapToFloat32Tensor(
                resized,
                TensorImageUtils.TORCHVISION_NORM_MEAN_RGB,
                TensorImageUtils.TORCHVISION_NORM_STD_RGB
        );

        // Run inference
        Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();
        float[] scores = outputTensor.getDataAsFloatArray();

        // Find class with highest score
        int maxIdx = 0;
        float maxScore = -Float.MAX_VALUE;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxIdx = i;
            }
        }

        String detectedClass = CLASS_LABELS[maxIdx];
        String[] speciesInfo = SPECIES_INFO.get(detectedClass);

        Bundle resultBundle = new Bundle();
        if (speciesInfo != null) {
            resultBundle.putString("detected_name", detectedClass);
            resultBundle.putFloat("detected_score", maxScore);
            resultBundle.putString("speciesSciName", speciesInfo[0]);
            resultBundle.putString("speciesEcosystem", speciesInfo[1]);
            resultBundle.putString("speciesHabitat", speciesInfo[2]);
            resultBundle.putString("speciesRole", speciesInfo[3]);
        }

        return resultBundle;
    }

    @SuppressLint("Unknown")
    private void processImageUri(@NonNull Uri uri) {
        try (InputStream is = requireContext().getContentResolver().openInputStream(uri)) {
            Bitmap bitmap = BitmapFactory.decodeStream(is);

            // Run classification and get bundle
            Bundle resultBundle = classifyImage(bitmap);
            resultBundle.putString("image_uri", uri.toString());

            NavController navController = NavHostFragment.findNavController(IdentifyFragment.this);
            navController.navigate(R.id.action_navigation_identify_to_identify_result, resultBundle);

        } catch (Exception e) {
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

//    @SuppressLint("Unknown")
//    private void processImageUri(@NonNull Uri uri) {
//        try (InputStream is = requireContext().getContentResolver().openInputStream(uri)) {
//            Bitmap bitmap = BitmapFactory.decodeStream(is);
//
//            // 1) Ask the model what it wants
//            int inputIndex = 0;
//            int[] inputShape = tflite.getInputTensor(inputIndex).shape();   // e.g. [1, 224, 224, 3]
//            DataType inputType = tflite.getInputTensor(inputIndex).dataType(); // FLOAT32 or UINT8
//
//            int height = inputShape[1];
//            int width  = inputShape[2];
//
//            // 2) Build a TensorImage with the *model’s* input type
//            TensorImage tensorImage = new TensorImage(inputType);
//
//            // 3) Build an image processor:
//            org.tensorflow.lite.support.image.ImageProcessor.Builder procBuilder =
//                    new org.tensorflow.lite.support.image.ImageProcessor.Builder()
//                            .add(new ResizeOp(height, width, ResizeOp.ResizeMethod.BILINEAR));
//
//            if (inputType == DataType.FLOAT32) {
//                procBuilder.add(new NormalizeOp(0.0f, 1.0f / 255.0f));
//            }
//            org.tensorflow.lite.support.image.ImageProcessor imageProcessor = procBuilder.build();
//
//            tensorImage.load(bitmap);
//            tensorImage = imageProcessor.process(tensorImage);
//
//            // 4) Prepare output buffer based on model’s output tensor
//            int outputIndex = 0;
//            int[] outputShape = tflite.getOutputTensor(outputIndex).shape();   // e.g. [1, numClasses]
//            DataType outputType = tflite.getOutputTensor(outputIndex).dataType();
//
//            TensorBuffer outputBuffer = TensorBuffer.createFixedSize(outputShape, outputType);
//
//            // 5) Run inference
//            tflite.run(tensorImage.getBuffer(), outputBuffer.getBuffer().rewind());
//
//            // 6) Read results (handle both FLOAT32 and UINT8)
//            float[] probs;
//            if (outputType == DataType.FLOAT32) {
//                probs = outputBuffer.getFloatArray();
//            } else {
//                // UINT8 -> convert to float probs in [0..1]
//                int[] ints = outputBuffer.getIntArray(); // returns 0..255 for UINT8
//                probs = new float[ints.length];
//                for (int i = 0; i < ints.length; i++) probs[i] = ints[i] / 255.0f;
//            }
//
//            // 7) Argmax
//            int bestIdx = 0;
//            float bestScore = -1f;
//            for (int i = 0; i < probs.length; i++) {
//                if (probs[i] > bestScore) { bestScore = probs[i]; bestIdx = i; }
//            }
//
//            // Use labels.txt if available
//            String detected;
//            if (labels != null && bestIdx < labels.size()) {
//                detected = labels.get(bestIdx);
//            } else {
//                detected = "Class " + bestIdx;
//            }
//
//            // (Optional) Map to label names if you have labels.txt in assets
//            String label = (labels != null && bestIdx < labels.size()) ? labels.get(bestIdx) : ("Class " + bestIdx);
//
//            // After finishing detection
//            String scoreStr = String.format("%.3f", bestScore);
//
//            // Create bundle
//            Bundle bundle = new Bundle();
//            bundle.putString("detected_name", detected);
//            bundle.putString("detected_score", scoreStr);
//            bundle.putString("image_uri", uri.toString()); // send image path
//
//            // Navigate
//            NavController navController = NavHostFragment.findNavController(IdentifyFragment.this);
//            navController.navigate(R.id.action_navigation_identify_to_identify_result, bundle);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

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