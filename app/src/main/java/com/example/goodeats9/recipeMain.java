package com.example.goodeats9;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button procedureButton;
    private Button ingredientsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find buttons by their ID
        procedureButton = findViewById(R.id.procedureButton);
        ingredientsButton = findViewById(R.id.ingredientsButton);

        // Load the default fragment (ProcedureFragment) into the container
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new ProcedureFragment())
                .commit();

        // Handle Procedure Button Click
        procedureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ProcedureFragment())
                        .commit();
            }
        });

        // Handle Ingredients Button Click
        ingredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new IngredientsFragment())
                        .commit();
            }
        });
    }
}