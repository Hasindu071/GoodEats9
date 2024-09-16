package com.example.goodeats9;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.goodeats9.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");

        // Write a message to the database
        myRef.setValue("Hello, firebase!");
    }
}
