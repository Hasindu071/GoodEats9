package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class AddNew extends AppCompatActivity {

    // Firebase authentication and database reference
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("recipes");

    // Firebase storage reference for image and video
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    // UI elements
    final int PICK_IMAGE_REQUEST = 1;
    final int PICK_VIDEO_REQUEST = 2;
    Uri imageUri, videoUri;

    String email;
    EditText ingredientText, amountText, methodText;
    ImageView addI, addM, selectImg, selectVid;
    Button saveRecipe;
    RecyclerView ingredientList, methodList;

    ArrayList<String> listI, listM;
    SimpleAdapter adapterI, adapterM;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        // Check if the user is logged in
        if (currentUser != null) {
            email = currentUser.getEmail();
        } else {
            email = "No user logged in";
        }

        // Initialize UI elements
        ingredientText = findViewById(R.id.ingrediant);
        amountText = findViewById(R.id.quantity);
        addI = findViewById(R.id.addI);
        ingredientList = findViewById(R.id.ingredientsList);

        methodText = findViewById(R.id.method);
        addM = findViewById(R.id.addM);
        methodList = findViewById(R.id.methodList);

        selectImg = findViewById(R.id.addPic);
        selectVid = findViewById(R.id.addVid);
        saveRecipe = findViewById(R.id.addrecipebtn);

        listI = new ArrayList<>();
        adapterI = new SimpleAdapter(listI);
        ingredientList.setLayoutManager(new LinearLayoutManager(this));
        ingredientList.setAdapter(adapterI);

        listM = new ArrayList<>();
        adapterM = new SimpleAdapter(listM);
        methodList.setLayoutManager(new LinearLayoutManager(this));
        methodList.setAdapter(adapterM);

        // Add ingredients to the list
        addI.setOnClickListener(v -> {
            String ingredients = String.valueOf(ingredientText.getText());
            String quantity = String.valueOf(amountText.getText());

            if (!ingredients.isEmpty() && !quantity.isEmpty()) {
                listI.add(ingredients + " - " + quantity);
                adapterI.notifyDataSetChanged();
                ingredientText.setText("");
                amountText.setText("");
            } else {
                Toast.makeText(this, "Please enter both ingredient and quantity", Toast.LENGTH_SHORT).show();
            }
        });

        // Add methods to the list
        addM.setOnClickListener(v -> {
            String method = methodText.getText().toString();

            if (!method.isEmpty()) {
                listM.add(method);
                adapterM.notifyDataSetChanged();
                methodText.setText("");
            } else {
                Toast.makeText(this, "Please enter the method", Toast.LENGTH_SHORT).show();
            }
        });

        // Select image from gallery
        selectImg.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        // Select video from gallery
        selectVid.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_VIDEO_REQUEST);
        });

        // Save recipe and upload it to Firebase
        saveRecipe.setOnClickListener(v -> saveRecipe());

        // Back button functionality
        ImageView back = findViewById(R.id.backbtn);
        back.setOnClickListener(v -> {
            this.finish(); // Close the current activity
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData(); // Get the image URI
            } else if (requestCode == PICK_VIDEO_REQUEST) {
                videoUri = data.getData(); // Get the video URI
            }
        }
    }

    // Upload image to Firebase Storage
    private void uploadImage(final RecipeCallback callback) {
        if (imageUri != null) {
            StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(callback::onImageUploaded)
            ).addOnFailureListener(e ->
                    Toast.makeText(AddNew.this, "Failed to upload image", Toast.LENGTH_SHORT).show()
            );
        } else {
            callback.onImageUploaded(null); // Ensure callback is triggered even when there's no image
        }
    }

    // Upload video to Firebase Storage
    private void uploadVideo(final RecipeCallback callback) {
        if (videoUri != null) {
            StorageReference videoRef = storageRef.child("videos/" + System.currentTimeMillis() + ".mp4");
            videoRef.putFile(videoUri).addOnSuccessListener(taskSnapshot ->
                    videoRef.getDownloadUrl().addOnSuccessListener(callback::onVideoUploaded)
            ).addOnFailureListener(e ->
                    Toast.makeText(AddNew.this, "Failed to upload video", Toast.LENGTH_SHORT).show()
            );
        } else {
            callback.onVideoUploaded(null); // Ensure callback is triggered even when there's no video
        }
    }

    // Save the recipe data to Firebase
    private void saveRecipe() {
        uploadImage(imageUrl -> {
            uploadVideo(videoUrl -> {
                saveRecipeToDatabase(imageUrl, videoUrl); // Save after both uploads finish
            });
        });
    }


    // Save recipe data including image and video URLs to Firebase Realtime Database
    private void saveRecipeToDatabase(Object imageUrl, Object videoUrl) {
        if (currentUser != null) {
            String userEmailKey = currentUser.getEmail().replace(".", "_");
            DatabaseReference userRef = ref.child(userEmailKey).push();

            // Collect recipe details
            HashMap<String, Object> recipeData = new HashMap<>();
            recipeData.put("ingredients", listI);
            recipeData.put("methods", listM);

            if (imageUrl != null) recipeData.put("imageUrl", imageUrl);
            if (videoUrl != null) recipeData.put("videoUrl", videoUrl);

            // Save to Firebase
            userRef.setValue(recipeData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddNew.this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddNew.this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Callback interface for handling uploads
    private interface RecipeCallback {
        void onImageUploaded(String imageUrl);
        void onVideoUploaded(String videoUrl);
    }

    // Inner adapter class for the RecyclerView
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

        private ArrayList<String> itemList;

        public SimpleAdapter(ArrayList<String> itemList) {
            this.itemList = itemList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemText.setText(itemList.get(position));
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView itemText;

            public ViewHolder(View itemView) {
                super(itemView);
                itemText = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
