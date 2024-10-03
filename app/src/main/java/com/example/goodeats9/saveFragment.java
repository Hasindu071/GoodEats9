package com.example.goodeats9;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

public class saveFragment extends Fragment {

    private RecyclerView recyclerView;
    private SavedRecipeAdapter adapter;
    private List<Datacls> savedRecipesList; // Use Datacls for saved recipes
    private FirebaseAuth auth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        savedRecipesList = new ArrayList<>();
        adapter = new SavedRecipeAdapter(getContext(), savedRecipesList);
        recyclerView.setAdapter(adapter);

        // Load saved recipes
        loadSavedRecipes();

        return view;
    }

    private void loadSavedRecipes() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Toast.makeText(getContext(), "Please log in to view saved recipes.", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = user.getUid(); // Get the current user's ID
        DatabaseReference savedRecipesRef = FirebaseDatabase.getInstance().getReference("saved_recipes").child(userId);
        savedRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                savedRecipesList.clear(); // Clear the list before adding new items
                for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                    Datacls recipe = recipeSnapshot.getValue(Datacls.class); // Get Datacls object
                    if (recipe != null) {
                        savedRecipesList.add(recipe); // Add recipe to the list
                    }
                }
                adapter.notifyDataSetChanged(); // Notify adapter of data change
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to load saved recipes: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
