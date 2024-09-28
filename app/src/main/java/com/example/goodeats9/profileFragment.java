package com.example.goodeats9;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

public class profileFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1;
    private String userId;
    private ImageView profileImage;
    private Uri imageUri;
    private DatabaseReference reference;
    private FirebaseAuth auth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Find views by ID
        ImageButton editProfileButton = view.findViewById(R.id.editProfileButton);
        profileImage = view.findViewById(R.id.profileImage);
        TextView userNameText = view.findViewById(R.id.userName);
        TextView userBioText = view.findViewById(R.id.userBio);

        // Get user details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", MODE_PRIVATE);
        String username = sharedPreferences.getString("UserName", "User");
        String email = sharedPreferences.getString("UserEmail", "Email");
        String description = sharedPreferences.getString("UserDescription", "Description");

        // Set the user name and bio
        userNameText.setText(username);
        userBioText.setText(description);

        // Get the current Firebase user
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference("users").child(userId);
            loadProfileImage(); // Fetch profile image from Firebase
        } else {
            Log.e("ProfileFragment", "User not authenticated");
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        // Edit profile button click listener
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), edit_profile.class);
            intent.putExtra("name", username);
            intent.putExtra("email", email);
            intent.putExtra("description", description);
            startActivity(intent);
        });

        // Profile image click listener to open image chooser
        profileImage.setOnClickListener(v -> openImageChooser());

        // View recipe button click listener
        Button viewRecipeButton = view.findViewById(R.id.viewRecipeButton);
        viewRecipeButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), recipeMain.class)));

        return view;
    }

    // Open image chooser to select a profile image
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
            profileImage.setImageURI(imageUri); // Temporarily display the selected image
            uploadProfileImage(imageUri, userId); // Upload the selected image to Firebase
        }
    }

    // Upload profile image to Firebase Storage and save its URL in Realtime Database
    private void uploadProfileImage(Uri imageUri, String userId) {
        if (imageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference("profile_images").child(userId + ".jpg");

            storageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Save image URL to Realtime Database
                        reference.child("profilePhotoUrl").setValue(uri.toString());
                        Toast.makeText(getActivity(), "Profile image uploaded successfully", Toast.LENGTH_SHORT).show();
                    })
            ).addOnFailureListener(e -> {
                Log.e("ProfileFragment", "Image upload failed: " + e.getMessage());
                Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Load profile image from Firebase Database
    private void loadProfileImage() {
        reference.child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePhotoUrl = snapshot.getValue(String.class);

                if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                    Glide.with(profileFragment.this)
                            .load(profilePhotoUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .circleCrop()
                            .into(profileImage);
                } else {
                    profileImage.setImageResource(R.drawable.placeholder_image);
                    Toast.makeText(getActivity(), "No profile image available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileFragment", "Error loading profile image: " + error.getMessage());
                profileImage.setImageResource(R.drawable.error_image);
                Toast.makeText(getActivity(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
