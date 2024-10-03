package com.example.goodeats9;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.ViewHolder> {

    private Context context;
    private List<Datacls> savedRecipes;

    public SavedRecipeAdapter(Context context, List<Datacls> savedRecipes) {
        this.context = context;
        this.savedRecipes = savedRecipes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_saved_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Datacls recipe = savedRecipes.get(position);

        // Set recipe name and userName
        holder.recipeName.setText(recipe.getName());
        holder.userName.setText("Saved by: " + recipe.getUserName());

        // Load the video or an image thumbnail
        holder.recipeVideoView.setVideoURI(Uri.parse(recipe.getVideoUri()));
        holder.recipeVideoView.seekTo(1); // Display the first frame as a thumbnail

        // If you have an imageUri, you can load it into an ImageView
        if (!recipe.getImageUri().isEmpty()) {
            Picasso.get().load(recipe.getImageUri()).into(holder.recipeImageView);
        }

        // Play video when clicked (optional)
        holder.itemView.setOnClickListener(view -> {
            holder.recipeVideoView.setVisibility(View.VISIBLE);
            holder.recipeVideoView.start();
        });
    }

    @Override
    public int getItemCount() {
        return savedRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView recipeName, userName;
        VideoView recipeVideoView;
        ImageView recipeImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            userName = itemView.findViewById(R.id.userName);
            recipeVideoView = itemView.findViewById(R.id.recipeVideoView);
            recipeImageView = itemView.findViewById(R.id.recipeImage);
        }
    }
}
