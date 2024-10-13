package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//

public class Forget_Password extends AppCompatActivity {

    private EditText editTextEmailAddress;
    private Button resetButton;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        // Set system bars padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        // Initialize UI elements
        ImageButton backButton = findViewById(R.id.btn_back);
        resetButton = findViewById(R.id.buttonRsetpassword);
        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);

        // Back button action to go back to login screen
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Forget_Password.this, login.class);
                startActivity(intent);
            }
        });

        // Reset password button action
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmailAddress.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Send reset password email
                auth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(Forget_Password.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                                    // Move to forget_password_3 page after email is sent
                                    Intent intent = new Intent(Forget_Password.this, login.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(Forget_Password.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//
