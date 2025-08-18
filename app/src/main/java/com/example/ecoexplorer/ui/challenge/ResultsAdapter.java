package com.example.ecoexplorer.ui.challenge;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecoexplorer.R;

import java.util.ArrayList;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private Context context;
    private ArrayList<Results> resultsList;

    public ResultsAdapter(Context context, ArrayList<Results> resultsList) {
        this.context = context;
        this.resultsList = resultsList;
    }

    public void setResults(ArrayList<Results> newResults) {
        this.resultsList = newResults;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_recent_results, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        Results result = resultsList.get(position);

        holder.quizName.setText(formatQuizName(result.getQuizName()));
        holder.score.setText(result.getScore() + " / " + result.getTotal());
        holder.date.setText(result.getDate());
    }

    @Override
    public int getItemCount() {
        return resultsList != null ? resultsList.size() : 0;
    }

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {
        TextView quizName, score, date;

        public ResultsViewHolder(@NonNull View itemView) {
            super(itemView);
            quizName = itemView.findViewById(R.id.tv_quiz_name);
            score = itemView.findViewById(R.id.tv_score);
            date = itemView.findViewById(R.id.tv_date);
        }
    }

    // Optional: make quiz name more readable
    private String formatQuizName(String rawName) {
        if (rawName == null) return "";
        return rawName.replace("_", " ").toUpperCase(); // e.g. "animal_quiz" → "ANIMAL QUIZ"
    }
}
