package com.example.goodeats9;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class login extends AppCompatActivity {

    EditText loginEmail, loginPassword;
    Button signinButton, googleButton, facebookButton;
    TextView signupRedirectText, forgotPasswordText;
    FirebaseAuth auth;
    FirebaseDatabase database;
    GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEmail = findViewById(R.id.Email);
        loginPassword = findViewById(R.id.Password);
        signupRedirectText = findViewById(R.id.textViewSignUp);
        signinButton = findViewById(R.id.buttonSignIN);
        googleButton = findViewById(R.id.buttonGoogle);
        forgotPasswordText = findViewById(R.id.textViewForgotPassword);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        forgotPasswordText.setPaintFlags(forgotPasswordText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPasswordText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, Forget_Password.class);
                startActivity(intent);
            }
        });

        signinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!validateEmail() | !validatePassword()) {

                } else {
                    checkEmail();
                }
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        signupRedirectText.setPaintFlags(signupRedirectText.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        signupRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(login.this, sign_up.class);
                startActivity(intent);
            }
        });
    }

    private void googleSignIn() {
        // Sign out of Firebase and Google to allow switching accounts
        auth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Now that we've signed out, start the sign-in process again
                Log.d("SignIn", "Signed out. Starting Google Sign-In process.");
                Intent intent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("SignIn", "Google Sign-In successful: " + (account != null));
                firebaseAuth(account.getIdToken());
            } catch (ApiException e) {
                Log.e("SignIn", "Google Sign-In failed: " + e.getMessage());
                Toast.makeText(getApplicationContext(), "Something went wrong1", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuth(String idToken) {
        Log.d("SignIn", "Attempting to authenticate with Firebase.");
        Log.d("SignIn", "ID Token: " + idToken);

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("SignIn", "Firebase Authentication Successful: " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            FirebaseUser user = auth.getCurrentUser();
                            if (user != null) {
                                Log.d("FirebaseAuth", "Name: " + user.getDisplayName());
                                Log.d("FirebaseAuth", "Profile Pic: " + user.getPhotoUrl());
                                Log.d("FirebaseAuth", "Email: " + user.getEmail());

                                HashMap<String, Object> map = new HashMap<>();
                                map.put("id", user.getUid());
                                map.put("name", user.getDisplayName());
                                map.put("profile", user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : "");
                                map.put("email", user.getEmail());

                                database.getReference().child("users").child(user.getUid()).setValue(map);

                                Log.d("SignIn", "Navigating to MainActivity.");
                                Intent intent = new Intent(login.this, menu_bar_main.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            Log.e("SignIn", "Firebase Authentication Failed: " + task.getException());
                            Toast.makeText(login.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public boolean validateEmail() {
        String val = loginEmail.getText().toString();
        if (val.isEmpty()) {
            loginEmail.setError("Email cannot be empty!");
            return false;
        } else {
            loginEmail.setError(null);
            return true;
        }
    }

    public boolean validatePassword() {
        String val = loginPassword.getText().toString();
        if (val.isEmpty()) {
            loginPassword.setError("Password cannot be empty!");
            return false;
        } else {
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

                    for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        String passwordFromDB = userSnapshot.child("password").getValue(String.class);

                        if (Objects.equals(passwordFromDB, userPassword)) {
                            loginPassword.setError(null);
                            Intent intent = new Intent(login.this, menu_bar_main.class);
                            startActivity(intent);
                            finish();
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
            }
        });
    }
}
