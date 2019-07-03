package com.example.coinscounter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.coinscounter.utills.PermissionManager;
import com.example.coinscounter.viewmodel.MainActivityViewModel;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_TO_WRITE_STORAGE = 2;
    static final int PERMISSION_TO_READ_STORAGE = 3;
    static final String TAG = "MainActivity";

    private MainActivityViewModel viewModel;
    Button takePictureBtn;
    Button calculateSumBtn;
    Button loadImgBtn;
    ImageView imgView;
    TextView threshText;
    SeekBar threshSeek;
    TextView distText;
    SeekBar distSeek;
    int[] viewCoords = new int[2];

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        takePictureBtn = findViewById(R.id.takePictureButton);
        calculateSumBtn = findViewById(R.id.calculateSum);
        loadImgBtn = findViewById(R.id.loadImgButton);
        imgView = findViewById(R.id.imageView);
        threshText = findViewById(R.id.threshTextView);
        threshSeek = findViewById(R.id.threshSeekBar);
        distText = findViewById(R.id.distTextView);
        distSeek = findViewById(R.id.distSeekBar);

        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        viewModel.getProcessedBitmap().observe(this, (processedBitmap) -> {
            imgView.setImageBitmap(processedBitmap);
            imgView.setVisibility(View.VISIBLE);
        });

        imgView.getLocationOnScreen(viewCoords);

        OpenCVLoader.initDebug();

        threshSeek.setMax(100);
        threshSeek.setProgress(24);

        distSeek.setMax(150);
        distSeek.setProgress(33);

        //TODO reuse the code
        threshSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                threshText.setText("Threshold:" + threshSeek.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.saveThreshSeekProgress(threshSeek.getProgress());
                viewModel.findCirclesInImage();
            }
        });

        distSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distText.setText("MinDist:" + distSeek.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                viewModel.savetDistSeekProgress(distSeek.getProgress());
                viewModel.findCirclesInImage();
            }
        });
//
//        imgView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent motionEvent) {
//                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
//                    //TODO fix variable declarations
//                    int touchX = (int) motionEvent.getX();
//                    int touchY = (int) motionEvent.getY();
//
//                    int x = touchX - viewCoords[0]; // viewCoords[0] is the X coordinate
//                    int y = touchY - viewCoords[1]; // viewCoords[1] is the y coordinate
//                    Log.v(TAG, "X= " + x + " Y= " + y);
//                    selectCoinOnLocation(x, y);
//                }
//                return true;
//            }
//        });

//        imgView.setOnTouchListener(this);


//        if (OpenCVLoader.initDebug()) {
//            Toast.makeText(this, "openCv successfully loaded", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "openCv cannot be loaded", Toast.LENGTH_SHORT).show();
//        }
    }
    
    //TODO make this work with gallery
    private void setUpLoadingImage() {
        imgView.setVisibility(View.INVISIBLE);
        imgView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.coins2));

    }

    private void setUpTakingPicture() {
        imgView.setVisibility(View.INVISIBLE);
        dispatchTakePictureIntent();
    }

    private void setSeekVisibility(boolean visible){
        if(visible){
            threshSeek.setVisibility(View.VISIBLE);
            threshText.setVisibility(View.VISIBLE);
            threshText.setText("Threshold:" + threshSeek.getProgress());
            distSeek.setVisibility(View.VISIBLE);
            distText.setVisibility(View.VISIBLE);
            distText.setText("MinDist:" + distSeek.getProgress());
        }else{
            threshSeek.setVisibility(View.GONE);
            threshText.setVisibility(View.GONE);
            distSeek.setVisibility(View.GONE);
            distText.setVisibility(View.GONE);
        }
    }

    public void loadImage(View view) {
        if (PermissionManager.getPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE,
                "Read external storage permission needed", PERMISSION_TO_READ_STORAGE)) {
            setUpLoadingImage();
        }
    }

    public void openCamera(View view) {
        if (PermissionManager.getPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                "Camera permission needed", PERMISSION_TO_WRITE_STORAGE)) {
            setUpTakingPicture();
        }
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
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        viewModel.setImagePath(image.getAbsolutePath());
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            viewModel.saveThreshSeekProgress(threshSeek.getProgress());
            viewModel.savetDistSeekProgress(distSeek.getProgress());
            viewModel.findCirclesInImage();
            setSeekVisibility(true);
        } else {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    public void calculateSum(View view) {
        if (viewModel.calculateSum()) {
            Intent intent = new Intent(this, ResultsActivity.class);
            startActivity(intent);
            setSeekVisibility(true);
        } else {
            Toast.makeText(this, "No coins selected", Toast.LENGTH_LONG).show();
        }
    }
}
