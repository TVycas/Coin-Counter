package com.example.coinscounter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
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

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughCircles;
import static org.opencv.imgproc.Imgproc.blur;
import static org.opencv.imgproc.Imgproc.cvtColor;
import static org.opencv.imgproc.Imgproc.intersectConvexConvex;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int PERMISSION_TO_WRITE_STORAGE = 2;
    static final int PERMISSION_TO_READ_STORAGE = 3;
    static final String TAG = "MainActivity";

    String currentPhotoPath;
    //TODO if I have the path why do I need the bitmap?
    Bitmap takenImage ;
    ImageView imgView;
    TextView threshText;
    SeekBar threshSeek;
    TextView distText;
    SeekBar distSeek;
    int[] viewCoords = new int[2];
    Mat circles;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = findViewById(R.id.imageView);
        threshText = findViewById(R.id.threshTextView);
        threshSeek = findViewById(R.id.threshSeekBar);
        distText = findViewById(R.id.distTextView);
        distSeek = findViewById(R.id.distSeekBar);

        //TODO remove
        takenImage = BitmapFactory.decodeResource(getResources(), R.drawable.coins);

        imgView.getLocationOnScreen(viewCoords);

        OpenCVLoader.initDebug();

        threshSeek.setMax(100);
        threshSeek.setProgress(50);

        distSeek.setMax(150);
        distSeek.setProgress(50);

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
                //TODO maybe add functionality to upload a picture
                getCircles(BitmapFactory.decodeResource(getResources(), R.drawable.coins));
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
                //TODO maybe add functionality to upload a picture
                getCircles(BitmapFactory.decodeResource(getResources(), R.drawable.coins));
            }
        });

        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    //TODO fix variable declarations
                    int touchX = (int) motionEvent.getX();
                    int touchY = (int) motionEvent.getY();

                    int x = touchX - viewCoords[0]; // viewCoords[0] is the X coordinate
                    int y = touchY - viewCoords[1]; // viewCoords[1] is the y coordinate
                    Log.v(TAG, "X= " + x + " Y= " + y);
                    selectCoinOnLocation(x, y);
                }
                return true;
            }
        });

//        imgView.setOnTouchListener(this);


