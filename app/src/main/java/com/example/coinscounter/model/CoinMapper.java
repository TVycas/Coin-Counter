package com.example.coinscounter.model;

/**
 * A helper object to deal with the mapping of coins from their float values to String counterparts, as well as
 * incrementing and decrementing the values by one coin denomination.
 */
public interface CoinMapper {
    /**
     * Increments the value by one coin denomination.
     *
     * @param value Initial value.
     * @return Value incremented by one denomination.
     */
    float incrementValue(float value);

    /**
     * Decrements the value by one coin denomination.
     *
     * @param value Initial value.
     * @return Value decremented by one denomination.
     */
    float decrementValue(float value);

    /**
     * Maps the class, predicted by the ML model with a float value.
     *
     * @param predictedClass The class predicted by the ML model
     * @return The float value of the class
     */
    Float mapPredictedClassToFloatValue(String predictedClass);

    /**
     * Maps a float value of the coin to a user-readable String
     *
     * @param value The float value of the coin
     * @return A user-readable String describing the value
     */
    String mapFloatValueToString(Float value);

    /**
     * Formats a float value to a String with the correct currency symbol.
     *
     * @param value Float value to be formatted.
     * @return A formatted String.
     */
    String formatFloatValueSumToString(float value);
}
