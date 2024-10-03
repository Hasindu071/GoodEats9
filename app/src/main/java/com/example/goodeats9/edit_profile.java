package com.example.goodeats9;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class edit_profile extends AppCompatActivity {

    EditText editName, editDescription;
    Button saveButton;
    String nameUser, descriptionUser;
    DatabaseReference reference;
    FirebaseUser currentUser;
    String userId;
    ImageView profilePhoto;

    // Variable to hold selected image URI
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Get current user from FirebaseAuth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Use the UID as a unique identifier for the user

        } else {
            Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
            finish();  // Close activity if no user is logged in
        }

        // Initialize Firebase Database reference for the user
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Initialize EditTexts and Button
        editName = findViewById(R.id.editName);
        editDescription = findViewById(R.id.editDescription); // Add this line
        saveButton = findViewById(R.id.buttonUpdate);

        // Initialize ImageView for profile photo
        profilePhoto = findViewById(R.id.propic);

        // Initialize Back Button
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> finish());  // Close activity

        // Initialize Camera Icon Button
        ImageView cameraIcon = findViewById(R.id.camera_icon);
        cameraIcon.setOnClickListener(v -> openImageChooser());

        // Load data into fields
        showData();

        // Load profile photo from Firebase
        loadProfilePhoto();

        // Handle save button click
        saveButton.setOnClickListener(view -> {
            boolean isUpdated = false;
            if (isNameChanged()) isUpdated = true;
            if (isDescriptionChanged()) isUpdated = true;

            if (isUpdated) {
                Toast.makeText(edit_profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                this.finish();

                if (selectedImageUri != null) {
                    uploadProfileImage(selectedImageUri, userId); // Upload new profile image if selected
                }
            } else {
                Toast.makeText(edit_profile.this, "No changes detected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Open image chooser to select a profile image
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();  // Get the selected image URI
            profilePhoto.setImageURI(selectedImageUri); // Set image in ImageView
        }
    }

    // Show existing data in EditTexts
    private void showData() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    nameUser = dataSnapshot.child("name").getValue(String.class);
                    descriptionUser = dataSnapshot.child("description").getValue(String.class);

                    editName.setText(nameUser);
                    editDescription.setText(descriptionUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(edit_profile.this, "Failed to retrieve user data.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Load profile photo from Firebase Database
    private void loadProfilePhoto() {
        reference.child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String profilePhotoUrl = snapshot.getValue(String.class);
                if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                    Glide.with(edit_profile.this)
                            .load(profilePhotoUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .circleCrop()
                            .into(profilePhoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(edit_profile.this, "Failed to load profile photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Upload new profile image to Firebase Storage
    private void uploadProfileImage(Uri imageUri, String userId) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId + ".jpg");

            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save image URL to Firebase Database
                        reference.child("profilePhotoUrl").setValue(uri.toString());
                        Toast.makeText(edit_profile.this, "Profile image updated successfully", Toast.LENGTH_SHORT).show();
                        loadProfilePhoto(); // Reload profile image after update
                    })
            ).addOnFailureListener(e -> {
                Toast.makeText(edit_profile.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Check if name has changed
    private boolean isNameChanged() {
        String newName = editName.getText().toString().trim();
        if (!newName.equals(nameUser)) {
            reference.child("name").setValue(newName);
            nameUser = newName;  // Update local variable
            return true;
        }
        return false;
    }




    // Check if description has changed
    private boolean isDescriptionChanged() {
        String newDescription = editDescription.getText().toString().trim();
        if (!newDescription.equals(descriptionUser)) {
            reference.child("description").setValue(newDescription);
            descriptionUser = newDescription;  // Update local variable
            return true;
        }
        return false;
    }
}
