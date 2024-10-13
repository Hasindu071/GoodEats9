package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//

public class forget_password_2 extends AppCompatActivity {

    private EditText editTextNumberDecimal1, editTextNumberDecimal2, editTextNumberDecimal3, editTextNumberDecimal4, editTextNumberDecimal5;
    private Button buttonVerify;
    private TextView textViewResendEmail;
    private ImageButton exitButton;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password2);

        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("ResetCodes");

        // Initialize views
        editTextNumberDecimal1 = findViewById(R.id.editTextNumberDecimal1);
        editTextNumberDecimal2 = findViewById(R.id.editTextNumberDecimal2);
        editTextNumberDecimal3 = findViewById(R.id.editTextNumberDecimal3);
        editTextNumberDecimal4 = findViewById(R.id.editTextNumberDecimal4);
        editTextNumberDecimal5 = findViewById(R.id.editTextNumberDecimal5);
        buttonVerify = findViewById(R.id.buttonVerify);
        textViewResendEmail = findViewById(R.id.textView3);
        exitButton = findViewById(R.id.exitButton);

        // Set up Text Watchers for moving focus between EditTexts
        setupTextWatchers();

        // Handle Verify button click
        buttonVerify.setOnClickListener(v -> {
            String enteredCode = getCodeFromInputFields();
            verifyCode(enteredCode);  // Verify entered code
        });

        // Handle Resend Email click
        textViewResendEmail.setOnClickListener(v -> resendVerificationEmail());

        // Handle Exit button click
        exitButton.setOnClickListener(v -> finish());  // Close activity
    }

    // Helper method to get the 5-digit code from input fields
    private String getCodeFromInputFields() {
        return editTextNumberDecimal1.getText().toString() +
                editTextNumberDecimal2.getText().toString() +
                editTextNumberDecimal3.getText().toString() +
                editTextNumberDecimal4.getText().toString() +
                editTextNumberDecimal5.getText().toString();
    }

    // Verify the code entered by the user
    private void verifyCode(String enteredCode) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String storedCode = snapshot.child("verificationCode").getValue(String.class);
                    long timestamp = snapshot.child("timestamp").getValue(Long.class);

                    if (enteredCode.equals(storedCode) && !isCodeExpired(timestamp)) {
                        allowPasswordReset();  // Code is valid, proceed to password reset
                    } else {
                        showErrorMessage("Code is incorrect or expired");
                    }
                } else {
                    showErrorMessage("No code found for the user");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
                showErrorMessage("Database error occurred");
            }
        });
    }

    // Resend the verification email
    private void resendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            auth.sendPasswordResetEmail(user.getEmail())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(forget_password_2.this, "Verification email resent!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(forget_password_2.this, "Failed to resend email", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Allow the user to reset their password
    private void allowPasswordReset() {
        String newPassword = "newPasswordHere";  // Get new password from UI or input field
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(forget_password_2.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(forget_password_2.this, "Failed to update password", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Check if the code is expired (1 hour validity)
    private boolean isCodeExpired(long timestamp) {
        long currentTime = System.currentTimeMillis();
        return (currentTime - timestamp) > 3600000; // 1 hour expiration time
    }

    // Placeholder for showing error messages
    private void showErrorMessage(String message) {
        Toast.makeText(forget_password_2.this, message, Toast.LENGTH_SHORT).show();
    }

    // Set up text watchers to automatically move focus between EditTexts (optional)
    private void setupTextWatchers() {
        // Logic to move focus from one EditText to the next automatically
    }
}
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//
