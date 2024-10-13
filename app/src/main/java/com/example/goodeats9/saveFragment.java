package com.example.goodeats9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//

public class saveFragment extends Fragment {

    private RecyclerView recyclerView;
    private SavedRecipeAdapter adapter;
    private List<Datacls> savedRecipeList;

    private DatabaseReference savedRecipesRef;
    private FirebaseAuth auth;
    private FirebaseUser currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        // Initialize Firebase auth and user
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        savedRecipeList = new ArrayList<>();
        adapter = new SavedRecipeAdapter(getContext(), savedRecipeList);
        recyclerView.setAdapter(adapter);

        if (currentUser != null) {
            loadSavedRecipes();
        } else {
            Toast.makeText(getContext(), "Please log in to see saved recipes.", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadSavedRecipes() {
        String userId = currentUser.getUid();
        savedRecipesRef = FirebaseDatabase.getInstance().getReference("saved_recipes").child(userId);

        savedRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedRecipeList.clear(); // Clear the list before adding new data
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Get the unique recipe ID (key)
                    String recipeId = snapshot.getKey();

                    // Get other recipe details from Firebase
                    String videoUri = snapshot.child("videoUri").getValue(String.class);
                    String name = snapshot.child("name").getValue(String.class);
                    String description = snapshot.child("description").getValue(String.class);
                    String userName = snapshot.child("userName").getValue(String.class);
                    String imageUri = snapshot.child("imageUri").getValue(String.class);

                    // Create a Datacls object with recipeId
                    Datacls recipe = new Datacls(recipeId, videoUri, name, description, userName, imageUri);
                    savedRecipeList.add(recipe);
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load saved recipes.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
//------------------------------------------------IM/2021/003 - Dulmi --------------------------------------------------//