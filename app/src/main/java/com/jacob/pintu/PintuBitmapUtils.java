package com.jacob.pintu;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jacob-wj on 2015/4/17.
 */
public class PintuBitmapUtils {
    /**
     * 将图片分割成count*count个小图片
     */
    public static List<ImagePieces>  getImagePiecesList(Bitmap bitmap , int count){
       List<ImagePieces> imagePiecesList = new ArrayList<>();
        int bitmapW  = bitmap.getWidth();
        int bitmapH = bitmap.getHeight();
        int width = Math.min(bitmapW,bitmapH)/count;

        for (int i = 0; i < count; i++) {
            for (int j = 0; j < count; j++) {
                int x  = j*width;
                int y = i * width;
                Bitmap bitmapChild = Bitmap.createBitmap(bitmap,x,y,width,width);
                ImagePieces imagePieces = new ImagePieces(i*count+j,bitmapChild);
                imagePiecesList.add(imagePieces);
            }
        }
        return imagePiecesList;
    }
}
