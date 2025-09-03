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

    public AnimalsAdapter(List<Animals> animalsList) {
        this.animalsList = animalsList;
    }

    @NonNull
    @Override
    public AnimalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.identify_lists, parent, false);
        return new AnimalsAdapter.AnimalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimalsAdapter.AnimalsViewHolder holder, int position) {
        Animals animals = animalsList.get(position);
        holder.textAnimalName.setText(animals.getName());
        holder.imageAnimal.setImageResource(animals.getImageResId());
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
