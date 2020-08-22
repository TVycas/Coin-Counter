package com.example.coinscounter.utills;

import android.content.res.Resources;

import com.example.coinscounter.R;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public final class EuroCoins {

    private static final LinkedHashMap<String, Float> stringToFloatMap = new LinkedHashMap<String, Float>() {{
        put("cent1", 0.01f);
        put("cent2", 0.02f);
        put("cent5", 0.05f);
        put("cent10", 0.1f);
        put("cent20", 0.2f);
        put("cent50", 0.5f);
        put("euro1", 1f);
        put("euro2", 2f);
    }};

    public static LinkedList<Float> floatList = new LinkedList<>(Arrays.asList(
            0.01f,
            0.02f,
            0.05f,
            0.1f,
            0.2f,
            0.5f,
            1f,
            2f
    ));

    public static String mapFloatValueToEuroString(Float value) {
        // TODO get context here somehow
        Resources res = Resources.getSystem();
        String[] stringValues = res.getStringArray(R.array.euro_values_array);
        if (floatList.contains(value)) {
            int index = floatList.indexOf(value);
            return stringValues[index];
        } else {
            return "Unrecognized value";
        }
    }

    public static Float mapRecognizedStringToFloatValue(String euroString) {
        if (stringToFloatMap.containsKey(euroString)) {
            return stringToFloatMap.get(euroString);
        } else {
            return 0f;
        }
    }

    public static String formatFloatValueToEuroString(float value) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(value) + " €";
    }
}