//        if (OpenCVLoader.initDebug()) {
//            Toast.makeText(this, "openCv successfully loaded", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "openCv cannot be loaded", Toast.LENGTH_SHORT).show();
//        }
    }

    public void calculateSum(View view) {
        //steps
        //1 determine one of the coins worth
        //2 use it and use the proportions based on type of money to calc the rest of the coins worth
        //3 sum it up and display

        int[] coinWorth = new int[circles.cols()];
        coinWorth[0] = askCoinWorth(0);
    }

    private int askCoinWorth(int index) {
        //TODO remove the 700
        Bitmap rotatedImage = BitmapFactory.decodeResource(getResources(), R.drawable.coins);
        int adjustedHeigth = 700 * rotatedImage.getHeight() / rotatedImage.getWidth();
        rotatedImage = Bitmap.createScaledBitmap(rotatedImage, 700, adjustedHeigth, true);

//        //TODO get rid of this
//        float degrees = 90;//rotation degree
//        Matrix matrix = new Matrix();
//        matrix.setRotate(degrees);
//        rotatedImage = Bitmap.createBitmap(rotatedImage, 0, 0, rotatedImage.getWidth(), rotatedImage.getHeight(), matrix, true);

        double[] circle = circles.get(0, index);
        Log.d(TAG, "Bitmap: " + rotatedImage.getHeight() + " " + rotatedImage.getWidth());
        Log.d(TAG, (float) circle[0] + " " + (float)circle[1] + " " + (float)circle[2]);

        Mat mat = new Mat();
        Utils.bitmapToMat(rotatedImage, mat);
        Imgproc.circle(mat, new Point(circle[0], circle[1]), (int)circle[2], new Scalar(255, 0, 0), 2);
        showMat(mat);

//        Canvas canvas = new Canvas(rotatedImage);
//        Paint paint = new Paint();
//        paint.setColor(Color.RED);
//        canvas.drawBitmap(rotatedImage, new Matrix(), null);
//        canvas.drawCircle((float) circle[0], (float)circle[1], (float)circle[2], paint);
//        imgView.setImageBitmap(rotatedImage);

        return 0;
    }


    //TODO remove this
    private void showMat(Mat mat){
        Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, resultBitmap);
        imgView.setImageBitmap(resultBitmap);
    }

    private void selectCoinOnLocation(int x, int y){
        //TODO get rid of this
        float degrees = 90;//rotation degree
        Matrix matrix = new Matrix();
        matrix.setRotate(degrees);
        Bitmap rotatedImage = BitmapFactory.decodeResource(getResources(), R.drawable.coins);
        rotatedImage = Bitmap.createBitmap(rotatedImage, 0, 0, rotatedImage.getWidth(), rotatedImage.getHeight(), matrix, true);

        //Bitmap to mat
        Mat fullMat = new Mat();
        Utils.bitmapToMat(rotatedImage, fullMat);

        //Resize the mat to be the same as imageView
        Size sz = new Size(imgView.getWidth(),imgView.getHeight());
        Imgproc.resize( fullMat, fullMat, sz );

        //Turning to grayscale
        cvtColor(fullMat, fullMat, COLOR_BGR2GRAY);

        //TODO how to determine the kernel size?
        blur( fullMat, fullMat, new Size( 40, 40), new Point(-1,-1));

        int diameter = findCoinDiameter(x, y, fullMat);

        showMat(fullMat);

//        Rect roi = new Rect(x, y, width, height);
//        Mat cropped = new Mat(fullMat, roi);

    }

    private int findCoinDiameter(int x, int y, Mat imgMat) {
        double[] currentColor = imgMat.get(y,x);
        Log.v(TAG, "Value = " + currentColor[0]);

        int backgroundValue = findBackgroundValue(imgMat);


        showMat(imgMat);
        return 0;
    }

    private int findBackgroundValue(Mat imgMat) {
        int averageValue  = 0;
        if(!circles.empty()) {
            for (int i = 0; i < circles.cols() || i == 2; i++) {
                double[] vCircle = circles.get(0, i);

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);

                averageValue = (int) (Math.round(vCircle[0]) + Math.round(vCircle[2]) + 5);
            }
        }else{
            //TODO find the most common value for image?
        }

        return averageValue;
    }


    private void getCircles(Bitmap bm) {
        new ImageProcessing(this, 700, threshSeek.getProgress(), distSeek.getProgress()).execute(bm);
    }

    public void loadImage(View view) {
        imgView.setVisibility(View.INVISIBLE);


        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            getCircles(BitmapFactory.decodeResource(getResources(), R.drawable.coins));
            threshSeek.setVisibility(View.VISIBLE);
            threshText.setVisibility(View.VISIBLE);

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
            threshSeek.setVisibility(View.VISIBLE);
            threshText.setVisibility(View.VISIBLE);
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
                    threshSeek.setVisibility(View.VISIBLE);
                    threshText.setVisibility(View.VISIBLE);
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
                    threshSeek.setVisibility(View.VISIBLE);
                    threshText.setVisibility(View.VISIBLE);
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
        private final int dist;
        private WeakReference<MainActivity> activityWeakReference;
        private Mat circles;


        ImageProcessing(MainActivity activity, int imageWidth, int lowerThreshold, int dist) {
            activityWeakReference = new WeakReference<MainActivity>(activity);
            this.imageWidth = imageWidth;
            this.lowerThreshold = lowerThreshold;
            this.dist = dist;
            circles = new Mat();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.threshText.setVisibility(View.VISIBLE);
        }

        @Override
        protected Bitmap doInBackground(Bitmap... bitmaps) {


            int adjustedHeigth = imageWidth * bitmaps[0].getHeight() / bitmaps[0].getWidth();
//            bitmaps[0] = Bitmap.createScaledBitmap(bitmaps[0], imageWidth, adjustedHeigth, true);

            Mat src = new Mat();
            Utils.bitmapToMat(bitmaps[0], src);

            publishProgress("Starting to resize...");

            Size sz = new Size(imageWidth, adjustedHeigth);
            Imgproc.resize( src, src, sz );


            //create a Mat out of bitmap
            Mat mat = src.clone();

//            Utils.bitmapToMat(bitmaps[0], mat);

            publishProgress("Turning grayscale...");
            //converts CV_8UC4 to CV_8UC1 for processing
            cvtColor(mat, mat, COLOR_BGR2GRAY);

            publishProgress("Starting convolution...");
            //TODO maybe the average is better?
            GaussianBlur(mat, mat, new Size(9, 9), 3, 3);
//            blur( mat, mat, new Size( 10, 10), new Point(-1,-1));
            publishProgress("Starting sum calculations..."); // Actual magic

//            Core.inRange(mat, new Scalar(50, 100, 0), new Scalar(95, 255, 255), mat);

            /// Apply the Hough Transform to find the circles
            //TODO upper threshold needs to be not this
            HoughCircles(mat, circles, CV_HOUGH_GRADIENT, 1, dist, 150, lowerThreshold, 0, 0);

            Log.d(TAG, "Size of circles - " + circles.size());
            Log.d(TAG, "Hough");

            Log.d(TAG, "Mat: " + mat.rows() + " " + mat.cols());

            for (int i = 0; i < circles.cols(); i++) {
                double[] vCircle = circles.get(0, i);

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);

//                //TODO fix this to be red
//                Imgproc.circle(src, pt, radius, new Scalar(0, 255, 0, 0), 2); //The mat is in 4 channels
//            }
//
//            Bitmap resultBitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
//            Utils.matToBitmap(src, resultBitmap);

                Imgproc.circle(mat, pt, radius, new Scalar(255, 0, 0), 2);
            }

            Bitmap resultBitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mat, resultBitmap);

//            //TODO get rid of this
//            float degrees = 90;//rotation degree
//            Matrix matrix = new Matrix();
//            matrix.setRotate(degrees);
//            resultBitmap = Bitmap.createBitmap(resultBitmap, 0, 0, resultBitmap.getWidth(), resultBitmap.getHeight(), matrix, true);

            return resultBitmap;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            activity.threshText.setText(values[0]);
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

            activity.threshText.setText("Threshold: " + lowerThreshold);

            activity.circles = circles;
        }

        //TODO remove
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
