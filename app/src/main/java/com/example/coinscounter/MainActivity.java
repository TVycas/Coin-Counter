package com.example.coinscounter;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.support.v7.app.AlertDialog.*;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_TO_USE_STORAGE = 2;
    private static final String TAG = "MainActivity";
    String currentPhotoPath;
    ImageView imgView;
    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = findViewById(R.id.imageView);
        text = findViewById(R.id.textView);

        if(OpenCVLoader.initDebug()){
            Toast.makeText(this, "openCv successfully loaded", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "openCv cannot be loaded", Toast.LENGTH_SHORT).show();
        }
    }

    public void openCamera(View view) {
        imgView.setVisibility(View.INVISIBLE);

        if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Camera permission needed", Toast.LENGTH_LONG).show();

            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_TO_USE_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_USE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                Log.d(TAG, "grantResults.length = " + grantResults.length + "\n grantResults[0] = " + grantResults[0] + " " + PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    dispatchTakePictureIntent();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Camera permission not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
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
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap takenImage = BitmapFactory.decodeFile(currentPhotoPath);
            new ImageProcessing(this, 700).execute(takenImage);
        } else {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    private static class ImageProcessing extends AsyncTask<Bitmap, String, Bitmap> {
        private static final String TAG = "ImageProcessing";
        private WeakReference<MainActivity> activityWeakReference;
        private final int imageWidth;


        ImageProcessing(MainActivity activity, int imageWidth) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
            this.imageWidth = imageWidth;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.text.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {
            publishProgress("Starting to compress...");

            int adjustedHeigth = imageWidth * bitmaps[0].getHeight() / bitmaps[0].getWidth();
            bitmaps[0] = Bitmap.createScaledBitmap(bitmaps[0], imageWidth, adjustedHeigth, true);

            publishProgress("Turning grayscale...");
            bitmaps[0] = RGBtoGrayscale(bitmaps[0]);

            publishProgress("Starting convolution...");
            int[][] LoGKernel = {
                    {0, 0, 1, 0, 0},
                    {0, 1, 2, 1, 0},
                    {1, 2, -16, 2, 1},
                    {0, 1, 2, 1, 0},
                    {0, 0, 1, 0, 0}
            };
            convoluteImg(bitmaps[0]);

            publishProgress("Starting sum calculations...");

            return bitmaps[0];
        }

        private void convoluteImg(Bitmap bitmap) {



        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.text.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.imgView.setImageBitmap(bitmap);
            activity.imgView.setVisibility(View.VISIBLE);

            activity.text.setText("");
            activity.text.setVisibility(View.INVISIBLE);
        }

        private Bitmap RGBtoGrayscale(Bitmap img) {
            float[] matrix = new float[]{
                    0.3f, 0.59f, 0.11f, 0, 0,
                    0.3f, 0.59f, 0.11f, 0, 0,
                    0.3f, 0.59f, 0.11f, 0, 0,
                    0, 0, 0, 1, 0,};

            Bitmap dest = Bitmap.createBitmap(
                    img.getWidth(),
                    img.getHeight(),
                    img.getConfig());

            Canvas canvas = new Canvas(dest);
            Paint paint = new Paint();
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            paint.setColorFilter(filter);
            canvas.drawBitmap(img, 0, 0, paint);

            return dest;
        }
    }
}
