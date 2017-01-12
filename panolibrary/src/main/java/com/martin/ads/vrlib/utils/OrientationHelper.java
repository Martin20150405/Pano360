package com.martin.ads.vrlib.utils;

import android.opengl.Matrix;

/**
 * Created by Ads on 2017/1/12.
 */

public class OrientationHelper {
    public static final int LOCK_MODE_NONE=0;
    // X:pitch  pointing at right
    // Y:yaw    pointing at sky
    // Z:roll   pointing at camera

    public static final int LOCK_MODE_AXIS_X=1<<0;
    public static final int LOCK_MODE_AXIS_Y=1<<1;
    public static final int LOCK_MODE_AXIS_Z=1<<2;
    public static final int LOCK_MODE_ALL_AXIS=7;

    private int lockAxisMode;
    //eulerAngles describing the initial rotation of model
    private float[] initialRotation=new float[3];
    private float[] initialInvertRotation=new float[16];
    private boolean rotationRecorded;
    float[] tmp=new float[16];

    public OrientationHelper() {
        lockAxisMode=LOCK_MODE_NONE;
        rotationRecorded=false;
    }

    public void recordRotation(float []rotationMatrix){
        if (!rotationRecorded){
            Matrix.invertM(initialInvertRotation,0,rotationMatrix,0);
            SensorUtils.getOrientationFromRotationMatrix(rotationMatrix,initialRotation);
            for(int i=0;i<initialRotation.length;i++)
                initialRotation[i]= (float) Math.toDegrees(-initialRotation[i]);
            rotationRecorded=true;
        }
    }

    public void revertRotation(float []modelMatrix){
        //the default order of euler angles is ZXY
        //in the sphere(landscape) , Y is the same,x is z,z is -x
        if(lockAxisMode==0) return;
        if(lockAxisMode==LOCK_MODE_ALL_AXIS) {
            Matrix.multiplyMM(tmp, 0, modelMatrix, 0, initialInvertRotation, 0);
            System.arraycopy(tmp, 0, modelMatrix, 0, 16);
            return;
        }
        //FIXME
/*        Matrix.multiplyMM(tmp,0,modelMatrix,0,initialInvertRotation,0);
        if((lockAxisMode & LOCK_MODE_AXIS_Z) ==0){
            Matrix.rotateM(tmp, 0, initialRotation[0], 0.0f, 0.0f, 1.0f);
        }
        if((lockAxisMode & LOCK_MODE_AXIS_X) ==0){
            Matrix.rotateM(tmp, 0, initialRotation[1], 1.0f, 0.0f, 0.0f);
        }
        if((lockAxisMode & LOCK_MODE_AXIS_Y) ==0) {
            Matrix.rotateM(tmp, 0, initialRotation[2], 0.0f, 1.0f, 0.0f);
        }
        System.arraycopy(tmp,0,modelMatrix,0,16);
        */
        if((lockAxisMode & LOCK_MODE_AXIS_Y) !=0) {
            Matrix.rotateM(modelMatrix, 0, -initialRotation[2], 0.0f, 1.0f, 0.0f);
            return;
        }

    }

    public void setRotationRecorded(boolean rotationRecorded) {
        this.rotationRecorded = rotationRecorded;
    }

    public void setLockAxisMode(int lockAxisMode) {
        this.lockAxisMode = lockAxisMode;
    }
}
