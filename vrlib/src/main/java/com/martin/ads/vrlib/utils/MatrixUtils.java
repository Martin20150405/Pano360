package com.martin.ads.vrlib.utils;

import android.opengl.Matrix;

import com.martin.ads.vrlib.constant.AdjustingMode;

/**
 * Created by Ads on 2017/1/27.
 */

public class MatrixUtils {
    public static float IDENTITY_MATRIX[]=new float[16];
    static {
        Matrix.setIdentityM(IDENTITY_MATRIX,0);
    }

    public static void updateProjection(int imageWidth, int imageHeight,int surfaceWidth,int surfaceHeight,int adjustingMode,float[] projectionMatrix){
        switch (adjustingMode){
            case AdjustingMode.ADJUSTING_MODE_STRETCH:
                MatrixUtils.updateProjectionFill(projectionMatrix);
                break;
            case AdjustingMode.ADJUSTING_MODE_FIT_TO_SCREEN:
                MatrixUtils.updateProjectionFit(imageWidth,imageHeight,
                        surfaceWidth,surfaceHeight,projectionMatrix);
                break;
            case AdjustingMode.ADJUSTING_MODE_CROP:
                MatrixUtils.updateProjectionCrop(imageWidth,imageHeight,
                        surfaceWidth,surfaceHeight,projectionMatrix);
                break;
            default:
                throw new RuntimeException("Adjusting Mode not found!");
        }
    }

    public static void updateProjectionFill(float[] projectionMatrix) {
        Matrix.setIdentityM(projectionMatrix,0);
    }

    public static void updateProjectionFit(int imageWidth, int imageHeight,int surfaceWidth,int surfaceHeight,float[] projectionMatrix) {
        float screenRatio=(float)surfaceWidth/surfaceHeight;
        float videoRatio=(float) imageWidth / imageHeight;
        if (videoRatio>screenRatio){
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-videoRatio/screenRatio,videoRatio/screenRatio,-1f,1f);
        }else Matrix.orthoM(projectionMatrix,0,-screenRatio/videoRatio,screenRatio/videoRatio,-1f,1f,-1f,1f);
    }

    public static void updateProjectionCrop(int imageWidth, int imageHeight,int surfaceWidth,int surfaceHeight,float[] projectionMatrix) {
        float screenRatio=(float)surfaceWidth/surfaceHeight;
        float videoRatio=(float) imageWidth / imageHeight;
        //crop is just making the screen fit the image.
        //only one difference
        if (videoRatio<screenRatio){
            Matrix.orthoM(projectionMatrix,0,-1f,1f,-videoRatio/screenRatio,videoRatio/screenRatio,-1f,1f);
        }else Matrix.orthoM(projectionMatrix,0,-screenRatio/videoRatio,screenRatio/videoRatio,-1f,1f,-1f,1f);
    }
}
