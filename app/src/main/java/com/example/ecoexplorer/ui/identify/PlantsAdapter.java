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

public class PlantsAdapter extends RecyclerView.Adapter<PlantsAdapter.PlantsViewHolder> {

    private List<Plants> plantList;

    public PlantsAdapter(List<Plants> plantList) {
        this.plantList = plantList;
    }

    @NonNull
    @Override
    public PlantsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.identify_lists, parent, false);
        return new PlantsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlantsViewHolder holder, int position) {
        Plants plant = plantList.get(position);
        holder.textPlantName.setText(plant.getName());
        holder.imagePlant.setImageResource(plant.getImageResId());
    }

    @Override
    public int getItemCount() {
        return plantList.size();
    }

    public static class PlantsViewHolder extends RecyclerView.ViewHolder {
        TextView textPlantName;
        ImageView imagePlant;

        public PlantsViewHolder(@NonNull View itemView) {
            super(itemView);
            textPlantName = itemView.findViewById(R.id.items_name);
            imagePlant = itemView.findViewById(R.id.items_image);
        }
    }
}
