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

    // Firebase references
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("recipes");

    // Firebase storage reference for image
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    // UI elements
    final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;

    String email;
    EditText ingredientText, amountText, methodText, titleText, descriptionText, servesText, cookTimeText;
    ImageView addI, addM, selectImg;
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
        titleText = findViewById(R.id.entername);
        descriptionText = findViewById(R.id.enterdiscription);
        ingredientText = findViewById(R.id.ingrediant);
        amountText = findViewById(R.id.quantity);
        addI = findViewById(R.id.addI);
        ingredientList = findViewById(R.id.ingredientsList);

        methodText = findViewById(R.id.method);
        addM = findViewById(R.id.addM);
        methodList = findViewById(R.id.methodList);

        selectImg = findViewById(R.id.addPic);
        saveRecipe = findViewById(R.id.addrecipebtn);
        servesText = findViewById(R.id.enterserves); // Use EditText for serves
        cookTimeText = findViewById(R.id.entertime); // Use EditText for cook time

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
            String ingredients = ingredientText.getText().toString();
            String quantity = amountText.getText().toString();

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

        // Save recipe and upload it to Firebase
        saveRecipe.setOnClickListener(v -> saveRecipe());

        // Back button functionality
        ImageView back = findViewById(R.id.backbtn);
        back.setOnClickListener(v -> this.finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            if (requestCode == PICK_IMAGE_REQUEST) {
                imageUri = data.getData(); // Get the image URI
            }
        }
    }

    private void saveRecipe() {
        if (validateInputs()) { // Validate all inputs before saving
            uploadImage(imageUrl -> {
                // Save the recipe with the image URL
                saveRecipeToDatabase(imageUrl);
            });
        }
    }

    private boolean validateInputs() {
        if (titleText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (descriptionText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (servesText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter serves", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (cookTimeText.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please enter cook time", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listI.isEmpty()) {
            Toast.makeText(this, "Please add at least one ingredient", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (listM.isEmpty()) {
            Toast.makeText(this, "Please add at least one method", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // Upload image to Firebase Storage
    private void uploadImage(final RecipeCallback callback) {
        if (imageUri != null) {
            StorageReference imageRef = storageRef.child("images/" + System.currentTimeMillis() + ".jpg");
            imageRef.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        callback.onImageUploaded(imageUrl); // Trigger callback after image upload
                    })).addOnFailureListener(e -> {
                Toast.makeText(AddNew.this, "Failed to upload image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        } else {
            callback.onImageUploaded(null); // Ensure callback is triggered even when there's no image
        }
    }

    // Save recipe data including image URLs to Firebase Realtime Database
    private void saveRecipeToDatabase(String imageUrl) {
        if (currentUser != null) {
            String userEmailKey = currentUser.getEmail().replace(".", "_");
            DatabaseReference userRef = ref.child(userEmailKey).push();

            // Collect recipe details
            HashMap<String, Object> recipeData = new HashMap<>();
            recipeData.put("title", titleText.getText().toString()); // Add title
            recipeData.put("description", descriptionText.getText().toString()); // Add description
            recipeData.put("serves", servesText.getText().toString()); // Add serves
            recipeData.put("cookTime", cookTimeText.getText().toString()); // Add cook time
            recipeData.put("ingredients", listI);
            recipeData.put("methods", listM);

            if (imageUrl != null) recipeData.put("imageUrl", imageUrl);

            // Save to Firebase
            userRef.setValue(recipeData).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(AddNew.this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                    titleText.setText("");
                    descriptionText.setText("");
                    servesText.setText("");
                    cookTimeText.setText("");
                    listI.clear();
                    listM.clear();
                    adapterI.notifyDataSetChanged();
                    adapterM.notifyDataSetChanged();
                } else {
                    Toast.makeText(AddNew.this, "Failed to save recipe: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }


            });
        }
    }

    // Callback interface for handling uploads
    private interface RecipeCallback {
        void onImageUploaded(String imageUrl);
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
