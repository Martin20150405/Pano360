package com.martin.ads.vrlib.math;

import android.opengl.Matrix;

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

    private PositionOrientation() {
        mX = mY = mZ = 0;
        mAngleX = mAngleY = mAngleZ = 0;
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
