package com.example.goodeats9;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.goodeats9.databinding.ActivityMenuBarBinding;

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
            } else if (item.getItemId() == R.id.save) {
                replaceFragment(new saveFragment());
            } else if (item.getItemId() == R.id.notification) {
                replaceFragment(new notificationFragment());
            } else if (item.getItemId() == R.id.profile) {
                replaceFragment(new profileFragment());
            }
            return true;
        });

    }



    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout , fragment);
        fragmentTransaction.commit();
    }
}