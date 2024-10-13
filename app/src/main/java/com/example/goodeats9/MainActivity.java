package com.example.goodeats9;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//

public class MainActivity extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if permission is granted or not
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission();
        } else {
            // Permission already granted, you can load video or access storage here
            loadVideoAndThumbnail();
        }
    }

    // Method to request permission
    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Show an explanation to the user why the permission is needed
            Toast.makeText(this, "Storage permission is required to access videos", Toast.LENGTH_SHORT).show();
        }
        // Request the permission
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
    }

    // Handle the result of the permission request
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE) {
            // If the permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                // Now you can load video and thumbnail
                loadVideoAndThumbnail();
            } else {
                // If permission is denied
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Method to load video and thumbnail (you can replace this with your own logic)
    private void loadVideoAndThumbnail() {
        // Add your logic to load the video and its thumbnail here
    }
}
//-----------------------------------------IM/2021/003 - Dulmi ---------------------------------------------------//

