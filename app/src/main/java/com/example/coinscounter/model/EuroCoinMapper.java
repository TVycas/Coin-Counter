package com.example.coinscounter.model;

import android.content.Context;

import com.example.coinscounter.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class EuroCoinMapper implements CoinMapper {

    private final LinkedHashMap<String, Float> stringToFloatMap = new LinkedHashMap<String, Float>() {{
        put("cent1", 0.01f);
        put("cent2", 0.02f);
        put("cent5", 0.05f);
        put("cent10", 0.1f);
        put("cent20", 0.2f);
        put("cent50", 0.5f);
        put("euro1", 1f);
        put("euro2", 2f);
    }};

    private final LinkedList<Float> floatList = new LinkedList<>(Arrays.asList(
            0.01f,
            0.02f,
            0.05f,
            0.1f,
            0.2f,
            0.5f,
            1f,
            2f
    ));

    String[] euroNames;

    public EuroCoinMapper(Context context) {
        euroNames = context.getResources().getStringArray(R.array.euro_values_array);
    }

    @Override
    public String mapFloatValueToString(Float value) {
        if (floatList.contains(value)) {
            int index = floatList.indexOf(value);
            return euroNames[index];
        } else {
            return "Unrecognized value";
        }
    }

    @Override
    public float incrementValue(float value) {
        int currentPosition = floatList.indexOf(value);
        if (currentPosition < floatList.size() - 1) {
            value = floatList.get(currentPosition + 1);
        }
        return value;
    }

    @Override
    public float decrementValue(float value) {
        int currentPosition = floatList.indexOf(value);
        if (currentPosition > 0) {
            value = floatList.get(currentPosition - 1);
        }
        return value;
    }

    @Override
    public Float mapPredictedClassToFloatValue(String euroString) {
        if (stringToFloatMap.containsKey(euroString)) {
            return stringToFloatMap.get(euroString);
        } else {
            return 0f;
        }
    }

    @Override
    public String formatFloatValueSumToString(float value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value) + " €";
    }
}
