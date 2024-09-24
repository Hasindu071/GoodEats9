package com.example.goodeats9;

import android.content.Intent;
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

import com.example.goodeats9.databinding.ActivityAddPhotoBinding;  // Ensure your XML name matches here
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Add_photo extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profilePhoto;
    private Button buttonSave;
    private Button buttonCancel;
    private TextView hint;
    private Uri imageUri;

    // Firebase Storage reference
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_photo); // Update to your actual layout name

        profilePhoto = findViewById(R.id.profile_pic);
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        hint = findViewById(R.id.hintText);

        // Initialize Firebase Storage reference
        storageReference = FirebaseStorage.getInstance().getReference("profile_pictures");

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
            imageUri = data.getData();
            hint.setVisibility(View.GONE);
            profilePhoto.setImageURI(imageUri);
        }
    }

    private void savePhoto() {
        if (imageUri != null) {
            // Define a unique name for the uploaded image
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

            // Upload the file
            fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(Add_photo.this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                            // Optionally, you can finish the activity after upload
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(Add_photo.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }
}
