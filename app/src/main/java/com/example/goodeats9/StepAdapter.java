package com.example.goodeats9;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

    public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepViewHolder> {

        private List<String> steps;

        public StepAdapter(List<String> steps) {
            this.steps = steps;
        }

        @NonNull
        @Override
        public StepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.step_item, parent, false);
            return new StepViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StepViewHolder holder, int position) {
            String step = steps.get(position);
            holder.stepNumber.setText("Step " + (position + 1));
            holder.stepDescription.setText(step); // Set the step description
        }

        @Override
        public int getItemCount() {
            return steps.size();
        }

        public static class StepViewHolder extends RecyclerView.ViewHolder {
            TextView stepNumber;
            TextView stepDescription;

            public StepViewHolder(@NonNull View itemView) {
                super(itemView);
                stepNumber = itemView.findViewById(R.id.step_number);
                stepDescription = itemView.findViewById(R.id.step_description);
            }
        }
    }


