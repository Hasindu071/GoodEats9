package com.example.goodeats9;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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
    TextView signupRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.Email);
        loginPassword = findViewById(R.id.Password);
        signupRedirectText = findViewById(R.id.textViewSignUp);
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

        //Get the text of sign up by its ID
        TextView textViewSignUp = findViewById(R.id.textViewSignUp);
        //Make the "sign up" text look like a link
        textViewSignUp.setPaintFlags(textViewSignUp.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // Handle Sign Up TextView click
        textViewSignUp.setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(login.this, sign_up.class);
            startActivity(intent);
        });

        //Get the text of sign up by its ID
        TextView forgotpassword = findViewById(R.id.textViewForgotPassword);
        //Make the "sign up" text look like a link
        forgotpassword.setPaintFlags(forgotpassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        // Handle Sign Up TextView click
        forgotpassword.setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(login.this, Forget_Password.class);
            startActivity(intent);
        });
    }

    public boolean validateEmail(){
        String val = loginEmail.getText().toString();
        if (val.isEmpty()){
            loginEmail.setError("Email cannot be empty!");
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
            return false;
        }else {
            loginPassword.setError(null);
            return true;
        }
    }

    public void checkEmail() {
        String userEmail = loginEmail.getText().toString().trim();
        String userPassword = loginPassword.getText().toString().trim();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users");
        Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail);

        checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    loginEmail.setError(null);

                    // Assuming the "email" field is unique and you get one result.
                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordFromDB, userPassword)) {
                            loginPassword.setError(null);

                            // Successful login, redirect to MainActivity
                            Intent intent = new Intent(login.this, menu_bar_main.class);
                            startActivity(intent);
                            finish(); // Call finish to close the current login activity
                        } else {
                            loginPassword.setError("Invalid Credentials!");
                            loginPassword.requestFocus();
                        }
                    }
                } else {
                    loginEmail.setError("Email does not exist!");
                    loginEmail.requestFocus();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors here
            }
        });
    }
}
