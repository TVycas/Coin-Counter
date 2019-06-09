package com.example.coinscounter;

import java.util.HashMap;
import java.util.LinkedHashMap;

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
    public static final LinkedHashMap<Float, Float> valueMap = new LinkedHashMap<Float, Float>() {{
        put(0.01f, c01);
        put(0.02f, c02);
        put(0.1f, c10);
        put(0.05f, c05);
        put(0.2f, c20);
        put(1f, e1);
        put(0.5f, c50);
        put(2f, e2);
    }};

}
