package com.example.goodeats9;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
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

        // Set recipe name and user name
        holder.recipeName.setText(recipe.getName());
        holder.userName.setText("Saved by: " + recipe.getUserName());

        // Load the video URI
        Uri videoUri = Uri.parse(recipe.getVideoUri());
        holder.recipeVideoView.setVideoURI(videoUri);

        // Load the thumbnail using MediaMetadataRetriever safely
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(context, videoUri);
            Bitmap thumbnail = retriever.getFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

            if (thumbnail != null) {
                holder.thumbnailImageView.setImageBitmap(thumbnail);
            } else {
                holder.thumbnailImageView.setImageResource(R.drawable.placeholder_image); // Use your placeholder image
            }

            retriever.release(); // Release retriever after use
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
            holder.thumbnailImageView.setImageResource(R.drawable.placeholder_image); // Fallback if error occurs
        }

        // Play button click listener
        holder.playIcon.setOnClickListener(v -> {
            holder.thumbnailImageView.setVisibility(View.GONE); // Hide thumbnail
            holder.playIcon.setVisibility(View.GONE); // Hide play button
            holder.recipeVideoView.setVisibility(View.VISIBLE); // Show VideoView
            holder.recipeVideoView.start(); // Start video
            holder.pauseIcon.setVisibility(View.VISIBLE); // Show pause button
            holder.updateSeekBar(holder); // Update seek bar progress
        });

        // Pause button click listener
        holder.pauseIcon.setOnClickListener(v -> {
            holder.recipeVideoView.pause();
            holder.playIcon.setVisibility(View.VISIBLE); // Show play button
            holder.pauseIcon.setVisibility(View.GONE); // Hide pause button
        });

        // Video completion listener
        holder.recipeVideoView.setOnCompletionListener(mediaPlayer -> {
            holder.recipeVideoView.stopPlayback();
            holder.playIcon.setVisibility(View.VISIBLE); // Show play button
            holder.pauseIcon.setVisibility(View.GONE); // Hide pause button
            holder.recipeVideoView.setVisibility(View.GONE); // Hide VideoView
            holder.thumbnailImageView.setVisibility(View.VISIBLE); // Show thumbnail again
            holder.seekBar.setProgress(0); // Reset seek bar progress
        });

        // Handle seek bar changes
        holder.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    holder.recipeVideoView.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set up seek bar when video is prepared
        holder.recipeVideoView.setOnPreparedListener(mediaPlayer -> {
            holder.seekBar.setMax(holder.recipeVideoView.getDuration());
            holder.updateSeekBar(holder);
        });

        // Delete button click listener
        holder.deleteButton.setOnClickListener(v -> {
            deleteRecipeFromDatabase(recipe.getVideoUri()); // Call method to delete from the database

            // Remove the item from the list and notify the adapter
            savedRecipes.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, savedRecipes.size());
        });
    }

    @Override
    public int getItemCount() {
        return savedRecipes.size();
    }

    private void deleteRecipeFromDatabase(String videoUri) {
        // Extract the unique identifier for deletion (assumed to be the same as videoUri)
        String videoIdentifier = videoUri.replace(".", "_"); // Change this according to your unique identifier format
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("savedRecipes");

        databaseReference.child(videoIdentifier) // Use videoIdentifier for deletion
                .removeValue()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Delete Recipe", "Recipe deleted successfully");
                    } else {
                        Log.e("Delete Recipe", "Failed to delete recipe", task.getException());
                    }
                });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView recipeName, userName;
        VideoView recipeVideoView;
        ImageView thumbnailImageView, playIcon, pauseIcon;
        SeekBar seekBar;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            userName = itemView.findViewById(R.id.userName);
            recipeVideoView = itemView.findViewById(R.id.recipeVideoView);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView); // Thumbnail ImageView
            playIcon = itemView.findViewById(R.id.playIcon);
            pauseIcon = itemView.findViewById(R.id.pauseIcon);
            seekBar = itemView.findViewById(R.id.seekBar);
            deleteButton = itemView.findViewById(R.id.imageButton); // Delete button
        }

        public void updateSeekBar(ViewHolder holder) {
            holder.seekBar.setProgress(holder.recipeVideoView.getCurrentPosition());
            if (holder.recipeVideoView.isPlaying()) {
                holder.seekBar.postDelayed(() -> updateSeekBar(holder), 1000);
            }
        }
    }
}