package com.example.goodeats9;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Forget_Password extends AppCompatActivity {

    //declare the variables
    EditText editTextEmailAddress;
    Button buttonRsetpassword;
    ImageButton btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        editTextEmailAddress = findViewById(R.id.editTextEmailAddress);
        buttonRsetpassword = findViewById(R.id.buttonRsetpassword);
        btn_back = findViewById(R.id.btn_back);

        // Back button to go to Login page
        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(Forget_Password.this, login.class);
            startActivity(intent);
            finish();
        });

        // Reset button logic
        buttonRsetpassword.setOnClickListener(v -> {
            //is validateEmail true
            if (validateEmail()) {
                String userEmail = editTextEmailAddress.getText().toString().trim(); //get the data in to the email box
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users"); //connect to the database
                Query checkUserDatabase = reference.orderByChild("email").equalTo(userEmail); // check the current email is equal to the new enter data

                checkUserDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) { //remove the current email
                            Toast.makeText(Forget_Password.this, "Please Check Your Email", Toast.LENGTH_SHORT).show(); //send message to the email and show the message
                            startActivity(new Intent(Forget_Password.this, login.class)); // back to login page
                        } else {
                            editTextEmailAddress.setError("No account with this email"); // show the error message
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Forget_Password.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    // Email validation
    public boolean validateEmail() {
        String val = editTextEmailAddress.getText().toString();
        if (val.isEmpty()) {
            editTextEmailAddress.setError("Email cannot be empty!");
            return false;
        }
        return true;
    }
}
