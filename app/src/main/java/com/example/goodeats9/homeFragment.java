package com.example.goodeats9;

import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static android.content.Context.MODE_PRIVATE;

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

        // Access SharedPreferences to get user data
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", MODE_PRIVATE);
        String username = sharedPreferences.getString("UserName", "User"); // If no name is found, default to "User"

        // Set the welcome text with the username
        TextView welcomeTextView = view.findViewById(R.id.text_name);
        welcomeTextView.setText(username);

        // Find the ImageView for the profile image
        ImageView profileImageView = view.findViewById(R.id.profile_pic);

        // Load profile photo from Firebase
        loadProfilePhoto(profileImageView);
    }

    // Method to load profile photo from Firebase using UID
    private void loadProfilePhoto(ImageView profileImageView) {
        reference.child("profilePhotoUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String profilePhotoUrl = snapshot.getValue(String.class);
                    if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
                        // Load the profile photo using Glide
                        Glide.with(homeFragment.this)
                                .load(profilePhotoUrl)
                                .placeholder(R.drawable.placeholder_image)  // Optional placeholder while loading
                                .error(R.drawable.error_image)              // Optional error image if failed
                                .circleCrop()                               // Optional circular crop
                                .into(profileImageView);
                    } else {
                        // Set default image if profile photo URL is empty
                        profileImageView.setImageResource(R.drawable.placeholder_image);
                    }
                } else {
                    // Set default image if no photo is found in the database
                    profileImageView.setImageResource(R.drawable.placeholder_image);
                    Toast.makeText(requireContext(), "Profile photo not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load profile photo: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
