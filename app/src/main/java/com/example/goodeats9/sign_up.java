package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class sign_up extends AppCompatActivity {

    EditText signUpName, signUpEmail, signUpPassword, signUpConfirmPassword, signUpDescription;
    Button signUpButton;
    ImageButton backButton;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseAuth auth;  // Add FirebaseAuth instance

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // Call the names of the text boxes
        signUpName            = findViewById(R.id.inputName);
        signUpEmail           = findViewById(R.id.inputEmail);
        signUpPassword        = findViewById(R.id.inputPassword);
        signUpConfirmPassword = findViewById(R.id.inputConfirmPassword);
        signUpButton          = findViewById(R.id.buttonSignUp);
        signUpDescription     = findViewById(R.id.inputDescription);
        backButton            = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(sign_up.this, login.class);
            startActivity(intent);
        });

        signUpButton.setOnClickListener(view -> {

            // get the values of the text boxes into variables
            String name            = signUpName.getText().toString();
            String email           = signUpEmail.getText().toString();
            String password        = signUpPassword.getText().toString();
            String confirmPassword = signUpConfirmPassword.getText().toString();
            String description     = signUpDescription.getText().toString();

            // Validate inputs (as you already have)

            if (name.isEmpty())
            {
                signUpName.setError("Name is required");
                signUpName.setBackgroundResource(R.drawable.input_error);
            }
            else if (email.isEmpty())
            {
                signUpEmail.setError("Email is required");
                signUpEmail.setBackgroundResource(R.drawable.input_error);
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                signUpEmail.setError("Invalid email format");
                signUpEmail.setBackgroundResource(R.drawable.input_error);
            }
            else if (password.isEmpty()) {
                signUpPassword.setError("Password is required");
                signUpPassword.setBackgroundResource(R.drawable.input_error);
            }
            else if (password.length() < 6)
            {
                signUpPassword.setError("Password must be at least 6 characters long");
                signUpPassword.setBackgroundResource(R.drawable.input_error);
            }
            else if (!password.equals(confirmPassword))
            {
                signUpConfirmPassword.setError("Passwords do not match");
                signUpConfirmPassword.setBackgroundResource(R.drawable.input_error);
            }
            else if (description.isEmpty())
            {
                signUpDescription.setError("Description is required");
                signUpDescription.setBackgroundResource(R.drawable.input_error);
            }
            else
            {
                // Create the user in Firebase Authentication
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task ->
                        {
                            if (task.isSuccessful())
                            {
                                // Sign up successful, get user ID
                                FirebaseUser user = auth.getCurrentUser();
                                String userId = user != null ? user.getUid() : "";

                                // Now, store additional user data in the database
                                database = FirebaseDatabase.getInstance();
                                reference = database.getReference("users");

                                helperClass helperClass = new helperClass(name, email, password, description);
                                reference.child(userId).setValue(helperClass)
                                        .addOnCompleteListener(databaseTask ->
                                        {
                                            if (databaseTask.isSuccessful())
                                            {
                                                Toast.makeText(sign_up.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(sign_up.this, login.class);
                                                startActivity(intent);
                                            }
                                            else
                                            {
                                                Toast.makeText(sign_up.this, "Failed to store user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                            else
                            {
                                // Handle errors (e.g., email already in use)
                                if (task.getException() instanceof FirebaseAuthUserCollisionException)
                                {
                                    signUpEmail.setError("Email already registered");
                                    signUpEmail.setBackgroundResource(R.drawable.input_error);
                                }
                                else
                                {
                                    Toast.makeText(sign_up.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
