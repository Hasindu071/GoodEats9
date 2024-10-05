package com.example.goodeats9;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SavedRecipeAdapter extends RecyclerView.Adapter<SavedRecipeAdapter.SavedRecipeViewHolder> {

    private List<Datacls> savedRecipes;
    private OnRecipeDeleteListener deleteListener;
    private Handler handler = new Handler();  // To handle seek bar updates

    public SavedRecipeAdapter(List<Datacls> savedRecipes, OnRecipeDeleteListener deleteListener) {
        this.savedRecipes = savedRecipes;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public SavedRecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_recipe, parent, false);
        return new SavedRecipeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedRecipeViewHolder holder, int position) {
        Datacls recipe = savedRecipes.get(position);
        holder.recipeName.setText(recipe.getName());
        holder.userName.setText("Saved by: " + recipe.getUserName());

        // Set the thumbnail for the video
        holder.thumbnailImageView.setImageURI(Uri.parse(recipe.getImageUri()));

        // Handle play button click
        holder.playIcon.setOnClickListener(v -> {
            holder.thumbnailImageView.setVisibility(View.GONE);
            holder.playIcon.setVisibility(View.GONE);
            holder.recipeVideoView.setVisibility(View.VISIBLE);
            holder.pauseIcon.setVisibility(View.VISIBLE);
            holder.recipeVideoView.setVideoURI(Uri.parse(recipe.getVideoUri()));
            holder.recipeVideoView.start();
            holder.videoSeekBar.setMax(holder.recipeVideoView.getDuration()); // Set max value for SeekBar

            // Update seekbar as the video plays
            updateSeekBar(holder);

            // Pause button click logic
            holder.pauseIcon.setOnClickListener(pauseView -> {
                if (holder.recipeVideoView.isPlaying()) {
                    holder.recipeVideoView.pause();
                    holder.pauseIcon.setImageResource(R.drawable.baseline_play_circle_24); // Change to play icon
                } else {
                    holder.recipeVideoView.start();
                    holder.pauseIcon.setImageResource(R.drawable.baseline_pause_circle_24); // Change to pause icon
                    updateSeekBar(holder); // Continue updating seekbar
                }
            });
        });

        // Handle delete button click
        holder.deleteButton.setOnClickListener(v -> {
            deleteListener.onRecipeDelete(recipe); // Pass the recipe to be deleted to the listener
        });

        // SeekBar change listener
        holder.videoSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    holder.recipeVideoView.seekTo(progress); // Manually adjust video time
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Pause the video when the user is adjusting the SeekBar
                holder.recipeVideoView.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Resume the video after the user has adjusted the SeekBar
                holder.recipeVideoView.start();
            }
        });
    }

    private void updateSeekBar(SavedRecipeViewHolder holder) {
        // Update the SeekBar according to video progress
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (holder.recipeVideoView.isPlaying()) {
                    int currentPosition = holder.recipeVideoView.getCurrentPosition();
                    holder.videoSeekBar.setMax(holder.recipeVideoView.getDuration());
                    holder.videoSeekBar.setProgress(currentPosition);
                    updateSeekBar(holder); // Recursive call to keep updating
                }
            }
        }, 1000); // Update every second
    }

    @Override
    public int getItemCount() {
        return savedRecipes.size();
    }

    public static class SavedRecipeViewHolder extends RecyclerView.ViewHolder {
        TextView recipeName, userName;
        VideoView recipeVideoView;
        ImageView thumbnailImageView, playIcon, pauseIcon;
        SeekBar videoSeekBar;
        ImageButton deleteButton;

        public SavedRecipeViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            userName = itemView.findViewById(R.id.userName);
            recipeVideoView = itemView.findViewById(R.id.recipeVideoView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);
            playIcon = itemView.findViewById(R.id.playIcon);
            pauseIcon = itemView.findViewById(R.id.pauseIcon);
            videoSeekBar = itemView.findViewById(R.id.videoSeekBar);
            deleteButton = itemView.findViewById(R.id.imageButton);
        }
    }

    public interface OnRecipeDeleteListener {
        void onRecipeDelete(Datacls recipe);
    }
}
