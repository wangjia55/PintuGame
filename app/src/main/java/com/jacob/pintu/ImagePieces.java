package com.jacob.pintu;

import android.graphics.Bitmap;

/**
 * Created by jacob-wj on 2015/4/17.
 */
public class ImagePieces {
    private int index;
    private Bitmap bitmap;

    public ImagePieces(int index, Bitmap bitmap) {
        this.index = index;
        this.bitmap = bitmap;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public String toString() {
        return "ImagePieces{" +
                "index=" + index +
                ", bitmap=" + bitmap +
                '}';
    }
}
