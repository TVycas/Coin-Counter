package com.example.coinscounter.coinprocessing;

import android.graphics.Bitmap;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughCircles;
import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * Uses ExecutorService to asynchronously find coins in an image using OpenCV's HoughCircles.
 */
public class ImageProcessor {
    private static final String TAG = ImageProcessor.class.getName();
    /**
     * The width, in pixels, to resize the image to.
     */
    private final int IMAGE_WIDTH;
    private final ExecutorService EXECUTOR_SERVICE;
    private Future<?> future;

    @Inject
    public ImageProcessor(int imageWidth, ExecutorService executor) {
        this.IMAGE_WIDTH = imageWidth;
        this.EXECUTOR_SERVICE = executor;
    }

    public void cancelExecution() {
        if (future != null && !future.isCancelled()) {
            boolean cancelled = future.cancel(true);
            Log.i(TAG, "cancelExecution: previous process canceled: " + cancelled);
        }
    }

    /**
     * Asynchronously process the image, finding the coins, making an image to display for the user and cropping each of the found coins.
     *
     * @param image          Image bitmap to process.
     * @param lowerThreshold Lower threshold for HoughCircles
     * @param minDist        Minimum distance for HoughCircles.
     * @param callback       Callback to return the processed coins and image to display.
     */
    public void processImage(Bitmap image, int lowerThreshold, int minDist, ImageProcessorCallback callback) {
        future = EXECUTOR_SERVICE.submit(new Runnable() {
            @Override
            public void run() {
                Mat resizedImageMat;
                Mat circles;
                // In case OpenCV crashes, the method still returns
                Bitmap bitmapToDisplay = image;
                List<Bitmap> croppedCoinsList = new ArrayList<>();

                if (!Thread.interrupted()) {
                    resizedImageMat = getResizedMat(image);
                } else {
                    return;
                }
                if (!Thread.interrupted()) {
                    circles = findCoinCircles(lowerThreshold, minDist, resizedImageMat.clone());
                } else {
                    return;
                }
                if (!Thread.interrupted()) {
                    bitmapToDisplay = getBitmapToDisplay(resizedImageMat.clone(), circles);
                } else {
                    return;
                }
                if (!Thread.interrupted()) {
                    croppedCoinsList = getCroppedCoinsList(resizedImageMat.clone(), circles);
                } else {
                    return;
                }
                callback.onComplete(bitmapToDisplay, croppedCoinsList);
            }
        });
    }

    /**
     * Converts the image to a Mat and resizes it, for further processing.
     *
     * @param image Bitmap to resize.
     * @return Resized Mat of the image.
     */
    private Mat getResizedMat(Bitmap image) {
        Mat src = new Mat();
        Utils.bitmapToMat(image, src);

        return resizeMat(src, (int) (src.size().width * 0.5));
    }

    /**
     * Applies Grayscale and Gaussian Blur before running the Hough Transformation to find circles (which are hopefully coins) in the image.
     *
     * @param lowerThreshold Lower threshold for HoughCircles
     * @param minDist        Minimum distance for HoughCircles.
     * @param matOfImage     A Mat object of the image.
     * @return A Mat describing the circles found in the image.
     */
    private Mat findCoinCircles(int lowerThreshold, int minDist, Mat matOfImage) {
        Mat circles = new Mat();
        //Converts the mat from CV_8UC4 to CV_8UC1 and applies GaussianBlur for processing
        cvtColor(matOfImage, matOfImage, COLOR_BGR2GRAY);
        GaussianBlur(matOfImage, matOfImage, new Size(9, 9), 3, 3);

        /// Apply the Hough Transform to find the circles
        HoughCircles(matOfImage, circles, CV_HOUGH_GRADIENT, 1, minDist, 100, lowerThreshold, 0, 0);

        return circles;
    }

    /**
     * Crops the coins out of the image.
     *
     * @param resizedImageMat A Mat used in the Hough Transformation
     * @param circles         The circles found by the Hough Transformation.
     * @return A bitmap ArrayList, where each element is a cropped coin.
     */
    private ArrayList<Bitmap> getCroppedCoinsList(Mat resizedImageMat, Mat circles) {
        ArrayList<Bitmap> croppedCoinsList = new ArrayList<>();
        for (int i = 0; i < circles.cols(); i++) {
            double[] vCircle = circles.get(0, i);

            Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int) Math.round(vCircle[2]);

            //Create a mask to only save the coin and not the background
            Mat mask = Mat.zeros(resizedImageMat.rows(), resizedImageMat.cols(), 0);

            Imgproc.circle(mask, pt, radius, new Scalar(255, 255, 255), -1, 8, 0);
            Mat matToSave = new Mat();

            resizedImageMat.copyTo(matToSave, mask);

            //Add 5% to the radius to make a cropped box around the coin
            radius *= 1.05;

            //Starting point for Rect
            int x = (int) (pt.x - radius);
            int y = (int) (pt.y - radius);

            //Create a new mat to store the cropped coin image
            Rect coinRect = new Rect(x, y, radius * 2, radius * 2);
            Mat croppedMat = new Mat(matToSave, coinRect);

            //Convert the Mat to Bitmap
            Bitmap croppedCoin = Bitmap.createBitmap(croppedMat.cols(), croppedMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(croppedMat, croppedCoin);

            //Save the bitmap to a list
            croppedCoinsList.add(croppedCoin);
        }

        Log.d(TAG, "Total cropped coins: " + croppedCoinsList.size());
        return croppedCoinsList;
    }

    /**
     * Draws red circles around the found coins and returns a bitmap to display to the user.
     *
     * @param matToDisplay A Mat varsion of the image.
     * @param circles      Circles to draw.
     * @return A bitmap to display to the user.
     */
    private Bitmap getBitmapToDisplay(Mat matToDisplay, Mat circles) {
        //Draw red circles round the found coins
        for (int i = 0; i < circles.cols(); i++) {
            double[] vCircle = circles.get(0, i);

            Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
            int radius = (int) Math.round(vCircle[2]);

            Imgproc.circle(matToDisplay, pt, radius, new Scalar(255, 0, 0, 100), 8); //The mat is in 4 channels (RGBA)
        }

        //Resize and convert mat to bitmap to display it for the user
        resizeMat(matToDisplay, IMAGE_WIDTH);
        Bitmap resultBitmap = Bitmap.createBitmap(matToDisplay.cols(), matToDisplay.rows(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(matToDisplay, resultBitmap);
        return resultBitmap;
    }

    /**
     * Resizes a Mat based in width
     *
     * @param mat           A Mat to resize
     * @param newImageWidth The new image width
     * @return Resized Mat
     */
    private Mat resizeMat(Mat mat, int newImageWidth) {
        int adjustedHeight = newImageWidth * (int) mat.size().height / (int) mat.size().width;
        Size sz = new Size(newImageWidth, adjustedHeight);
        Imgproc.resize(mat, mat, sz);

        return mat;
    }

    /**
     * An interface to return the results of the processing to the caller.
     */
    public interface ImageProcessorCallback {
        void onComplete(Bitmap imageToDisplay, List<Bitmap> croppedCoinsList);
    }
}

