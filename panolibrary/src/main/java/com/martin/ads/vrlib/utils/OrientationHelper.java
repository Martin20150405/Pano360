package com.martin.ads.vrlib.utils;

import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by Ads on 2017/1/12.
 * the code may be a little confusing
 * for me and those who don't really understand the rotation matrix,
 * euler angles, rotation vector, quaternion ,etc.
 */

public class OrientationHelper {
    private static final String TAG = "OrientationHelper";
    public static final int LOCK_MODE_NONE=0;
    //In the rotationMatrix , the coordinate is defined as follows:
    // Z:azimuth/yaw   pointing at sky  rotation around the -Z axis
    // X:pitch     pointing at east  rotation around the -X axis
    // Y:roll      pointing at north  rotation around the Y axis

    // because we remapped the coordinate to landscape
    // and rotated around the X-axis,so
    //X is pointing north
    //Y is pointing ground
    //Z is pointing west

    //In the OpenGL coordinate
    //Z is pointing at the camera(observer)
    //X is pointing at right of the camera(observer)
    //Y is pointing at up of the camera(observer)
    //These constants are defined under OpenGL coordinate
    //notice : Matrix in OpenGL is row-major
    public static final int LOCK_MODE_AXIS_X=1<<0;
    public static final int LOCK_MODE_AXIS_Y=1<<1;
    public static final int LOCK_MODE_AXIS_Z=1<<2;
    public static final int LOCK_MODE_ALL_AXIS=7;

    public static final int IGNORE_ROTATION_NONE=0;
    public static final int IGNORE_ROTATION_AXIS_X=1<<3;
    public static final int IGNORE_ROTATION_AXIS_Y=1<<4;
    public static final int IGNORE_ROTATION_AXIS_Z=1<<5;

    private int lockAxisMode;
    private int ignoreRotationMode;

    //eulerAngles describing the initial rotation of model
    private float[] initialRotation=new float[3];
    private float[] currentRotation=new float[3];
    private boolean rotationRecorded;

    float[] tmp=new float[16];

    //TODO:this should be adjustable
    public OrientationHelper() {
        lockAxisMode=LOCK_MODE_NONE;
        ignoreRotationMode=IGNORE_ROTATION_NONE;
        rotationRecorded=false;
    }

    public void recordRotation(float []rotationMatrix){
        // we need to Transpose it to match model
        // or just rotate in the reverse orientation
        // and reversed order
        if (!rotationRecorded){
            Matrix.transposeM(tmp,0,rotationMatrix,0);
            SensorUtils.getOrientationFromRotationMatrix(tmp,initialRotation);
            convertToDegrees(initialRotation);
            rotationRecorded=true;
        }else{
            Matrix.transposeM(tmp,0,rotationMatrix,0);
            SensorUtils.getOrientationFromRotationMatrix(tmp,currentRotation);
            convertToDegrees(currentRotation);
        }
    }

    public void revertRotation(float []modelMatrix){
        //the default order of euler angles is ZXY
        //Applying these three intrinsic rotations in azimuth, pitch and roll order transforms
        //identity matrix to the rotation matrix given in input R
        if(lockAxisMode==LOCK_MODE_NONE
                && ignoreRotationMode==IGNORE_ROTATION_NONE) return;
        if(ignoreRotationMode!=0){
            Matrix.setIdentityM(tmp,0);
            if((ignoreRotationMode & IGNORE_ROTATION_AXIS_Z) ==0){
                Matrix.rotateM(tmp, 0, -currentRotation[0], 0.0f, 0.0f, 1.0f);
            }
            //axis X have Gimbal lock
            if((ignoreRotationMode & IGNORE_ROTATION_AXIS_X) ==0){
                Matrix.rotateM(tmp, 0, -currentRotation[1], 1.0f, 0.0f, 0.0f);
            }
            if((ignoreRotationMode & IGNORE_ROTATION_AXIS_Y) ==0) {
                Matrix.rotateM(tmp, 0, currentRotation[2], 0.0f, 1.0f, 0.0f);
            }
            System.arraycopy(tmp,0,modelMatrix,0,16);
            Matrix.transposeM(tmp,0,modelMatrix,0);
            SensorUtils.getOrientationFromRotationMatrix(tmp,currentRotation);
            convertToDegrees(currentRotation);
        }

        if(lockAxisMode!=0){
            Matrix.setIdentityM(tmp,0);
            float rotateZ=(lockAxisMode & LOCK_MODE_AXIS_Z) !=0? +initialRotation[0]:0;
            float rotateX=(lockAxisMode & LOCK_MODE_AXIS_X) !=0? +initialRotation[1]:0;
            float rotateY=(lockAxisMode & LOCK_MODE_AXIS_Y) !=0? -initialRotation[2]:0;
            Matrix.rotateM(tmp, 0, -currentRotation[0]+rotateZ, 0.0f, 0.0f, 1.0f);
            Matrix.rotateM(tmp, 0, -currentRotation[1]+rotateX, 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(tmp, 0, currentRotation[2]+rotateY, 0.0f, 1.0f, 0.0f);
            System.arraycopy(tmp,0,modelMatrix,0,16);
            Matrix.transposeM(tmp,0,modelMatrix,0);
            SensorUtils.getOrientationFromRotationMatrix(tmp,currentRotation);
            convertToDegrees(currentRotation);
        }
    }

    private void convertToDegrees(float[] s){
        for(int i=0;i<s.length;i++)
            s[i]= (float) Math.toDegrees(s[i]);
    }
    public void setRotationRecorded(boolean rotationRecorded) {
        this.rotationRecorded = rotationRecorded;
    }

    public void setLockAxisMode(int lockAxisMode) {
        this.lockAxisMode = lockAxisMode;
    }

    public int getLockAxisMode() {
        return lockAxisMode;
    }

    public int getIgnoreRotationMode() {
        return ignoreRotationMode;
    }

    public void setIgnoreRotationMode(int ignoreRotationMode) {
        this.ignoreRotationMode = ignoreRotationMode;
    }

    //get current yaw pitch roll here
    public float[] getCurrentRotation() {
        return currentRotation;
    }
}
