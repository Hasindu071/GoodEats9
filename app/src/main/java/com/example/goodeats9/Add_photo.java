package com.example.goodeats9; // Update with your actual package name

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Add_photo extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profilePhoto;
    private Button buttonSave;
    private Button buttonCancel;
    private TextView hint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo); // Update to your actual layout name

        profilePhoto = findViewById(R.id.profile_pic);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        hint = findViewById(R.id.hintText);


        // Set onClickListener for the image view to select an image
        profilePhoto.setOnClickListener(v -> openFileChooser());

        // Set onClickListener for the Cancel button
        buttonCancel.setOnClickListener(v -> finish());

        // Set onClickListener for the Save button
        buttonSave.setOnClickListener(v -> savePhoto());
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            hint.setVisibility(View.GONE);
            profilePhoto.setImageURI(imageUri);
            // You can save the URI for further processing
        }
    }

    private void savePhoto() {
        // Implement your save logic here
        // For example, upload the image to a server or save to local storage
        Toast.makeText(this, "Photo saved!", Toast.LENGTH_SHORT).show();
        // Optionally, you can finish the activity or navigate elsewhere
        finish();
    }
}
