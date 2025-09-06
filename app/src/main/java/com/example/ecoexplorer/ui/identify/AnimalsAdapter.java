package com.example.ecoexplorer.ui.identify;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;

import java.util.List;

public class AnimalsAdapter extends RecyclerView.Adapter<AnimalsAdapter.AnimalsViewHolder> {

    private List<Animals> animalsList;
    private OnItemClickListener listener;

    // Custom click listener
    public interface OnItemClickListener {
        void onItemClick(Animals animal);
    }

    // Constructor
    public AnimalsAdapter(List<Animals> animalsList, OnItemClickListener listener) {
        this.animalsList = animalsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AnimalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.identify_lists, parent, false);
        return new AnimalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalsViewHolder holder, int position) {
        Animals animal = animalsList.get(position);
        holder.textAnimalName.setText(animal.getName());
        holder.imageAnimal.setImageResource(animal.getImageResId());

        // Handle clicks
        holder.itemView.setOnClickListener(v -> listener.onItemClick(animal));
    }

    @Override
    public int getItemCount() {
        return animalsList.size();
    }

    public static class AnimalsViewHolder extends RecyclerView.ViewHolder {
        TextView textAnimalName;
        ImageView imageAnimal;

        public AnimalsViewHolder(@NonNull View itemView) {
            super(itemView);
            textAnimalName = itemView.findViewById(R.id.items_name);
            imageAnimal = itemView.findViewById(R.id.items_image);
        }
    }
}
