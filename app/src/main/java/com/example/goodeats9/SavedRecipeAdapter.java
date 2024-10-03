package com.example.goodeats9;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.ViewHolder> {

    private Context context;
    private List<Datacls> savedRecipesList;

    public SavedRecipeAdapter(Context context, List<Datacls> savedRecipesList) {
        this.context = context;
        this.savedRecipesList = savedRecipesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Datacls recipe = savedRecipesList.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.userName.setText("Saved by: " + recipe.getUserName());

        // Load the recipe image using an image loading library (optional)
        // Example with Glide:
        // Glide.with(context).load(recipe.getImageUri()).into(holder.recipeImage);
    }

    @Override
    public int getItemCount() {
        return savedRecipesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView recipeName, userName;
        public ImageView recipeImage;

        public ViewHolder(View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            userName = itemView.findViewById(R.id.userName);
            recipeImage = itemView.findViewById(R.id.recipeImage);
        }
    }
}
