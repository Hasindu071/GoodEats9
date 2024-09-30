package com.example.goodeats9;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button signupButton;
    TextView signupRedirectText, forgotPasswordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.Email);
        loginPassword = findViewById(R.id.Password);
        signupRedirectText = findViewById(R.id.textViewSignUp);
        forgotPasswordText = findViewById(R.id.textViewForgotPassword);
        signupButton = findViewById(R.id.buttonSignIN);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() | !validatePassword()) {

                }else{
                    checkEmail();
                }
            }
        });  // Make sure to use the correct layout file

        signupRedirectText.setPaintFlags(signupRedirectText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, sign_up.class);
                startActivity(intent);
            }
        });

        forgotPasswordText.setPaintFlags(forgotPasswordText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, Forget_Password.class);
                startActivity(intent);
            }
        });

    }

    public boolean validateEmail(){
        String val = loginEmail.getText().toString();
        if (val.isEmpty()){
            loginEmail.setError("Email cannot be empty!");
            loginEmail.setBackgroundResource(R.drawable.input_error);
            return false;
        }else {
            loginEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword(){
        String val = loginPassword.getText().toString();
        if (val.isEmpty()){
            loginPassword.setError("Password cannot be empty!");
            loginPassword.setBackgroundResource(R.drawable.input_error);
            return false;
        }else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkEmail() {
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        // Firebase Authentication instance
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Authenticate the user using Firebase Authentication
        mAuth.signInWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Get the currently logged-in user's UID
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            String userID = currentUser.getUid();

                            // Now fetch the user's data from the Firebase Realtime Database
                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(userID);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        String nameFromDB = snapshot.child("name").getValue(String.class);
                                        String descFromDB = snapshot.child("description").getValue(String.class);

                                        // Save user's name and description in SharedPreferences
                                        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("UserName", nameFromDB);  // Store the username
                                        editor.putString("UserDescription", descFromDB);  // Store the description
                                        editor.apply();

                                        // Successful login, redirect to MainActivity
                                        Intent intent = new Intent(login.this, menu_bar_main.class);
                                        startActivity(intent);
                                        finish(); // Close the login activity
                                    } else {
                                        // Handle case where user data does not exist in Realtime Database
                                        loginEmail.setError("User data not found!");
                                        loginEmail.setBackgroundResource(R.drawable.input_error);
                                        loginEmail.requestFocus();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    // Handle possible errors from Firebase
                                    Toast.makeText(login.this, "Database error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        // Handle authentication failure
                        loginPassword.setError("Invalid Credentials!");
                        loginEmail.setBackgroundResource(R.drawable.input_error);
                        loginPassword.requestFocus();
                    }
                });
    }
    }

