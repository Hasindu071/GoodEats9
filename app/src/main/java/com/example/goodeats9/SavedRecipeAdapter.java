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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView recipeImage;
        TextView recipeName;
        TextView userName;

        public ViewHolder(View itemView) {
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            recipeName = itemView.findViewById(R.id.recipeName);
            userName = itemView.findViewById(R.id.userName);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recipe_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Datacls recipe = savedRecipesList.get(position);

        holder.recipeName.setText(recipe.getName());
        holder.userName.setText("Saved by: " + recipe.getUserName());

        // Use Glide to load images
        Glide.with(context)
                .load(recipe.getImageUri())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(holder.recipeImage);
    }

    @Override
    public int getItemCount() {
        return savedRecipesList.size();
    }
}
