package com.example.goodeats9;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast; // Import Toast for error handling

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class searchFragment extends Fragment {

    private GridView gridView;
    private ArrayList<DataClass> dataList;
    private AdaptorSearch adapter;
    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        // Initialize UI components
        gridView = view.findViewById(R.id.gridView);
        dataList = new ArrayList<>();
        adapter = new AdaptorSearch(getContext(), dataList); // Set adapter with the context and data
        gridView.setAdapter(adapter);

        // Firebase database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes");

        // Fetch data from Firebase
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userEmail = currentUser.getEmail();
        String formattedEmail = userEmail.replace(".", "_");
        fetchImageData(formattedEmail);

        return view;
    }

    private void fetchImageData(String formattedEmail) {
        // Reference the user's recipes node in Firebase
        DatabaseReference userRecipesRef = databaseReference.child(formattedEmail);

        // Listen for changes in the user's recipes node
        userRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Clear the list to avoid duplicate entries
                dataList.clear();

                // Iterate through each recipe in the user's recipes node
                for (DataSnapshot recipeSnapshot : snapshot.getChildren()) {
                    String imageUri = recipeSnapshot.child("imageUri").getValue(String.class); // Get the imageUri
                    String name = recipeSnapshot.child("name").getValue(String.class); // Get the name

                    if (imageUri != null && name != null) {
                        DataClass dataClass = new DataClass(imageUri, name); // Create a new DataClass object
                        dataList.add(dataClass);  // Add the recipe data to the list
                    }
                }

                // Notify the adapter that data has changed to update the UI
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle any errors
                Toast.makeText(getContext(), "Failed to load images: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
