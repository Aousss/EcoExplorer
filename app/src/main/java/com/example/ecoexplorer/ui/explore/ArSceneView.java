package com.example.ecoexplorer.ui.explore;

import static com.example.ecoexplorer.R.id.sceneView;

import android.icu.text.Transliterator;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecoexplorer.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.ar.core.Config;

import io.github.sceneview.ar.node.ArModelNode;
import io.github.sceneview.ar.node.PlacementMode;
import io.github.sceneview.node.VideoNode;

public class ArSceneView extends Fragment {

    private ArSceneView sceneView;
    private ExtendedFloatingActionButton placeAR;

    private ArModelNode modelNode;
    private MediaPlayer mediaPlayer;
    private Config.LightEstimationMode lightEstimationMode;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ar_scene_layout, container, false);

        placeAR = view.findViewById(R.id.placeAR);
        placeAR.setOnClickListener(v -> placeModel());

        sceneView = (ArSceneView) view.findViewById(R.id.sceneView);
        sceneView.setLightEstimationMode(Config.LightEstimationMode.DISABLED);

        mediaPlayer = MediaPlayer.create(requireContext(), R.raw.ad);

        VideoNode videoNode = new VideoNode(
                sceneView.getEngine(),
                0.7f,
                new Transliterator.Position(),
                "model/plane.glb",
                mediaPlayer,
                (videoNode, error) -> mediaPlayer.start());

        // AR Model Node
        modelNode = new ArModelNode(sceneView.getEngine(), PlacementMode.INSTANT);
        modelNode.loadModelGlbAsync(
                "models/sofa.glb",
                1f,
                new Transliterator.Position(),
                (node, error) -> {
            if (error == null) {
                sceneView.getPlaneRenderer().setVisible(true);
            } else {
                Toast.makeText(requireContext(), "Model load failed: " + error, Toast.LENGTH_SHORT).show();
            }
        });

        modelNode.setOnAnchorChanged(anchor -> {
            placeAR.setVisibility(anchor != null ? View.GONE : View.VISIBLE);
            return null;
        });


        sceneView.addChild(modelNode);
        modelNode.addChild(videoNode1);

        return view;
    }

    private void placeModel() {
        modelNode.anchor();
        sceneView.getPlaneRenderer().setVisibility(false);
    }

    public void setLightEstimationMode(Config.LightEstimationMode lightEstimationMode) {
        this.lightEstimationMode = lightEstimationMode;
    }

    public Config.LightEstimationMode getLightEstimationMode() {
        return lightEstimationMode;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}