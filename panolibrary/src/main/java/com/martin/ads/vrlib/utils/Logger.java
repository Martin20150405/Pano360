package com.martin.ads.vrlib.utils;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by Ads on 2016/11/5.
 */
public class Logger {
    public static String TAG = "Logger";

    /**
     * Matrices are 4 x 4 column-vector matrices stored in column-major order:
     * @param matrix length=16
     */
    public static void logMatrix(float[] matrix){
        Log.d(TAG,"Start Displaying Matrix");
        for(int i=0;i<4;i++){
            String s="";
            for(int j=i;j<16;j+=4){
                s=s+matrix[j]+" ";
            }
            Log.d(TAG,s);
        }
    }
}
