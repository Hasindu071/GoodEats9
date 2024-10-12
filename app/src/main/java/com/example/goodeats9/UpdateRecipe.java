package com.example.goodeats9;

import android.content.Intent;
import android.net.Uri; // Import Uri for handling images/videos
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class UpdateRecipe extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;
    private ImageView recipeImageView;
    private VideoView recipeVideoView;
    private Uri imageUri;
    private Uri videoUri;

    private String existingImageUri;
    private String existingVideoUri;
    private String recipeID;
    private String userEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_recipe);

        // Initialize UI elements
        EditText titleEditText = findViewById(R.id.entername);
        EditText cookTimeEditText = findViewById(R.id.entertime);
        EditText servesEditText = findViewById(R.id.enterserves);
        EditText descriptionEditText = findViewById(R.id.enterdiscription);
        recipeImageView = findViewById(R.id.addPic);
        recipeVideoView = findViewById(R.id.recipeVideo);

        Button addImage = findViewById(R.id.updateImage);
        Button addVideo = findViewById(R.id.updateVideo);
        Button update = findViewById(R.id.updatebtn);

        addImage.setOnClickListener(v -> openImageChooser());
        addVideo.setOnClickListener(v -> openVideoChooser());

        // Retrieve data from Intent
        Intent intent = getIntent();
        recipeID = intent.getStringExtra("recipeID");
        String title = intent.getStringExtra("title");
        String cookTime = intent.getStringExtra("cookTime");
        String serves = intent.getStringExtra("serves");
        String description = intent.getStringExtra("description");
        existingImageUri = intent.getStringExtra("Image");
        existingVideoUri = intent.getStringExtra("video");
        userEmail = intent.getStringExtra("currentUserEmail");

        // Set retrieved values to EditText fields
        titleEditText.setText(title);
        cookTimeEditText.setText(cookTime);
        servesEditText.setText(serves);
        descriptionEditText.setText(description);

        // Update button click listener
        update.setOnClickListener(v -> {
            uploadNewMedia(
                    titleEditText.getText().toString(),
                    cookTimeEditText.getText().toString(),
                    servesEditText.getText().toString(),
                    descriptionEditText.getText().toString()
            );
        });
    }

    private void uploadNewMedia(String title, String cookTime, String serves, String description) {
        // Reference to Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        HashMap<String, Object> updatedRecipe = new HashMap<>();

        // If a new image is selected, upload it and then handle video
        if (imageUri != null) {
            deleteImageOnStorage(existingImageUri); // Delete the existing image
            StorageReference imageRef = storageRef.child("RecipeImages/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUrl -> {
                                updatedRecipe.put("imageUri", downloadUrl.toString());
                                uploadVideoIfSelected(updatedRecipe, title, cookTime, serves, description); // Proceed to video
                            }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            // No new image, proceed to check and upload video
            uploadVideoIfSelected(updatedRecipe, title, cookTime, serves, description);
        }
    }

    private void uploadVideoIfSelected(HashMap<String, Object> updatedRecipe, String title, String cookTime, String serves, String description) {
        // If a new video is selected, upload it; otherwise update the recipe in the database
        if (videoUri != null) {
            // Delete the existing video before uploading the new one
            deleteVideoOnStorage(existingVideoUri);

            // Create a reference for the new video
            StorageReference videoRef = FirebaseStorage.getInstance().getReference()
                    .child("RecipeVideos/" + System.currentTimeMillis() + ".mp4");

            // Start uploading the video
            videoRef.putFile(videoUri)
                    .addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl()
                            .addOnSuccessListener(downloadUrl -> {
                                // Update the recipe with the new video URL
                                updatedRecipe.put("videoUri", downloadUrl.toString());
                                // Update database after uploading video
                                updateRecipeInDatabase(updatedRecipe, title, cookTime, serves, description);
                            }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show())
                    .addOnProgressListener(snapshot -> {
                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Toast.makeText(this, "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
                    });
        } else {
            // No new video, directly update the recipe in the database
            updateRecipeInDatabase(updatedRecipe, title, cookTime, serves, description);
        }
    }

    private void updateRecipeInDatabase(HashMap<String, Object> updatedRecipe, String title, String cookTime, String serves, String description) {
        // Reference to Firebase Database
        DatabaseReference recipeRef = FirebaseDatabase.getInstance()
                .getReference("recipes")
                .child(userEmail)
                .child(recipeID);

        // Add other details to the updated recipe
        updatedRecipe.put("name", title);
        updatedRecipe.put("cookTime", cookTime);
        updatedRecipe.put("serves", serves);
        updatedRecipe.put("description", description);

        // Update recipe in the database
        recipeRef.updateChildren(updatedRecipe).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(UpdateRecipe.this, "Recipe updated successfully!", Toast.LENGTH_SHORT).show();
                finish(); // Close the activity
            } else {
                Toast.makeText(UpdateRecipe.this, "Failed to update recipe. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void deleteImageOnStorage(String imageUri) {
        // delete image from storage
        if (imageUri != null && !imageUri.isEmpty()) {
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUri);
            imageRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("DeleteFile", "Image deleted successfully");
            }).addOnFailureListener(e -> {
                Log.e("DeleteFile", "Failed to delete image: " + e.getMessage());
            });
        }
    }

    private void deleteVideoOnStorage(String videoUri) {
        // delete video from storage
        if (videoUri != null && !videoUri.isEmpty()) {
            StorageReference videoRef = FirebaseStorage.getInstance().getReferenceFromUrl(videoUri);
            videoRef.delete().addOnSuccessListener(aVoid -> {
                Log.d("DeleteFile", "Video deleted successfully");
            }).addOnFailureListener(e -> {
                Log.e("DeleteFile", "Failed to delete video: " + e.getMessage());
            });
        }
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result is OK and data is not null
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData();
                if (imageUri != null) {
                    // Load selected image into ImageView using Glide
                    Glide.with(this).load(imageUri).into(recipeImageView);
                }
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                videoUri = data.getData(); // Store the video URI
                if (videoUri != null) {
                    // Set the video URI and start playing
                    recipeVideoView.setVideoURI(videoUri);
                    recipeVideoView.start();
                }
            }
        }
    }
}


