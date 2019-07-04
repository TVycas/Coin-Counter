package com.example.coinscounter.utills;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.coinscounter.model.Model;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughCircles;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class ImageProcessing extends AsyncTask<Bitmap, String, Bitmap> {
    private static final String TAG = "ImageProcessing";
    private final int imageWidth;
    private final int lowerThreshold;
    private final int minDist;
    private Mat circles;
    private Model model;


    public ImageProcessing(Model model, int imageWidth, int lowerThreshold, int dist) {
        circles = new Mat();

        this.model = model;
        this.imageWidth = imageWidth;
        this.lowerThreshold = lowerThreshold;
        this.minDist = dist;
    }

    @Override
    protected Bitmap doInBackground(Bitmap... bitmaps) {


//            bitmaps[0] = Bitmap.createScaledBitmap(bitmaps[0], imageWidth, adjustedHeigth, true);


        //create a Mat out of the bitmap
        Mat src = new Mat();
        Utils.bitmapToMat(bitmaps[0], src);

        src = resizeMat(src, (int) (src.size().width * 0.5));

        model.setProcessedMat(src);
        Mat mat = src.clone();

//            Utils.bitmapToMat(bitmaps[0], mat);
        if (!isCancelled()) {
            publishProgress("Turning grayscale...");
            //converts CV_8UC4 to CV_8UC1 for processing
            cvtColor(mat, mat, COLOR_BGR2GRAY);

            publishProgress("Starting convolution...");
            GaussianBlur(mat, mat, new Size(9, 9), 3, 3);
//            blur( mat, mat, new Size( 10, 10), new Point(-1,-1));

        }

        publishProgress("Starting sum calculations..."); // Actual magic

        /// Apply the Hough Transform to find the circles
        if (!isCancelled()) {
            HoughCircles(mat, circles, CV_HOUGH_GRADIENT, 1, minDist, 100, lowerThreshold, 0, 0);
        }

        Log.d(TAG, "Size of circles - " + circles.size());
        Log.d(TAG, "Hough");
        Log.d(TAG, "Mat: " + mat.rows() + " " + mat.cols());

        if (!isCancelled()) {

            for (int i = 0; i < circles.cols(); i++) {
                double[] vCircle = circles.get(0, i);

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int) Math.round(vCircle[2]);

                Imgproc.circle(src, pt, radius, new Scalar(255, 0, 0, 100), 8); //The mat is in 4 channels (RGBA)
            }

        }

        publishProgress("Starting to resize...");
//
//        int adjustedHeigth = imageWidth * bitmaps[0].getHeight() / bitmaps[0].getWidth();
//        Size sz = new Size(imageWidth, adjustedHeigth);
//        Imgproc.resize(src, src, sz);

        src = resizeMat(src, imageWidth);

        Bitmap resultBitmap = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(src, resultBitmap);


        return resultBitmap;
    }


    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        model.setProcessedBitmap(bitmap);
        model.setCircles(circles);
    }

    private Mat resizeMat(Mat mat, int newImageWidth) {
        int adjustedHeigth = newImageWidth * (int) mat.size().height / (int) mat.size().width;
        Size sz = new Size(newImageWidth, adjustedHeigth);
        Imgproc.resize(mat, mat, sz);

        return mat;
    }

}
