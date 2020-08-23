package com.example.coinscounter.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.coinscounter.R;
import com.example.coinscounter.utills.PermissionManager;
import com.example.coinscounter.viewmodel.MainActivityViewModel;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int GALLERY_PICK_IMAGE = 4;
    private static final int PERMISSION_TO_WRITE_STORAGE = 2;
    private static final int PERMISSION_TO_READ_STORAGE = 3;

    private MainActivityViewModel viewModel;
    private Button calculateSumBtn;
    private ImageView imgView;

    private String photoFileAbsolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculateSumBtn = findViewById(R.id.calculate_sum_btn);
        imgView = findViewById(R.id.image_view);
        LinearLayout thresholdUpdateBar = findViewById(R.id.thresh_update_bar);
        LinearLayout distUpdateBar = findViewById(R.id.dist_update_bar);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        viewModel.getImageToDisplay().observe(this, (processedBitmap) -> {
            imgView.setImageBitmap(processedBitmap);

            thresholdUpdateBar.setVisibility(View.VISIBLE);
            distUpdateBar.setVisibility(View.VISIBLE);
        });

        viewModel.getNumOfSelectedCoins().observe(this, (numOfSelectedCoins) -> {
            if (numOfSelectedCoins > 0) {
                calculateSumBtn.setEnabled(true);
            } else {
                calculateSumBtn.setEnabled(false);
            }
        });

        OpenCVLoader.initDebug();

    }

    public void loadImage(View view) {
        if (PermissionManager.getPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                "Read external storage permission needed", PERMISSION_TO_READ_STORAGE)) {
            dispatchOpenGalleryIntent();
        }
    }

    private void dispatchOpenGalleryIntent() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_PICK_IMAGE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchTakePictureIntent();
                } else {
                    Toast.makeText(this, "Write permission not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSION_TO_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dispatchOpenGalleryIntent();
                } else {
                    Toast.makeText(this, "Read permission not granted", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void openCamera(View view) {
        if (PermissionManager.getPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "Camera permission needed", PERMISSION_TO_WRITE_STORAGE)) {
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoFileAbsolutePath = photoFile.getAbsolutePath();
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            Log.e(TAG, "createImageFile: Failed to create a File for photo", e);
            e.printStackTrace();
        }

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            viewModel.setImageOfCoins(photoFileAbsolutePath);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

        } else if (requestCode == GALLERY_PICK_IMAGE && resultCode == RESULT_OK) {
            InputStream photoInputStream;

            try {
                photoInputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Picture wasn't found!", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.setImageOfCoins(photoInputStream);

        } else if (requestCode == GALLERY_PICK_IMAGE) {
            Toast.makeText(this, "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculateSum(View view) {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }

    public void decThreshold(View view) {
        viewModel.decThreshold();
    }

    public void incThreshold(View view) {
        viewModel.incThreshold();
    }

    public void decDistance(View view) {
        viewModel.decDistance();
    }

    public void incDistance(View view) {
        viewModel.incDistance();
    }
}
