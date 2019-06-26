package com.example.coinscounter.utills;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class CoinCardItem implements Parcelable {
    private Bitmap imageResource;
    private String name;
    private float value;

    public CoinCardItem(Bitmap imageResource, String name, float value){
        this.imageResource = imageResource;
        this.name = name;
        this.value = value;
    }

    protected CoinCardItem(Parcel in) {
        imageResource = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        value = in.readFloat();
    }

    public static final Creator<CoinCardItem> CREATOR = new Creator<CoinCardItem>() {
        @Override
        public CoinCardItem createFromParcel(Parcel in) {
            return new CoinCardItem(in);
        }

        @Override
        public CoinCardItem[] newArray(int size) {
            return new CoinCardItem[size];
        }
    };

    public Bitmap getImageBitmap(){
        return imageResource;
    }

    public String getName(){
        return name;
    }

    public float getValue(){ return value; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(imageResource, i);
        parcel.writeString(name);
        parcel.writeFloat(value);
    }
}
