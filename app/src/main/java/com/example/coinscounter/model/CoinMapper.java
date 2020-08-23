package com.example.coinscounter.model;

public interface CoinMapper {
    float incrementValue(float value);

    float decrementValue(float value);

    Float mapPredictedClassToFloatValue(String euroString);

    String formatFloatValueSumToString(float value);

    String mapFloatValueToString(Float value);
}
