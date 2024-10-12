package com.example.goodeats9;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import android.content.Context;

import java.util.ArrayList;
import java.util.Objects;

public class homeFragment extends Fragment {

    private DatabaseReference reference; // Firebase reference
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get current user from FirebaseAuth
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(requireContext(), "No authenticated user found", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();

        // Initialize Firebase reference for the user data
        reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

        // Find the ImageView for the profile image
        ImageView profileImageView = view.findViewById(R.id.profile_pic);
        TextView welcomeTextView = view.findViewById(R.id.text_name);

        // Check if the user is signed in with Google
        if (currentUser.getDisplayName() != null) {
            // Google Sign-In User: Display name and profile picture from FirebaseUser
            String googleName = currentUser.getDisplayName();
            Uri googlePhotoUri = currentUser.getPhotoUrl();

            // Set the welcome text with the Google username
            welcomeTextView.setText(googleName);

            // Load Google profile photo using Glide if photo URL is not null
            if (googlePhotoUri != null) {
                Glide.with(this)
                        .load(googlePhotoUri.toString())
                        .placeholder(R.drawable.placeholder_image)
                        .error(R.drawable.error_image)
                        .circleCrop()
                        .into(profileImageView);
            }
        } else {
            // Normal Sign-In: Access SharedPreferences to get user data
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", Context.MODE_PRIVATE);
            String username = sharedPreferences.getString("UserName", "User"); // If no name is found, default to "User"

            // Set the welcome text with the username
            welcomeTextView.setText(username);

            // Load profile photo from Firebase for normal sign-in
            loadProfileImage(profileImageView);
        }

        // Configure the ImageSlider
        configureImageSlider(view);
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
                Log.e("homeFragment", "Image upload failed: " + e.getMessage());
                Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
            });
        }
    }

    // Load profile image from Firebase Database
    private void loadProfileImage(ImageView profileImageView) {
        reference.child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePhotoUrl = snapshot.getValue(String.class);

                if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                    Glide.with(homeFragment.this)
                            .load(profilePhotoUrl)
                            .placeholder(R.drawable.placeholder_image)
                            .error(R.drawable.error_image)
                            .circleCrop()
                            .into(profileImageView);
                } else {
                    profileImageView.setImageResource(R.drawable.placeholder_image);
                    Toast.makeText(getActivity(), "No profile image available", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("homeFragment", "Error loading profile image: " + error.getMessage());
                profileImageView.setImageResource(R.drawable.error_image);
                Toast.makeText(getActivity(), "Failed to load profile image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Method to configure ImageSlider and add images
    private void configureImageSlider(View view) {
        ImageSlider imageSlider = view.findViewById(R.id.imageSlider); // Use the view to find the ImageSlider

        // Create a list for slide models
        ArrayList<SlideModel> slideModels = new ArrayList<>();

        // Adding local drawable images
        slideModels.add(new SlideModel(R.drawable.image1, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image2, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image3, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image4, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image5, ScaleTypes.FIT));
        slideModels.add(new SlideModel(R.drawable.image6, ScaleTypes.FIT));

        // Set the image list to the slider
        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
    }
}
