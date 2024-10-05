package com.example.goodeats9;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Reviews extends AppCompatActivity {

    private EditText inputComment;
    private Button sendButton;
    private ImageButton backButton;
    private RecyclerView recyclerView;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList;
    private DatabaseReference databaseReference;
    private String username;
    private String recipeId,email;  // To store the unique recipe ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        inputComment = findViewById(R.id.inputComment);
        sendButton = findViewById(R.id.button);
        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.recyclerView);

        reviewList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(reviewAdapter);

        // Assume recipeId is passed via intent when opening the reviews activity
        recipeId = getIntent().getStringExtra("recipeId");
        email = getIntent().getStringExtra("userEmail");

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("recipes").child(email).child(recipeId).child("reviews");

        // Retrieve username from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
        username = sharedPreferences.getString("UserName", "User");

        loadReviews();

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postReview();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void loadReviews() {
        // Listen for changes in the reviews for the specific recipe
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                reviewList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    // Retrieve the review object
                    Review review = snapshot.getValue(Review.class);

                    // Check if the review is not null before adding it to the list
                    if (review != null) {
                        reviewList.add(review);
                    }
                }
                reviewAdapter.notifyDataSetChanged(); // Notify adapter after changes
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Reviews.this, "Failed to load reviews", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postReview() {
        String comment = inputComment.getText().toString().trim();
        if (TextUtils.isEmpty(comment)) {
            Toast.makeText(this, "Comment cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date());

        // Create a new Review object with the username, comment, and timestamp
        Review review = new Review(username, comment, timestamp);
        databaseReference.push().setValue(review).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                inputComment.setText(""); // Clear input field after posting
                Toast.makeText(Reviews.this, "Comment posted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Reviews.this, "Failed to post comment", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
