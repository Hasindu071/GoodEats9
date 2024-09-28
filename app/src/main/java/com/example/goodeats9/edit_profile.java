package com.example.goodeats9;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
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

public class edit_profile extends AppCompatActivity {

    EditText editName, editEmail, editCurrentPassword, editNewPassword, editDescription;
    Button saveButton;
    String nameUser, emailUser, currentPasswordUser, descriptionUser;
    DatabaseReference reference;
    FirebaseUser currentUser;
    String userId;
    ImageView profilePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Get current user from FirebaseAuth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();  // Use the UID as a unique identifier for the user
            emailUser = currentUser.getEmail();
        } else {
            Toast.makeText(this, "No authenticated user found", Toast.LENGTH_SHORT).show();
            finish();  // Close activity if no user is logged in
        }

        // Initialize Firebase Database reference for the user
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Initialize EditTexts and Button
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editDescription = findViewById(R.id.editDescription);
        saveButton = findViewById(R.id.buttonUpdate);

        // Initialize ImageView for profile photo
        profilePhoto = findViewById(R.id.propic);

        // Load data into fields
        showData();

        // Load profile photo from Firebase
        loadProfilePhoto();

        // Handle save button click
        saveButton.setOnClickListener(view -> {
            if (isNameChanged() || isEmailChanged() || isPasswordChanged() || isDescriptionChanged()) {
                Toast.makeText(edit_profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(edit_profile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load user data into fields
    public void showData() {
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    nameUser = snapshot.child("name").getValue(String.class);
                    descriptionUser = snapshot.child("description").getValue(String.class);
                    currentPasswordUser = snapshot.child("password").getValue(String.class);

                    editName.setText(nameUser);
                    editDescription.setText(descriptionUser);
                    editEmail.setText(emailUser);  // Use the current email from FirebaseAuth
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(edit_profile.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load profile photo from Firebase
    private void loadProfilePhoto() {
        reference.child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profilePhotoUrl = snapshot.getValue(String.class);
                    if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                        Glide.with(edit_profile.this)
                                .load(profilePhotoUrl)
                                .placeholder(R.drawable.placeholder_image)  // Optional placeholder while loading
                                .error(R.drawable.error_image)              // Optional error if failed
                                .circleCrop()
                                .into(profilePhoto);
                    }
                } else {
                    Toast.makeText(edit_profile.this, "Profile photo not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(edit_profile.this, "Failed to load profile photo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Check if the name has changed
    private boolean isNameChanged() {
        if (!nameUser.equals(editName.getText().toString())) {
            reference.child("name").setValue(editName.getText().toString());
            nameUser = editName.getText().toString();  // Update local variable
            return true;
        }
        return false;
    }

    // Check if the email has changed (not directly in the database but through Firebase Auth)
    private boolean isEmailChanged() {
        if (!emailUser.equals(editEmail.getText().toString())) {
            currentUser.updateEmail(editEmail.getText().toString()).addOnSuccessListener(aVoid -> {
                reference.child("email").setValue(editEmail.getText().toString());
                emailUser = editEmail.getText().toString();  // Update local variable
            }).addOnFailureListener(e -> {
                Toast.makeText(edit_profile.this, "Email update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
            return true;
        }
        return false;
    }

    // Check if the password has changed
    private boolean isPasswordChanged() {
        if (!editNewPassword.getText().toString().isEmpty() && !editCurrentPassword.getText().toString().isEmpty()) {
            // Validate the current password
            if (!currentPasswordUser.equals(editCurrentPassword.getText().toString())) {
                Toast.makeText(this, "Current password is incorrect", Toast.LENGTH_SHORT).show();
                return false;
            }

            // Update the password in Firebase Authentication and Database
            currentUser.updatePassword(editNewPassword.getText().toString()).addOnSuccessListener(aVoid -> {
                reference.child("password").setValue(editNewPassword.getText().toString());
                currentPasswordUser = editNewPassword.getText().toString();  // Update local variable
                Toast.makeText(edit_profile.this, "Password updated", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(edit_profile.this, "Password update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
            return true;
        }
        return false;
    }

    // Check if the description has changed
    private boolean isDescriptionChanged() {
        if (!descriptionUser.equals(editDescription.getText().toString())) {
            reference.child("description").setValue(editDescription.getText().toString());
            descriptionUser = editDescription.getText().toString();  // Update local variable
            return true;
        }
        return false;
    }
}
