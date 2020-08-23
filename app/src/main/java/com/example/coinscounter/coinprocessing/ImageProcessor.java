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
import java.util.concurrent.Executor;

import javax.inject.Inject;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.CV_HOUGH_GRADIENT;
import static org.opencv.imgproc.Imgproc.GaussianBlur;
import static org.opencv.imgproc.Imgproc.HoughCircles;
import static org.opencv.imgproc.Imgproc.cvtColor;

public class ImageProcessor {
    private static final String TAG = ImageProcessor.class.getName();
    private final int IMAGE_WIDTH;
    // TODO use ExecutorService to shutdown process
    private final Executor EXECUTOR;

    @Inject
    public ImageProcessor(int imageWidth, Executor executor) {
        this.IMAGE_WIDTH = imageWidth;
        this.EXECUTOR = executor;
    }

    public void processImage(Bitmap image, int lowerThreshold, int minDist, ImageProcessorCallback callback) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                Mat resizedImageMat = getResizedMat(image);
                Mat circles = findCoinCircles(lowerThreshold, minDist, resizedImageMat.clone());
                Bitmap bitmapToDisplay = getBitmapToDisplay(resizedImageMat.clone(), circles);
                List<Bitmap> croppedCoinsList = getCroppedCoinsList(resizedImageMat.clone(), circles);
                callback.onComplete(bitmapToDisplay, croppedCoinsList);
            }
        });
    }

    private ArrayList<Bitmap> getCroppedCoinsList(Mat resizedImageMat, Mat circles) {
        ArrayList<Bitmap> croppedCoinsList = new ArrayList<>();
        for (int i = 0; i < circles.cols(); i++) {
            Log.d(TAG, "Cropping coin numb " + i);
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

//            Log.d(TAG, "Channels: " + croppedMat.channels());
//            Log.d(TAG, "Type: " + croppedMat.type());

//            cvtColor(croppedMat, croppedMat, COLOR_BGRA2RGBA);

            //Convert the Mat to Bitmap
            Bitmap croppedCoin = Bitmap.createBitmap(croppedMat.cols(), croppedMat.rows(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(croppedMat, croppedCoin);

            //Save the bitmap to the list for the ML model
            croppedCoinsList.add(croppedCoin);
        }

        Log.d(TAG, "Total cropped coins: " + croppedCoinsList.size());
        return croppedCoinsList;
    }

//    public void saveCoins(List<Bitmap> croppedCoinsList) throws IOException {
//        String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
//                "/CoinsCounter/SavedCoins";
//
//        Log.d(TAG, file_path);
//        File dir = new File(file_path);
//
//        if (!dir.exists())
//            dir.mkdirs();
//
//        FileOutputStream fOut = null;
//
//        for (Bitmap croppedCoin : croppedCoinsList) {
//            //Save the cropped coin bitmap to disk
//            File file = new File(dir, "CroppedCoin_" + System.currentTimeMillis() + ".png");
//            fOut = new FileOutputStream(file);
//            croppedCoin.compress(Bitmap.CompressFormat.PNG, 100, fOut);
//        }
//
//        if (fOut != null)
//            fOut.flush();
//
//        fOut.close();
//    }

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

    private Mat findCoinCircles(int lowerThreshold, int minDist, Mat matOfImage) {
        Mat circles = new Mat();
        //Converts the mat from CV_8UC4 to CV_8UC1 and applies GaussianBlur for processing
        cvtColor(matOfImage, matOfImage, COLOR_BGR2GRAY);
        GaussianBlur(matOfImage, matOfImage, new Size(9, 9), 3, 3);

        /// Apply the Hough Transform to find the circles
        HoughCircles(matOfImage, circles, CV_HOUGH_GRADIENT, 1, minDist, 100, lowerThreshold, 0, 0);

        Log.d(TAG, "Size of circles - " + circles.size());
        return circles;
//        Log.d(TAG, "Hough");
//        Log.d(TAG, "Mat: " + matOfImage.rows() + " " + matOfImage.cols());
    }

    private Mat getResizedMat(Bitmap image) {
        Mat src = new Mat();
        Utils.bitmapToMat(image, src);

        return resizeMat(src, (int) (src.size().width * 0.5));
    }

    private Mat resizeMat(Mat mat, int newImageWidth) {
        int adjustedHeight = newImageWidth * (int) mat.size().height / (int) mat.size().width;
        Size sz = new Size(newImageWidth, adjustedHeight);
        Imgproc.resize(mat, mat, sz);

        return mat;
    }

    public interface ImageProcessorCallback {
        void onComplete(Bitmap imageToDisplay, List<Bitmap> croppedCoinsList);
    }
}

