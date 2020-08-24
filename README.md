# Coin Counter

An Android app to calculate the value of coins in an image using a
TensorFlow Lite image recognition model.

The app works by first capturing an image of coins (all placed to show
the reverse (or the "tails") of the coin). Then, the app displays an image
of found coins for the user to select or deselect detected as coins.
Once the users are happy with the selection, they can initiate the coin
recognition, see the calculated sum of values and update the wrongly
recognized coins to their correct values.

The app uses intents to allow the user to either capture an image of the
coins or select one from the gallery. Then, it uses OpenCV and it's
Hough Circles method to find circles in the image (which hopefully
correspond to coins). Later, these circles are used in a TensorFlow Lite
model, generated using Firebase, to predict the values of each of the
coins.

### Made with:
* [TensorFlow Lite](https://www.tensorflow.org/lite) image recognition
* [Google ML-Kit](https://developers.google.com/ml-kit)
* [OpenCV](https://opencv.org/) image processing
* Hilt dependency injection
* Model-View-ViewModel architecture
* Android Jetpack
* LiveData objects
* Android RecyclerViews
* Android Intents
* Android custom AlertDialog

### Screenshots

  <p align="left">
    <img src="../assets/coin_selection.jpg" width="200" style="padding-left: 10px"/>
    <img src="../assets/coin_selection_2.jpg" width="200" style="padding-left: 10px"/>
    <img src="../assets/results1.jpg" width="200" style="padding-left: 10px"/>
    <img src="../assets/change_value1.jpg" width="200" style="padding-left: 10px"/>
    <img src="../assets/change_value2.jpg" width="200" style="padding-left: 10px"/>
  </p>
