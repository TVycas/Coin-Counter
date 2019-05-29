package com.example.coinscounter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughCircles;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_TO_WRITE_STORAGE = 2;
    static final int PERMISSION_TO_READ_STORAGE = 3;
    static final String TAG = "MainActivity";

    String currentPhotoPath;
    Bitmap takenImage;
    ImageView imgView;
    TextView text;
    SeekBar seek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = findViewById(R.id.imageView);
        text = findViewById(R.id.threshText);
        seek = findViewById(R.id.seekBar);

        OpenCVLoader.initDebug();

        seek.setMax(150);
        seek.setProgress(50);

        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                Log.i("The threshold is: ", Integer.toString(progress));
                text.setText("Threshold:" + seek.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                //TODO maybe add functionality to upload a picture
                getCircles(BitmapFactory.decodeResource(getResources(), R.drawable.coins));
            }
        });

//        imgView.setOnTouchListener(this);


//        if (OpenCVLoader.initDebug()) {
//            Toast.makeText(this, "openCv successfully loaded", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "openCv cannot be loaded", Toast.LENGTH_SHORT).show();
//        }
    }

//    public boolean onTouch(View v, MotionEvent event) {
//        int action = event.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                downx = event.getX();
//                downy = event.getY();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                upx = event.getX();
//                upy = event.getY();
//                canvas.drawLine(downx, downy, upx, upy, paint);
//                imageView.invalidate();
//                downx = upx;
//                downy = upy;
//                break;
//            case MotionEvent.ACTION_UP:
//                upx = event.getX();
//                upy = event.getY();
//                canvas.drawLine(downx, downy, upx, upy, paint);
//                imageView.invalidate();
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    private void getCircles(Bitmap bm) {
        new ImageProcessing(this, 700, seek.getProgress()).execute(bm);
    }

    public void loadImage(View view) {
        imgView.setVisibility(View.INVISIBLE);


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getCircles(BitmapFactory.decodeResource(getResources(), R.drawable.coins));
            seek.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Camera permission needed", Toast.LENGTH_LONG).show();

            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_TO_READ_STORAGE);
        }

    }

    public void openCamera(View view) {
        imgView.setVisibility(View.INVISIBLE);

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
            seek.setVisibility(View.VISIBLE);
            text.setVisibility(View.VISIBLE);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Camera permission needed", Toast.LENGTH_LONG).show();

            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_TO_WRITE_STORAGE);
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
            takenImage = BitmapFactory.decodeFile(currentPhotoPath);
            getCircles(takenImage);
        } else {
            Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_TO_WRITE_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    dispatchTakePictureIntent();
                    seek.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Write permission not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }

            case PERMISSION_TO_READ_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    getCircles(BitmapFactory.decodeResource(getResources(), R.drawable.coins));
                    seek.setVisibility(View.VISIBLE);
                    text.setVisibility(View.VISIBLE);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Read permission not granted", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    private static class ImageProcessing extends AsyncTask<Bitmap, String, Bitmap> {
        private static final String TAG = "ImageProcessing";
        private final int imageWidth;
        private final int lowerThreshold;
        private WeakReference<MainActivity> activityWeakReference;


        ImageProcessing(MainActivity activity, int imageWidth, int lowerThreshold) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
            this.imageWidth = imageWidth;
            this.lowerThreshold = lowerThreshold;
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

            //create a Mat out of bitmap
            Mat mat = new Mat();
            Mat src = new Mat();
//            Mat mat = new Mat(bitmaps[0].getHeight(), bitmaps[0].getWidth(), CvType.CV_8UC1);

            Utils.bitmapToMat(bitmaps[0], mat);
            Utils.bitmapToMat(bitmaps[0], src);

            //converts CV_8UC4 to CV_8UC1 for processing
            Imgproc.cvtColor(mat, mat, Imgproc.COLOR_BGR2GRAY);

            publishProgress("Starting convolution..."); // reikia naudotis library
            GaussianBlur(mat, mat, new Size(9, 9), 3, 3);

            publishProgress("Starting sum calculations..."); // Actual magic
            Mat circles = new Mat();

//            Core.inRange(mat, new Scalar(50, 100, 0), new Scalar(95, 255, 255), mat);

            /// Apply the Hough Transform to find the circles
            //TODO upper threshold needs to be not this
            HoughCircles(mat, circles, CV_HOUGH_GRADIENT, 1, 10, 150, lowerThreshold, 0, 0);

            Log.d(TAG, "Size of circles - " + circles.size());
            Log.d(TAG, "Hough");

            for (int i = 0; i < circles.cols(); i++) {
                double[] vCircle = circles.get(0, i);

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);

//                Imgproc.circle(src, pt, radius, new Scalar(255, 0, 0), 1);
//            }
//
//            Bitmap resultBitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(src, resultBitmap);

                Imgproc.circle(mat, pt, radius, new Scalar(255, 0, 0), 2);
            }

            Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, resultBitmap);

            //TODO get rid of this
            float degrees = 90;//rotation degree
            Matrix matrix = new Matrix();
            matrix.setRotate(degrees);
            resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight(), matrix, true);

            return resultBitmap;
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

            activity.text.setText("Threshold: " + lowerThreshold);
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
