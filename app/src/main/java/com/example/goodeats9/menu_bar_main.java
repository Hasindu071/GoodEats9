package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goodeats9.databinding.ActivityMenuBarBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class menu_bar_main extends AppCompatActivity {

    ActivityMenuBarBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMenuBarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        replaceFragment(new homeFragment());
        binding.bottomNavigationView.setBackground(null);

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.home) {
                replaceFragment(new homeFragment());
            } else if (item.getItemId() == R.id.search) {
                replaceFragment(new searchFragment());
            } else if (item.getItemId() == R.id.save) {
                replaceFragment(new saveFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new profileFragment());
            }
            return true;
        });
        // Initialize FAB and set click listener
        FloatingActionButton fab = findViewById(R.id.addNewItem);
        fab.setOnClickListener(v -> {
            // Navigate to AddNew activity when FAB is clicked
            Intent intent = new Intent(menu_bar_main.this, AddNew.class);
            startActivity(intent);
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout , fragment);
        fragmentTransaction.commit();
    }

}