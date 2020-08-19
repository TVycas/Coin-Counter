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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.coinscounter.R;
import com.example.coinscounter.utills.PermissionManager;
import com.example.coinscounter.viewmodel.MainActivityViewModel;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

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
    private TextView threshText;
    private SeekBar threshSeek;
    private TextView distText;
    private SeekBar distSeek;

    private String photoFileAbsolutePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        calculateSumBtn = findViewById(R.id.calculateSum);
        imgView = findViewById(R.id.imageView);
        threshText = findViewById(R.id.threshTextView);
        threshSeek = findViewById(R.id.threshSeekBar);
        distText = findViewById(R.id.distTextView);
        distSeek = findViewById(R.id.distSeekBar);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        viewModel.getImageToDisplay().observe(this, (processedBitmap) -> {
            imgView.setImageBitmap(processedBitmap);
            imgView.setVisibility(View.VISIBLE);
        });

        viewModel.getNumOfSelectedCoins().observe(this, (numOfSelectedCoins) -> {
            if (numOfSelectedCoins > 0) {
                calculateSumBtn.setVisibility(View.VISIBLE);
            } else {
                calculateSumBtn.setVisibility(View.INVISIBLE);
                // TODO post message for the user to select coins
            }
        });

        OpenCVLoader.initDebug();

        setUpSeekBars();
    }

    private void setUpSeekBars() {
        threshSeek.setMax(100);
        threshSeek.setProgress(40);

        distSeek.setMax(150);
        distSeek.setProgress(50);

        threshSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshText.setText("Threshold: " + threshSeek.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {/* no-op */}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.processCoinImage(threshSeek.getProgress(), distSeek.getProgress());
            }
        });

        distSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distText.setText("MinDist: " + distSeek.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {/* no-op */}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.processCoinImage(distSeek.getProgress(), distSeek.getProgress());
            }
        });
    }

    private void setSeekVisibility(boolean visible) {
        if (visible) {
            threshSeek.setVisibility(View.VISIBLE);
            threshText.setVisibility(View.VISIBLE);
            threshText.setText("Threshold: " + threshSeek.getProgress());
            distSeek.setVisibility(View.VISIBLE);
            distText.setVisibility(View.VISIBLE);
            distText.setText("MinDist: " + distSeek.getProgress());

            viewModel.processCoinImage(threshSeek.getProgress(), distSeek.getProgress());
        } else {
            // TODO do I need to make theses invisible?
            imgView.setVisibility(View.GONE);
            threshSeek.setVisibility(View.INVISIBLE);
            threshText.setVisibility(View.INVISIBLE);
            distSeek.setVisibility(View.INVISIBLE);
            distText.setVisibility(View.INVISIBLE);
        }
    }

    public void loadImage(View view) {
        if (PermissionManager.getPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                "Read external storage permission needed", PERMISSION_TO_READ_STORAGE)) {
            setUpLoadingImage();
        }
    }

    private void setUpLoadingImage() {
        setSeekVisibility(false);
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, GALLERY_PICK_IMAGE);
    }

    public void openCamera(View view) {
        if (PermissionManager.getPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "Camera permission needed", PERMISSION_TO_WRITE_STORAGE)) {
            setUpTakingPicture();
        }
    }

    private void setUpTakingPicture() {
        setSeekVisibility(false);
        dispatchTakePictureIntent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpTakingPicture();
                } else {
                    Toast.makeText(this, "Write permission not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSION_TO_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setUpLoadingImage();
                } else {
                    Toast.makeText(this, "Read permission not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile();
            photoFileAbsolutePath = photoFile.getAbsolutePath();
            // Continue only if the File was successfully created
            if (photoFile != null) {
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
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
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
            setSeekVisibility(true);

        } else if (requestCode == REQUEST_IMAGE_CAPTURE) {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();

        } else if (requestCode == GALLERY_PICK_IMAGE && resultCode == RESULT_OK) {
            InputStream photoInputStream;

            try {
                photoInputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Picture wasn't found!", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.setImageOfCoins(photoInputStream);
            setSeekVisibility(true);

        } else if (requestCode == GALLERY_PICK_IMAGE) {
            Toast.makeText(this, "Picture wasn't chosen!", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculateSum(View view) {
        Intent intent = new Intent(this, ResultsActivity.class);
        startActivity(intent);
    }
}
