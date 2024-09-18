package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class sign_up extends AppCompatActivity {

    EditText signUpName , signUpEmail , signUpUserName , signUpPassword;
    Button signUpButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        signUpName = findViewById(R.id.inputName);
        signUpEmail = findViewById(R.id.inputEmail);
        signUpUserName = findViewById(R.id.inputUserName);
        signUpPassword= findViewById(R.id.inputPassword);
        signUpName = findViewById(R.id.inputConfirmPassword);
        signUpButton = findViewById(R.id.buttonSignUp);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database = FirebaseDatabase.getInstance();
                reference=database.getReference("users");

                String name = signUpName.getText().toString();
                String email = signUpEmail.getText().toString();
                String userName = signUpUserName.getText().toString();
                String Password = signUpPassword.getText().toString();

                helperClass helperClass = new helperClass(name, email , Password , userName);
                reference.child(name).setValue(helperClass);

                Toast.makeText(sign_up.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(sign_up.this , login.class);
                startActivity(intent);
            }
        });



    };

}