package com.example.goodeats9;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.goodeats9.MethodStep;
import com.example.goodeats9.R;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.MethodViewHolder> {
    private List<MethodStep> methodList;

    public StepAdapter(List<MethodStep> methodList) {
        this.methodList = methodList;
    }

    @NonNull
    @Override
    public MethodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
        return new MethodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MethodViewHolder holder, int position) {
        MethodStep methodStep = methodList.get(position);
        holder.stepNumber.setText(methodStep.getStepNumber());
        holder.stepDescription.setText(methodStep.getDescription());
    }

    @Override
    public int getItemCount() {
        return methodList.size();
    }

    static class MethodViewHolder extends RecyclerView.ViewHolder {
        TextView stepNumber;
        TextView stepDescription;

        MethodViewHolder(@NonNull View itemView) {
            super(itemView);
            stepNumber = itemView.findViewById(R.id.step_number);
            stepDescription = itemView.findViewById(R.id.step_description);
        }
    }
}