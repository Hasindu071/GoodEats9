package com.example.goodeats9;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide; // For loading images

import static android.content.Context.MODE_PRIVATE;

public class homeFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Access SharedPreferences to get user data
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", MODE_PRIVATE);
        String username = sharedPreferences.getString("UserName", "User"); // If no name is found, default to "User"
        String profilePhotoUrl = sharedPreferences.getString("imageUrl", ""); // Profile image URL

        // Set the welcome text with the username
        TextView welcomeTextView = view.findViewById(R.id.text_name);
        welcomeTextView.setText(username);

        // Find the ImageView for the profile image
        ImageView profileImageView = view.findViewById(R.id.profile_pic); // Make sure this matches your layout ID

        // Load the profile image using Glide
        if (profilePhotoUrl != null && !profilePhotoUrl.isEmpty()) {
            Glide.with(this)
                    .load(profilePhotoUrl)   // Load image URL
                    .placeholder(R.drawable.placeholder_image)  // Placeholder while loading
                    .error(R.drawable.error_image)  // Error image if URL is invalid
                    .circleCrop()  // Optionally, crop the image into a circle
                    .into(profileImageView);  // Load the image into the ImageView
        } else {
            // Set a default image if no URL is found
            profileImageView.setImageResource(R.drawable.placeholder_image);
        }
    }
}
