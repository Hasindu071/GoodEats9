package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddNew extends AppCompatActivity {

    private DatabaseReference ref;

    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    ImageView addPic, backbtn, addI, addM;
    VideoView videoView;
    EditText entername, enterdiscription, enterserves, entertime, ingrediant, quantity, method;
    Button addrecipebtn, view_video_button, addImage_button;
    RecyclerView ingredientsList, methodList;

    Uri imageUri;
    Uri videoUri;

    // Lists to hold ingredients and methods
    ArrayList<String> ListI, ListM;
    SimpleAdapter adapterI, adapterM;

    @SuppressLint({"WrongViewCast", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        ref = FirebaseDatabase.getInstance().getReference("recipes");
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Initialize views
        addPic = findViewById(R.id.addPic);
        videoView = findViewById(R.id.videoView);
        view_video_button = findViewById(R.id.view_video_button);
        addImage_button = findViewById(R.id.addImage_button);

        entername = findViewById(R.id.entername);
        enterdiscription = findViewById(R.id.enterdiscription);
        enterserves = findViewById(R.id.enterserves);
        entertime = findViewById(R.id.entertime);

        ingrediant = findViewById(R.id.ingrediant);
        quantity = findViewById(R.id.quantity);
        addI = findViewById(R.id.addI);
        ingredientsList = findViewById(R.id.ingredientsList);

        method = findViewById(R.id.method);
        addM = findViewById(R.id.addM);
        methodList = findViewById(R.id.methodList);
        addrecipebtn = findViewById(R.id.addrecipebtn);

        // Back button functionality
        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(v -> finish());

        // Select image for the recipe
        addImage_button.setOnClickListener(v -> openImageChooser());

        // Select video for the recipe
        view_video_button.setOnClickListener(v -> openVideoChooser());

        // Handle adding recipe button click
        addrecipebtn.setOnClickListener(v -> addRecipe());

        // Initialize the list and adapter for ingredients
        ListI = new ArrayList<>();
        adapterI = new SimpleAdapter(ListI);
        ingredientsList.setLayoutManager(new LinearLayoutManager(this));
        ingredientsList.setAdapter(adapterI);

// Initialize the list and adapter for methods
        ListM = new ArrayList<>();
        adapterM = new SimpleAdapter(ListM);
        methodList.setLayoutManager(new LinearLayoutManager(this));
        methodList.setAdapter(adapterM);

        // Add ingredient and quantity to the ingredients list
        addI.setOnClickListener(v -> {
            String ingredientName = ingrediant.getText().toString();
            String ingredientQuantity = quantity.getText().toString();

            if (!ingredientName.isEmpty() && !ingredientQuantity.isEmpty()) {
                ListI.add(ingredientName + " - " + ingredientQuantity);
                adapterI.notifyDataSetChanged(); // Notify RecyclerView to refresh
                ingrediant.setText("");
                quantity.setText("");
            } else {
                Toast.makeText(this, "Please enter both ingredient and quantity", Toast.LENGTH_SHORT).show();
            }
        });

        // Add method to the methods list
        addM.setOnClickListener(v -> {
            String methodName = method.getText().toString();

            if (!methodName.isEmpty()) {
                ListM.add(methodName);
                adapterM.notifyDataSetChanged(); // Notify RecyclerView to refresh
                method.setText("");
            } else {
                Toast.makeText(this, "Please enter the method", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Function to open the image chooser
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    // Function to open the video chooser
    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    // Handle the result of image/video picker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addPic.setImageBitmap(getResizedImage(imageUri, 141, 141)); // Resize to ~5x5 cm
        } else if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }

    // Method to resize the selected image
    private Bitmap getResizedImage(Uri uri, int width, int height) {
        try {
            Bitmap originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    // Handle adding the recipe logic and storing data to Firebase
    @SuppressLint("NotifyDataSetChanged")
    private void addRecipe() {
        String name = entername.getText().toString().trim();
        String description = enterdiscription.getText().toString().trim();
        String serves = enterserves.getText().toString().trim();
        String cookTime = entertime.getText().toString().trim();

        // Get current authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Validate input
        if (isInputValid(name, description, serves, cookTime) && currentUser != null) {

            // Get a unique key using the user's email and replace "." with "_"
            String userEmailKey = currentUser.getEmail().replace(".", "_");


// Create a unique recipe ID for each recipe
            DatabaseReference userRef = ref.child(userEmailKey).push();
            String recipeId = userRef.getKey();

            // Create a map to hold the recipe details
            Map<String, Object> recipeData = new HashMap<>();
            recipeData.put("name", name);
            recipeData.put("description", description);
            recipeData.put("serves", serves);
            recipeData.put("cookTime", cookTime);
            recipeData.put("ingredients", ListI);  // Store ingredients list
            recipeData.put("methods", ListM);      // Store methods list

            // Check if there is an image or video to upload
            if (imageUri != null) {
                uploadImage(userRef, recipeData);
            } else if (videoUri != null) {
                uploadVideo(userRef, recipeData);
            } else {
                saveRecipeData(userRef, recipeData); // If no image/video, save data directly
            }
        } else {
            Toast.makeText(AddNew.this, "Please fill all fields or log in", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to upload image to Firebase Storage
    private void uploadImage(DatabaseReference userRef, Map<String, Object> recipeData) {
        StorageReference imageRef = storageReference.child("Recipiimages/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            recipeData.put("imageUri", uri.toString());
            saveRecipeData(userRef, recipeData);
        })).addOnFailureListener(e -> {
            Toast.makeText(AddNew.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Method to upload video to Firebase Storage
    private void uploadVideo(DatabaseReference userRef, Map<String, Object> recipeData) {
        StorageReference videoRef = storageReference.child("Recipivideos/" + System.currentTimeMillis() + ".mp4");
        UploadTask uploadTask = videoRef.putFile(videoUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            recipeData.put("videoUri", uri.toString());
            saveRecipeData(userRef, recipeData);
        })).addOnFailureListener(e -> {
            Toast.makeText(AddNew.this, "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    // Method to save recipe data to Firebase Realtime Database
    private void saveRecipeData(DatabaseReference userRef, Map<String, Object> recipeData) {
        userRef.setValue(recipeData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddNew.this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                resetFields();
            } else {
                Toast.makeText(AddNew.this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Validate input fields (without ingredient and method checks since they are already lists)
    private boolean isInputValid(String... inputs) {
        for (String input : inputs) {
            if (input.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    // Method to reset all input fields
    private void resetFields() {
        entername.setText("");
        enterdiscription.setText("");
        enterserves.setText("");
        entertime.setText("");
        ingrediant.setText("");
        quantity.setText("");
        method.setText("");
        ListI.clear();
        ListM.clear();
        adapterI.notifyDataSetChanged();
        adapterM.notifyDataSetChanged();
        addPic.setImageResource(R.drawable.placeholder_image);
        videoView.stopPlayback();
        videoView.setVideoURI(null);
    }

    // RecyclerView Adapter for displaying ingredients and methods
    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

        private ArrayList<String> itemList;

        public SimpleAdapter(ArrayList<String> itemList) {
            this.itemList = itemList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.textView.setText(itemList.get(position));
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;

            public ViewHolder(View view) {
                super(view);
                textView = view.findViewById(android.R.id.text1);
            }
        }
    }
}