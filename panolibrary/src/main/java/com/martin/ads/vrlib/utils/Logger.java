package com.martin.ads.vrlib.utils;

import android.opengl.Matrix;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

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

    public static void logTouchEvent(View v, MotionEvent event){
        StringBuilder result=new StringBuilder();
        result.append(v.toString()+" \n");
        result.append("Action: ").append(event.getAction()).append("\n");
        result.append("Location: ").append(event.getX()).append(" x ")
                .append(event.getY()).append("\n");
        result.append("Edge flags: ").append(event.getEdgeFlags());
        result.append("\n");
        result.append("Pressure: ").append(event.getPressure());
        result.append("  ").append("Size: ").append(event.getSize());
        result.append("\n").append("Down time: ");
        result.append(event.getDownTime()).append("ms\n");
        result.append("Event time: ").append(event.getEventTime());
        result.append("ms").append(" Elapsed:");
        result.append(event.getEventTime() - event.getDownTime());
        result.append(" ms\n");
        Log.d(TAG,result.toString());
    }
}
