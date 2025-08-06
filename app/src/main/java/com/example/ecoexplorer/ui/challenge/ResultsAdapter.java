package com.example.ecoexplorer.ui.challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private List<Results> resultsList;

    public ResultsAdapter(Context context, List<Results> resultsList) {
        this.resultsList = resultsList;
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_recent_results, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        Results results = resultsList.get(position);
    }

    @Override
    public int getItemCount() {
        return resultsList.size();
    }

    public void setResults(List<Results> newResults) {
        this.resultsList.clear();
        this.resultsList.addAll(newResults);
        notifyDataSetChanged();
    }

    static class ResultsViewHolder extends RecyclerView.ViewHolder {
        private TextView subjectTextView;
        private TextView descriptionTextView;

        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.result_subjects);
            descriptionTextView = itemView.findViewById(R.id.result_description);
        }
    }
}
