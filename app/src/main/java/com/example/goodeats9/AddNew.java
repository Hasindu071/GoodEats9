package com.example.goodeats9;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddNew extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    private ImageView addPic, backbtn;
    private VideoView videoView;
    private EditText entername, enterdiscription, enterserves, entertime, ingrediant, quantity, method;
    private Button addrecipebtn, view_video_button;

    private Uri imageUri;
    private Uri videoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        // Initialize views
        addPic = findViewById(R.id.addPic);
        videoView = findViewById(R.id.videoView);
        entername = findViewById(R.id.entername);
        enterdiscription = findViewById(R.id.enterdiscription);
        enterserves = findViewById(R.id.enterserves);
        entertime = findViewById(R.id.entertime);
        ingrediant = findViewById(R.id.ingrediant);
        quantity = findViewById(R.id.quantity);
        method = findViewById(R.id.method);
        addrecipebtn = findViewById(R.id.addrecipebtn);
        view_video_button = findViewById(R.id.view_video_button);
        TextView hintText = findViewById(R.id.hintText);
        TextView hintText2 = findViewById(R.id.hintText2);

        // Back button functionality
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(v -> finish());

        // Select image for the recipe
        addPic.setOnClickListener(v -> openImageChooser());

        // Select video for the recipe
        view_video_button.setOnClickListener(v -> openVideoChooser());

        // Handle adding recipe button click
        addrecipebtn.setOnClickListener(v -> addRecipe());
    }

    // Function to open the image chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Function to open the video chooser
    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    // Handle the result of image/video picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addPic.setImageBitmap(getResizedImage(imageUri, 141, 141)); // Resize to ~5x5 cm
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }

    // Method to resize the selected image
    private Bitmap getResizedImage(Uri uri, int width, int height) {
        try {
            Bitmap originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Handle adding the recipe logic
    private void addRecipe() {
        String name = entername.getText().toString().trim();
        String description = enterdiscription.getText().toString().trim();
        String serves = enterserves.getText().toString().trim();
        String cookTime = entertime.getText().toString().trim();
        String ingredientName = ingrediant.getText().toString().trim();
        String ingredientQuantity = quantity.getText().toString().trim();
        String methodDescription = method.getText().toString().trim();

        // Validate input
        if (isInputValid(name, description, serves, cookTime, ingredientName, ingredientQuantity, methodDescription)) {
            // TODO: Save recipe details to Firebase or other storage
            Toast.makeText(AddNew.this, "Recipe added successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(AddNew.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }

    // Validate input fields
    private boolean isInputValid(String... inputs) {
        for (String input : inputs) {
            if (input.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Clear input fields and reset views
    private void clearFields() {
        entername.setText("");
        enterdiscription.setText("");
        enterserves.setText("");
        entertime.setText("");
        ingrediant.setText("");
        quantity.setText("");
        method.setText("");
        addPic.setImageResource(R.drawable.placeholder_image); // Placeholder image
        videoView.stopPlayback();
        videoView.setVideoURI(null);
    }
}
