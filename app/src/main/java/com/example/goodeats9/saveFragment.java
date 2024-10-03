package com.example.goodeats9;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
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
    private List<DataClass> savedRecipesList;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_save, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        savedRecipesList = new ArrayList<>();
        adapter = new SavedRecipeAdapter(getContext(), savedRecipesList);
        recyclerView.setAdapter(adapter);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (auth.getCurrentUser() != null) {
            loadSavedRecipes();
        } else {
            Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    private void loadSavedRecipes() {
        String userId = auth.getCurrentUser().getUid();
        Log.d("saveFragment", "Loading saved recipes for user: " + userId); // Log user ID
        DatabaseReference ref = database.getReference("saved_recipes").child(userId);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                savedRecipesList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass recipe = dataSnapshot.getValue(DataClass.class);
                    if (recipe != null) {
                        savedRecipesList.add(recipe);
                    } else {
                        Log.e("DataError", "Recipe data is null for " + dataSnapshot.getKey());
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", error.getMessage());
                Toast.makeText(getContext(), "Error fetching data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
