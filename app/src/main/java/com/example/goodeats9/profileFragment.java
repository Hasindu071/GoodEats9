package com.example.goodeats9;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
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

public class profileFragment extends Fragment {

    private String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Find the views by ID
        ImageButton editProfileButton = view.findViewById(R.id.editProfileButton);
        ImageView profileImage = view.findViewById(R.id.profileImage);
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

            // Fetch the profile image URL from Firebase Realtime Database
            loadProfileImage(userId, profileImage);
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

        // Handle the click event for the View Recipe button
        Button viewRecipeButton = view.findViewById(R.id.viewRecipeButton);
        viewRecipeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), recipeMain.class);
            startActivity(intent);
        });

        return view;
    }

    /**
     * Loads the profile image from Firebase Realtime Database.
     *
     * @param userId The unique ID of the current user.
     * @param profileImage The ImageView where the profile image will be loaded.
     */
    private void loadProfileImage(String userId, ImageView profileImage) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
        userRef.child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String profilePhotoUrl = snapshot.getValue(String.class);
                Log.d("ProfileFragment", "Profile Photo URL: " + profilePhotoUrl);  // Log the URL for debugging

                // Check if the URL is valid and not empty
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
