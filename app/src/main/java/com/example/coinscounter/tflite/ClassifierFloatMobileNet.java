package com.example.coinscounter.tflite;

import java.io.IOException;
import java.nio.MappedByteBuffer;

/** This TensorFlowLite classifier works with the float MobileNet model. */
public class ClassifierFloatMobileNet extends Classifier {

  /** MobileNet requires additional normalization of the used input. */
  private static final float IMAGE_MEAN = 127.5f;
  private static final float IMAGE_STD = 127.5f;

  /**
   * An array to hold inference results, to be feed into Tensorflow Lite as outputs. This isn't part
   * of the super class, because we need a primitive array here.
   */
  private float[][] labelProbArray = null;

  /**
   * Initializes a {@code ClassifierFloatMobileNet}.
   *
   */
  public ClassifierFloatMobileNet(MappedByteBuffer modelFile, Device device, int numThreads)
      throws IOException {
    super(modelFile, device, numThreads);
    labelProbArray = new float[1][getNumLabels()];

  }

  @Override
  public int getImageSizeX() {
    return 150;
  }

  @Override
  public int getImageSizeY() {
    return 150;
  }

  @Override
  protected String getModelPath() {
    return "thirdModel.tflite";
  }

  @Override
  protected String getLabelPath() {
    return "labels.txt";
  }

  @Override
  protected int getNumBytesPerChannel() {
    return 4; // Float.SIZE / Byte.SIZE;
  }

  @Override
  protected void addPixelValue(int pixelValue) {
    //Loading pixels in RGBA order (from the initial ARGB)
    imgData.putFloat(((pixelValue >> 16) & 0xFF) / 255.f);
    imgData.putFloat(((pixelValue >> 8) & 0xFF) / 255.f);
    imgData.putFloat((pixelValue & 0xFF) / 255.f);
    imgData.putFloat(((pixelValue >> 24) & 0xFF) / 255.f);
  }

  @Override
  protected float getProbability(int labelIndex) {
    return labelProbArray[0][labelIndex];
  }

  @Override
  protected void setProbability(int labelIndex, Number value) {
    labelProbArray[0][labelIndex] = value.floatValue();
  }

  @Override
  protected float getNormalizedProbability(int labelIndex) {
    return labelProbArray[0][labelIndex];
  }

  @Override
  protected void runInference() {
    tflite.run(imgData, labelProbArray);
  }
}
