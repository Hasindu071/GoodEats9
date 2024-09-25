package com.example.goodeats9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

    // Firebase Realtime Database reference
    private DatabaseReference databaseReference;

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

        // Initialize Firebase Realtime Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

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
            // Get the email from SharedPreferences
            SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
            String userEmail = sharedPreferences.getString("UserEmail", null); // Default to null if not found

            if (userEmail != null) {
                // Replace special characters in the email (e.g., '.', '@') to avoid issues with Firebase paths
                String sanitizedEmail = userEmail.replace(".", "_").replace("@", "_");

                // First, check if the user already has a photo stored in Firebase
                databaseReference.child(sanitizedEmail).child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // User already has a photo, delete the previous one
                            String oldPhotoUrl = snapshot.getValue(String.class);
                            if (oldPhotoUrl != null) {
                                StorageReference oldPhotoRef = FirebaseStorage.getInstance().getReferenceFromUrl(oldPhotoUrl);
                                oldPhotoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // Successfully deleted old photo, now upload new one
                                        uploadNewPhoto(sanitizedEmail);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(Exception e) {
                                        Toast.makeText(Add_photo.this, "Failed to delete old photo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                // No old photo URL, just upload the new one
                                uploadNewPhoto(sanitizedEmail);
                            }
                        } else {
                            // No old photo URL, just upload the new one
                            uploadNewPhoto(sanitizedEmail);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(Add_photo.this, "Error checking previous photo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(this, "Error: User email is null", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadNewPhoto(String sanitizedEmail) {
        // Define the path using the user's email
        StorageReference mailImage = storageReference.child(sanitizedEmail + "/" + System.currentTimeMillis() + ".jpg");

        // Upload the file
        mailImage.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded photo
                        mailImage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Save the new photo URL in the database
                                databaseReference.child(sanitizedEmail).child("profilePhotoUrl").setValue(downloadUri.toString());
                                Toast.makeText(Add_photo.this, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show();
                                // Optionally, you can finish the activity after upload
                                finish();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(Add_photo.this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
