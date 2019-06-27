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
    private final int dist;
    private Mat circles;
    private Model model;


    public ImageProcessing(Model model, int imageWidth, int lowerThreshold, int dist) {
        circles = new Mat();

        this.model = model;
        this.imageWidth = imageWidth;
        this.lowerThreshold = lowerThreshold;
        this.dist = dist;
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

        //TODO
        //This is the process grayscale image that will be later used to get the cropped circles and so on.
        //It's different form the resultBitmap as it is not just the rescaled original image
        //TODO this whole check the referece thing maybe could be moved to a method cause it's done a few times


        model.setProcessedMat(src);


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
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        model.setProcessedBitmap(bitmap);
        model.setCircles(circles);
    }
}
