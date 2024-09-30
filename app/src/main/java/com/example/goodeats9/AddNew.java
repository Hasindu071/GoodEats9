package com.example.goodeats9;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.circleCrop;

import android.content.Intent;
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

    private ImageView addPic, addI, addM, backbtn;
    private VideoView videoView;
    private TextView headline, hintText, hintText2;
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
        hintText = findViewById(R.id.hintText);
        hintText2 = findViewById(R.id.hintText2);

        // Back button functionality
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Select image for the recipe
        addPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageChooser();
            }
        });

        // Select video for the recipe
        view_video_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoChooser();
            }
        });

        // Handle adding recipe button click
        addrecipebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRecipe();
            }
        });
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
            addPic.setImageURI(imageUri);
            hintText.setVisibility(View.GONE);
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.start();
            hintText2.setVisibility(View.GONE);
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
        if (name.isEmpty() || description.isEmpty() || serves.isEmpty() || cookTime.isEmpty() || ingredientName.isEmpty() || ingredientQuantity.isEmpty() || methodDescription.isEmpty()) {
            Toast.makeText(AddNew.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // TODO: Save recipe details to Firebase or other storage

        Toast.makeText(AddNew.this, "Recipe added successfully!", Toast.LENGTH_SHORT).show();

        // Clear fields
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
