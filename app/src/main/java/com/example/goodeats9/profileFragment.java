package com.example.goodeats9;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.storage.UploadTask;

public class profileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String userId;
    private ImageView profileImage;
    private Uri imageUri;
    private DatabaseReference reference;  // Reference to Firebase Database

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the views by ID
        ImageButton editProfileButton = view.findViewById(R.id.editProfileButton);
        profileImage = view.findViewById(R.id.profileImage);
        TextView userNameText = view.findViewById(R.id.userName);
        TextView userBioText = view.findViewById(R.id.userBio);

        // Access SharedPreferences to get user data
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", MODE_PRIVATE);
        String username = sharedPreferences.getString("UserName", "User");
        String email = sharedPreferences.getString("UserEmail", "Email");
        String description = sharedPreferences.getString("UserDescription", "Description");

        // Set the user's name and description in the TextViews
        userNameText.setText(username);
        userBioText.setText(description);

        // Get the current user's ID from Firebase Authentication
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            Log.d("ProfileFragment", "Current User ID: " + userId);

            // Initialize Firebase database reference for the user
            reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            // Fetch the profile image URL from Firebase Realtime Database
            loadProfileImage();
        } else {
            Log.e("ProfileFragment", "User is not authenticated");
            Toast.makeText(getActivity(), "User is not authenticated", Toast.LENGTH_SHORT).show();
        }

        // Handle the click event for the Edit Profile button
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), edit_profile.class);
            // Pass the user details to the edit profile activity
            intent.putExtra("name", username);
            intent.putExtra("email", email);
            intent.putExtra("description", description);
            startActivity(intent);
        });

        // Handle profile image click to upload a new one
        profileImage.setOnClickListener(v -> openImageChooser());

        // Handle the click event for the View Recipe button
        Button viewRecipeButton = view.findViewById(R.id.viewRecipeButton);
        viewRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), recipeMain.class);
            startActivity(intent);
        });

        return view;
    }

    // Open the image chooser to select a profile image
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            profileImage.setImageURI(imageUri); // Show the selected image

            // Now upload the selected image
            uploadProfileImage(imageUri, userId);
        }
    }

    /**
     * Uploads the profile image to Firebase Storage and saves the image URL to Firebase Realtime Database.
     *
     * @param imageUri The URI of the selected image.
     * @param userId   The unique ID of the current user.
     */
    private void uploadProfileImage(Uri imageUri, String userId) {
        if (imageUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("profile_images").child(userId + ".jpg");

            storageReference.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save the image URL to Firebase Realtime Database
                        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                        userRef.child("profilePhotoUrl").setValue(uri.toString());

                        Toast.makeText(getActivity(), "Profile image uploaded successfully", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e -> {
                // Handle the failure case
                Toast.makeText(getActivity(), "Profile image upload failed", Toast.LENGTH_SHORT).show();
            });
        }
    }

    /**
     * Loads the profile image from Firebase Realtime Database.
     */
    private void loadProfileImage() {
        reference.child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePhotoUrl = snapshot.getValue(String.class);
                Log.d("ProfileFragment", "Profile Photo URL: " + profilePhotoUrl);  // Log the URL for debugging

                if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                    // Load the profile image into the ImageView using Glide
                    Glide.with(profileFragment.this)
                            .load(profilePhotoUrl)
                            .placeholder(R.drawable.placeholder_image)  // Optional placeholder image
                            .error(R.drawable.error_image)  // Error placeholder
                            .circleCrop()  // Circle cropping for profile image
                            .into(profileImage);
                } else {
                    // Set a default image if no URL is found
                    profileImage.setImageResource(R.drawable.placeholder_image);
                    Toast.makeText(getActivity(), "Profile image URL is missing", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
                Log.e("ProfileFragment", "Error loading profile image: " + error.getMessage());
                profileImage.setImageResource(R.drawable.error_image);
                Toast.makeText(getActivity(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
