package com.example.coinscounter.utills;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public final class EuroCoins {
    //euro-cents
    private static final float c01 = 16.25f;
    private static final float c02 = 18.75f;
    private static final float c05 = 21.25f;
    private static final float c10 = 19.75f;
    private static final float c20 = 22.25f;
    private static final float c50 = 24.25f;

    //euros
    private static final float e1 = 23.25f;
    private static final float e2 = 25.75f;

    //sorted by value TODO This maybe needs to be automated
    public static final LinkedHashMap<String, Float> stringToFloatMap = new LinkedHashMap<String, Float>() {{
        put("cent1", 0.01f);
        put("cent2", 0.02f);
        put("cent5", 0.05f);
        put("cent10", 0.1f);
        put("cent20", 0.2f);
        put("cent50", 0.5f);
        put("euro1", 1f);
        put("euro2", 2f);
    }};

    public static final LinkedHashMap<Float, String> floatToStringMap = new LinkedHashMap<Float, String>() {{
        put(0.01f, "cent1");
        put(0.02f, "cent2");
        put(0.05f, "cent5");
        put(0.1f, "cent10");
        put(0.2f, "cent20");
        put(0.5f, "cent50");
        put(1f, "euro1");
        put(2f, "euro2");
    }};

    public static LinkedList<String> stringList = new LinkedList<String>(Arrays.asList(
            "cent1",
            "cent2",
            "cent5",
            "cent10",
            "cent20",
            "cent50",
            "euro1",
            "euro2"
            ));

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

}
