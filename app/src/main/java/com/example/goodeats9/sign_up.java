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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class sign_up extends AppCompatActivity
{

    EditText signUpName , signUpEmail , signUpPassword , signUpConfirmPassword , signUpDescription;
    Button signUpButton;
    ImageButton backButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        //Call the names of the text boxes
        signUpName            = findViewById(R.id.inputName);
        signUpEmail           = findViewById(R.id.inputEmail);
        signUpPassword        = findViewById(R.id.inputPassword);
        signUpConfirmPassword = findViewById(R.id.inputConfirmPassword);
        signUpButton          = findViewById(R.id.buttonSignUp);
        signUpDescription     = findViewById(R.id.inputDescription);
        backButton            = findViewById(R.id.backButton);

        backButton.setOnClickListener(v -> {
            // Navigate to SignUpActivity when the Sign Up TextView is clicked
            Intent intent = new Intent(sign_up.this, login.class);
            startActivity(intent);
        });

        signUpButton.setOnClickListener(view ->
        {

            // get the values of the text boxes into variables
            String name             = signUpName.getText().toString();
            String email            = signUpEmail.getText().toString();
            String Password         = signUpPassword.getText().toString();
            String ConfirmPassword  = signUpConfirmPassword.getText().toString();
            String Description      = signUpDescription.getText().toString();

            // Reset background to default before validation
            signUpName.setBackgroundResource(R.drawable.round_texbox);
            signUpEmail.setBackgroundResource(R.drawable.round_texbox);
            signUpPassword.setBackgroundResource(R.drawable.round_texbox);
            signUpConfirmPassword.setBackgroundResource(R.drawable.round_texbox);
            signUpDescription.setBackgroundResource(R.drawable.round_texbox);

            // Clear previous errors and reset the textbox colour when the signup button is clicked.
            signUpName.setError(null);
            signUpEmail.setError(null);
            signUpPassword.setError(null);
            signUpConfirmPassword.setError(null);
            signUpDescription.setError(null);

            // Validate the Name
            if (name.isEmpty()) // check if name is empty
            {
                signUpName.setError("Name is required");
                signUpName.setBackgroundResource(R.drawable.input_error);

            }
            else if (name.matches(".*\\d.*")) // Name cannot have numbers
            {
                signUpName.setError("Name cannot contain numbers");

            }

            //validate the email
            else if (email.isEmpty()) //check if email is empty
            {
                signUpEmail.setError("Email is required");
                signUpEmail.setBackgroundResource(R.drawable.input_error);

            }
            //check email pattern
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches())
            {
                signUpEmail.setError("Invalid email format");
                signUpEmail.setBackgroundResource(R.drawable.input_error);

            }

            //validate the password
            else if (Password.isEmpty()) // Check if the password is empty
            {
                signUpPassword.setError("Password is required");
                signUpPassword.setBackgroundResource(R.drawable.input_error);

            }
            else if (Password.length() < 6) // password length should be 6 characters long
            {
                signUpPassword.setError("Password must be at least 6 characters long");
                signUpPassword.setBackgroundResource(R.drawable.input_error);

            }

            //Validate Confirm Password
            else if (ConfirmPassword.isEmpty()) //Confirm Password cannot be empty
            {
                signUpConfirmPassword.setError("Confirm Password is required");
                signUpConfirmPassword.setBackgroundResource(R.drawable.input_error);

            }
            else if (!Password.equals(ConfirmPassword)) //Password and confirm password should be matched
            {
                signUpConfirmPassword.setError("Passwords do not match");
                signUpConfirmPassword.setBackgroundResource(R.drawable.input_error);

            }
            // Validate the Description
            else if (Description.isEmpty()) // check if Description is empty
            {
                signUpDescription.setError("Description is required");
                signUpDescription.setBackgroundResource(R.drawable.input_error);
            }

            else
            {
                //Create database connection
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                //check whether the email is already exist
                reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists()) // email already exists
                        {
                            signUpEmail.setError("Email already registered");
                            signUpEmail.setBackgroundResource(R.drawable.input_error);
                        }
                        else // email doesn't exist. so continue with signing up
                        {
                            helperClass helperClass = new helperClass(name, email, Password,Description);
                            reference.child(name).setValue(helperClass);

                            Toast.makeText(sign_up.this, "You have signed up successfully", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(sign_up.this, login.class);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Handle possible errors
                        Toast.makeText(sign_up.this, "Error checking email", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}