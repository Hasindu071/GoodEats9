package com.example.goodeats9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
//-----------------------------------------IM/2021/094 - Sandani ---------------------------------------------------//
public class AddNew extends AppCompatActivity {

    private DatabaseReference ref;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

    private Spinner categorySpinner;

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_VIDEO_REQUEST = 2;

    ImageView addPic, backbtn, addI, addM;
    VideoView videoView;
    EditText entername, enterdiscription, enterserves, entertime, ingrediant, quantity, method;
    Button addrecipebtn, view_video_button, addImage_button;
    RecyclerView ingredientsList, methodList;

    Uri imageUri;
    Uri videoUri;

    ArrayList<String> ListI, ListM;
    SimpleAdapter adapterI, adapterM;

    @SuppressLint({"WrongViewCast", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);

        categorySpinner = findViewById(R.id.category_spinner);

        // Example categories to populate the spinner
        String[] categories = {"Breakfast", "Lunch", "Dinner", "Snack"};

        // Creating an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Retrieve the selected item
                String selectedCategory = parentView.getItemAtPosition(position).toString();

                // Do something with the selected value, for example, log it or display it
                System.out.println("Selected Category: " + selectedCategory);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // This can be left empty if you do not need to handle the case of nothing being selected
            }
        });

        // Applying the adapter to the spinner
        categorySpinner.setAdapter(adapter);

        ref = FirebaseDatabase.getInstance().getReference("recipes");
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

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

        backbtn = findViewById(R.id.backbtn);
        backbtn.setOnClickListener(v -> finish());

        addImage_button.setOnClickListener(v -> openImageChooser());
        view_video_button.setOnClickListener(v -> openVideoChooser());

        addrecipebtn.setOnClickListener(v -> addRecipe());

        ListI = new ArrayList<>();
        adapterI = new SimpleAdapter(ListI);
        ingredientsList.setLayoutManager(new LinearLayoutManager(this));
        ingredientsList.setAdapter(adapterI);

        ListM = new ArrayList<>();
        adapterM = new SimpleAdapter(ListM);
        methodList.setLayoutManager(new LinearLayoutManager(this));
        methodList.setAdapter(adapterM);

        addI.setOnClickListener(v -> {
            String ingredientName = ingrediant.getText().toString();
            String ingredientQuantity = quantity.getText().toString();

            if (!TextUtils.isEmpty(ingredientName) && !TextUtils.isEmpty(ingredientQuantity)) {
                ListI.add(ingredientName + " - " + ingredientQuantity);
                adapterI.notifyDataSetChanged();
                ingrediant.setText("");
                quantity.setText("");
            } else {
                Toast.makeText(this, "Please enter both ingredient and quantity", Toast.LENGTH_SHORT).show();
            }
        });

        addM.setOnClickListener(v -> {
            String methodName = method.getText().toString();

            if (!TextUtils.isEmpty(methodName)) {
                ListM.add(methodName);
                adapterM.notifyDataSetChanged();
                method.setText("");
            } else {
                Toast.makeText(this, "Please enter the method", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openVideoChooser() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_VIDEO_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            addPic.setImageBitmap(getResizedImage(imageUri, 141, 141));
        }
        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            videoUri = data.getData();
            videoView.setVideoURI(videoUri);
            videoView.start();
        }
    }

    private Bitmap getResizedImage(Uri uri, int width, int height) {
        try {
            Bitmap originalBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            return Bitmap.createScaledBitmap(originalBitmap, width, height, false);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void addRecipe() {
        String name = entername.getText().toString().trim();
        String description = enterdiscription.getText().toString().trim();
        String serves = enterserves.getText().toString().trim();
        String cookTime = entertime.getText().toString().trim();
        String selectedCategory = categorySpinner.getSelectedItem().toString();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (isInputValid(name, description, serves, cookTime) && currentUser != null) {
            String userEmailKey = currentUser.getEmail().replace(".", "_");
            SharedPreferences sharedPreferences = getSharedPreferences("loginDetails", MODE_PRIVATE);
            String username = sharedPreferences.getString("UserName", "User");

            DatabaseReference userRef = ref.child(userEmailKey).push();
            String recipeId = userRef.getKey();

            Map<String, Object> recipeData = new HashMap<>();
            recipeData.put("name", name);
            recipeData.put("username", username);
            recipeData.put("description", description);
            recipeData.put("serves", serves);
            recipeData.put("cookTime", cookTime);
            recipeData.put("ingredients", ListI);
            recipeData.put("methods", ListM);
            recipeData.put("category",selectedCategory);

            if (imageUri != null) {
                uploadImage(userRef, recipeData);
            } else if (videoUri != null) {
                uploadVideo(userRef, recipeData);
            } else {
                saveRecipeData(userRef, recipeData);
            }
        } else {
            Toast.makeText(AddNew.this, "Please fill all fields or log in", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(DatabaseReference userRef, Map<String, Object> recipeData) {
        StorageReference imageRef = storageReference.child("RecipeImages/" + System.currentTimeMillis() + ".jpg");
        UploadTask uploadTask = imageRef.putFile(imageUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            recipeData.put("imageUri", uri.toString());
            if (videoUri != null) {
                uploadVideo(userRef, recipeData);  // Upload video if present
            } else {
                saveRecipeData(userRef, recipeData);
            }
        })).addOnFailureListener(e -> {
            Toast.makeText(AddNew.this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void uploadVideo(DatabaseReference userRef, Map<String, Object> recipeData) {
        StorageReference videoRef = storageReference.child("RecipeVideos/" + System.currentTimeMillis() + ".mp4");
        UploadTask uploadTask = videoRef.putFile(videoUri);

        uploadTask.addOnSuccessListener(taskSnapshot -> videoRef.getDownloadUrl().addOnSuccessListener(uri -> {
            recipeData.put("videoUri", uri.toString());
            saveRecipeData(userRef, recipeData);
            Toast.makeText(AddNew.this, "Video uploaded successfully", Toast.LENGTH_SHORT).show();
        })).addOnFailureListener(e -> {
            Toast.makeText(AddNew.this, "Video upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }).addOnProgressListener(snapshot -> {
            double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
            Toast.makeText(AddNew.this, "Upload is " + progress + "% done", Toast.LENGTH_SHORT).show();
        });
    }

    private void saveRecipeData(DatabaseReference userRef, Map<String, Object> recipeData) {
        userRef.setValue(recipeData).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AddNew.this, "Recipe saved successfully!", Toast.LENGTH_SHORT).show();
                resetFields();
                //close the add new page and go back
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new homeFragment());
                fragmentTransaction.commit();

            } else {
                Toast.makeText(AddNew.this, "Failed to save recipe", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isInputValid(String... inputs) {
        for (String input : inputs) {
            if (TextUtils.isEmpty(input)) {
                return false;
            }
        }
        return true;
    }

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

    public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {
        private ArrayList<String> itemList;

        public SimpleAdapter(ArrayList<String> itemList) {
            this.itemList = itemList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
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
//-----------------------------------------IM/2021/094 - Sandani ---------------------------------------------------//