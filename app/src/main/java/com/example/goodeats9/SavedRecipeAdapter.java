package com.example.goodeats9;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ImageView;
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

        // Load the video
        holder.recipeVideoView.setVideoURI(Uri.parse(recipe.getVideoUri()));

        // Load the thumbnail image if applicable
        if (!recipe.getImageUri().isEmpty()) {
            Picasso.get().load(recipe.getImageUri()).into(holder.recipeImage);
        }

        // Set up play icon click listener
        holder.playIcon.setOnClickListener(v -> {
            holder.recipeVideoView.start();
            holder.playIcon.setVisibility(View.GONE);
            holder.pauseIcon.setVisibility(View.VISIBLE);
            holder.updateSeekBar(holder);
        });

        // Set up pause icon click listener
        holder.pauseIcon.setOnClickListener(v -> {
            holder.recipeVideoView.pause();
            holder.playIcon.setVisibility(View.VISIBLE);
            holder.pauseIcon.setVisibility(View.GONE);
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

        // Handle video completion
        holder.recipeVideoView.setOnCompletionListener(mediaPlayer -> {
            holder.recipeVideoView.stopPlayback();
            holder.playIcon.setVisibility(View.VISIBLE);
            holder.pauseIcon.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return savedRecipes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView recipeName, userName;
        VideoView recipeVideoView;
        ImageView recipeImage, playIcon, pauseIcon;
        SeekBar seekBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            recipeName = itemView.findViewById(R.id.recipeName);
            userName = itemView.findViewById(R.id.userName);
            recipeVideoView = itemView.findViewById(R.id.recipeVideoView);
            recipeImage = itemView.findViewById(R.id.recipeImage);
            playIcon = itemView.findViewById(R.id.playIcon);
            pauseIcon = itemView.findViewById(R.id.pauseIcon);
            seekBar = itemView.findViewById(R.id.seekBar);
        }

        public void updateSeekBar(ViewHolder holder) {
            holder.seekBar.setProgress(holder.recipeVideoView.getCurrentPosition());
            if (holder.recipeVideoView.isPlaying()) {
                holder.seekBar.postDelayed(() -> updateSeekBar(holder), 1000);
            }
        }
    }
}
