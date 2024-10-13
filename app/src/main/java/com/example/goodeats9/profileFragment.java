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
import androidx.appcompat.app.AlertDialog;
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
//-----------------------------------------IM/2021/062 - Hasindu ---------------------------------------------------//
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
        ImageButton logOutButton = view.findViewById(R.id.logOutButton);
        ImageButton editProfileButton = view.findViewById(R.id.editProfileButton);
        profileImage = view.findViewById(R.id.profileImage);
        TextView userNameText = view.findViewById(R.id.userName);
        TextView userBioText = view.findViewById(R.id.userBio);

        // Get user details from SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", MODE_PRIVATE);
        String username = sharedPreferences.getString("UserName", "User");
        String description = sharedPreferences.getString("UserDescription", "Description");

        // Set the user name and bio
        userNameText.setText(username);
        userBioText.setText(description);

        // Get the current Firebase user
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            userId = currentUser.getUid();
            reference = FirebaseDatabase.getInstance().getReference("users").child(userId);

            //-----------------------------------------IM/2021/028 - Manditha ---------------------------------------------------//

            // Fetch user data from Firebase Realtime Database
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // Fetch and set the user's name
                        String name = snapshot.child("name").getValue(String.class);
                        if (name != null) {
                            userNameText.setText(name);
                        } else {
                            userNameText.setText("User");
                        }

                        // Fetch and set the user's description (default if Google sign-in)
                        String description = snapshot.child("description").getValue(String.class);
                        if (description != null && !description.isEmpty()) {
                            userBioText.setText(description);
                        } else {
                            userBioText.setText("No description provided");
                        }

                        // Load profile image
                        loadProfileImage();
                    } else {
                        // Handle case where user data is not found
                        Log.e("ProfileFragment", "User data not found in the database");
                        Toast.makeText(getActivity(), "User data not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProfileFragment", "Database error: " + error.getMessage());
                    Toast.makeText(getActivity(), "Error loading profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("ProfileFragment", "User not authenticated");
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show confirmation dialog
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton("Confirm", (dialog, which) -> {
                            // Sign out from Firebase
                            FirebaseAuth auth = FirebaseAuth.getInstance();
                            if (auth.getCurrentUser() != null) {
                                auth.signOut();

                                // Clear login state from SharedPreferences
                                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("loginDetails", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.remove("isLoggedIn");  // Only remove login-related data
                                editor.remove("UserName");
                                editor.remove("UserDescription");
                                editor.apply();

                                // Redirect to Login page and finish the current activity
                                Intent intent = new Intent(requireActivity(), login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);  // Clear activity stack
                                startActivity(intent);
                            }

                        })
                        .setNegativeButton("Cancel", (dialog, which) -> {
                            // Dismiss the dialog and do nothing
                            dialog.dismiss();
                        })
                        .show();
            }
        });
//-----------------------------------------IM/2021/028 - Manditha ---------------------------------------------------//


        // Edit profile button click listener
        editProfileButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), edit_profile.class);
            intent.putExtra("name", userNameText.getText().toString());
            intent.putExtra("description", userBioText.getText().toString());
            startActivity(intent);
        });


        /////////////////////////////////////////////////////////////////////////////////////////////



        // View recipe button click listener
        Button UpdateRecipeButton = view.findViewById(R.id.UpdateRecipeButton);
        UpdateRecipeButton.setOnClickListener(v -> startActivity(new Intent(getActivity(), MyRecipies.class)));

        return view;
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
//-----------------------------------------IM/2021/062 - Hasindu ---------------------------------------------------//