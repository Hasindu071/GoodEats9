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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class saveFragment extends Fragment implements SavedRecipeAdapter.OnRecipeDeleteListener {

    private RecyclerView recyclerView;
    private SavedRecipeAdapter adapter;
    private List<Datacls> savedRecipes; // List to hold saved recipes
    private DatabaseReference databaseReference; // Reference to Firebase database

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        // Initialize RecyclerView and savedRecipes list
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        savedRecipes = new ArrayList<>();

        // Initialize the adapter with the saved recipes list and set it to the RecyclerView
        adapter = new SavedRecipeAdapter(savedRecipes, this);
        recyclerView.setAdapter(adapter);

        // Load saved recipes from Firebase
        loadSavedRecipes();

        return view; // Return the inflated view
    }

    private void loadSavedRecipes() {
        databaseReference = FirebaseDatabase.getInstance().getReference("saved_recipes");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedRecipes.clear(); // Clear the previous recipes
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Datacls recipe = snapshot.getValue(Datacls.class);
                    if (recipe != null) {
                        savedRecipes.add(recipe); // Add each recipe to the list
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
                Toast.makeText(getContext(), "Failed to load recipes", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRecipeDelete(Datacls recipe) {
        // Remove recipe from savedRecipes list
        savedRecipes.remove(recipe);

        // Optionally, remove recipe from Firebase as well
        databaseReference.child(recipe.getId()).removeValue();

        // Notify adapter about the change
        adapter.notifyDataSetChanged();

        // Show a confirmation message
        Toast.makeText(getContext(), "Recipe deleted", Toast.LENGTH_SHORT).show();
    }
}
