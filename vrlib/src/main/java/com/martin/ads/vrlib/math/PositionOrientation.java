package com.martin.ads.vrlib.math;

import android.graphics.YuvImage;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Created by Ads on 2017/3/11.
 */

public class PositionOrientation {

    private float mX;
    private float mY;
    private float mZ;
    private float mAngleX;
    private float mAngleY;
    private float mAngleZ;

    private float yaw;
    private float pitch;
    private float[] visibleArea;
    private float distance;

    public PositionOrientation fromTriangularSystem(float yaw, float pitch, float distance) {
        this.yaw=yaw;
        this.pitch=pitch;
        this.distance=distance;

        mAngleX = pitch;
        mAngleY = 90 + yaw;
        mAngleZ = 0;

        pitch= (float) Math.toRadians(pitch);
        yaw= (float) Math.toRadians(yaw);

        this.mZ = (float) (distance * Math.sin(yaw));
        this.mY = (float) (distance * Math.sin(pitch));
        this.mX = (float) (distance * Math.cos(yaw));

        return this;
    }

    public boolean aroundPosition(float mYaw, float mPitch){
        mYaw = mYaw - 90;// to sync two systems ...
        mYaw = mYaw<0?360+mYaw:mYaw;
        mPitch=-mPitch;

        if (yaw > (mYaw-visibleArea[0]) && yaw < (mYaw+visibleArea[0])
                && pitch > (mPitch-visibleArea[1]) && pitch < (mPitch+visibleArea[1])
        ) return true;
        else return false;
    }


    private PositionOrientation() {
        mX = mY = mZ = 0;
        mAngleX = mAngleY = mAngleZ = 0;
    }

    public PositionOrientation config(float[] visibleArea) {
        this.visibleArea=visibleArea;
        return this;
    }

    private PositionOrientation(float mX, float mY, float mZ) {
        this.mX = mX;
        this.mY = mY;
        this.mZ = mZ;
    }

    public float getX() {
        return mX;
    }

    public PositionOrientation setX(float x) {
        this.mX = x;
        return this;
    }

    public float getY() {
        return mY;
    }

    public PositionOrientation setY(float y) {
        this.mY = y;
        return this;
    }

    public float getZ() {
        return mZ;
    }

    public PositionOrientation setZ(float z) {
        this.mZ = z;
        return this;
    }

    public float getAngleX() {
        return mAngleX;
    }

    /**
     * setAngleX
     * @param angleX in degree
     * @return self
     */
    public PositionOrientation setAngleX(float angleX) {
        this.mAngleX = angleX;
        return this;
    }

    public float getAngleY() {
        return mAngleY;
    }

    /**
     * setAngleY
     * @param angleY in degree
     * @return self
     */
    public PositionOrientation setAngleY(float angleY) {
        this.mAngleY = angleY;
        return this;
    }

    public float getAngleZ() {
        return mAngleZ;
    }

    /**
     * setAngleZ
     * @param angleZ in degree
     * @return self
     */
    public PositionOrientation setAngleZ(float angleZ) {
        this.mAngleZ = angleZ;
        return this;
    }

    public static PositionOrientation newInstance(){
        return new PositionOrientation();
    }

    public void updateModelMatrix(float modelMatrix[]){
        Matrix.setIdentityM(modelMatrix, 0);

        Matrix.translateM(modelMatrix, 0, getX(),getY(),getZ());

        Matrix.rotateM(modelMatrix, 0, -getAngleY(), 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, getAngleX(), 1.0f, 0.0f, 0.0f);
        Matrix.rotateM(modelMatrix, 0, getAngleZ(), 0.0f, 0.0f, 1.0f);
    }
}
