package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//

public class forget_password_3 extends AppCompatActivity {

    private EditText editTextNewPassword, editTextConfirmPassword;
    private Button buttonResetPassword;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password3);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        editTextNewPassword = findViewById(R.id.NewPassword);
        editTextConfirmPassword = findViewById(R.id.Comfirmpassword);
        buttonResetPassword = findViewById(R.id.button_Reset);

        // Set action for Reset Password button
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPassword = editTextNewPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();

                // Validate inputs
                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(getApplicationContext(), "Enter new password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Confirm your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!newPassword.equals(confirmPassword)) {
                    Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (newPassword.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Update the password
                updatePassword(newPassword);
            }
        });
    }

    private void updatePassword(String newPassword) {
        FirebaseUser user = auth.getCurrentUser();  // Get the current user

        if (user != null) {
            // Update password for authenticated user
            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(forget_password_3.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                        // Redirect to login page
                        Intent intent = new Intent(forget_password_3.this, forget_password_4.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        // Display detailed error message
                        String errorMessage = task.getException() != null ? task.getException().getMessage() : "Failed to update password!";
                        Toast.makeText(forget_password_3.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            // If the user is null, it means the user is not authenticated
            Toast.makeText(forget_password_3.this, "Error: User not authenticated!", Toast.LENGTH_SHORT).show();
        }
    }
}
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//
