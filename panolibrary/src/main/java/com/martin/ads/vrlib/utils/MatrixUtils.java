package com.martin.ads.vrlib.utils;

import android.opengl.Matrix;

/**
 * Created by Ads on 2017/1/27.
 */

public class MatrixUtils {
    //crop is just making the screen fit the image.
    public static void updateProjection(int imageWidth, int imageHeight,int surfaceWidth,int surfaceHeight,float[] projectionMatrix,int adjustingMode) {
        float screenRatio=(float)surfaceWidth/surfaceHeight;
        float videoRatio=(float) imageWidth / imageHeight;
        int flag=videoRatio>screenRatio? 0:1;
        if (flag+adjustingMode==3){
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-videoRatio/screenRatio,videoRatio/screenRatio,-1f,1f);
        }else Matrix.orthoM(projectionMatrix,0,-screenRatio/videoRatio,screenRatio/videoRatio,-1f,1f,-1f,1f);
    }
}
