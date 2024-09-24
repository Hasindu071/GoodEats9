package com.example.goodeats9;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class edit_profile extends AppCompatActivity {

    EditText editName, editEmail, editCurrentPassword, editNewPassword, editDescription;
    Button saveButton;
    String nameUser, emailUser,  currentPasswordUser, descriptionUser;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Initialize camera button and set click listener
        ImageView propic = findViewById(R.id.camera_icon);
        propic.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profile.this, Add_photo.class);
            startActivity(intent);
        });

        // Initialize back button and set click listener
        ImageView back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> {
            Intent intent = new Intent(edit_profile.this, profileFragment.class);
            this.finish();
        });

        // Initialize Firebase reference
        reference = FirebaseDatabase.getInstance().getReference("users");

        // Initialize EditTexts and Button
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editCurrentPassword = findViewById(R.id.editCurrentPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editDescription = findViewById(R.id.editDescription);
        saveButton = findViewById(R.id.buttonUpdate);

        // Load data into fields
        showData();

        // Handle save button click
        saveButton.setOnClickListener(view -> {
            if (isNameChanged() || isEmailChanged() || isPasswordChanged()) {
                Toast.makeText(edit_profile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(edit_profile.this, "No Changes Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to load user data into fields
    public void showData() {
        Intent intent = getIntent();

        nameUser = intent.getStringExtra("name");
        emailUser = intent.getStringExtra("email");
        descriptionUser = intent.getStringExtra("description");

        editName.setText(nameUser);
        editEmail.setText(emailUser);
        editDescription.setText(descriptionUser);
    }

    // Check if the name has changed
    private boolean isNameChanged() {
        if (!nameUser.equals(editName.getText().toString())) {
            reference.child(nameUser).child("name").setValue(editName.getText().toString());
            nameUser = editName.getText().toString(); // Update local variable
            return true;
        } else {
            return false;
        }
    }

    // Check if the email has changed
    private boolean isEmailChanged() {
        if (!emailUser.equals(editEmail.getText().toString())) {
            reference.child(nameUser).child("email").setValue(editEmail.getText().toString());
            emailUser = editEmail.getText().toString(); // Update local variable
            return true;
        } else {
            return false;
        }
    }

    // Check if the password has changed
    private boolean isPasswordChanged() {
        // Check if a new password is provided
        if (!editNewPassword.getText().toString().isEmpty()) {

            // First, validate the current password
            if (!currentPasswordUser.equals(editCurrentPassword.getText().toString())) {
                // Current password does not match, so show error
                Toast.makeText(edit_profile.this, "Incorrect current password", Toast.LENGTH_SHORT).show();
                return false;
            }

// If the current password matches, update to the new password
            reference.child(nameUser).child("password").setValue(editNewPassword.getText().toString());
            currentPasswordUser = editNewPassword.getText().toString(); // Update local variable
            Toast.makeText(edit_profile.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
            return true;

        } else {
            // No new password was provided, so no change
            return false;
        }
    }
}