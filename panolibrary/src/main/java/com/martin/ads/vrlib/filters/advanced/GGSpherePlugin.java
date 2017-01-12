package com.martin.ads.vrlib.filters.advanced;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.vrlib.SensorEventHandler;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.filters.base.GGAbsFilter;
import com.martin.ads.vrlib.object.Sphere;
import com.martin.ads.vrlib.programs.GLSphereProgram;
import com.martin.ads.vrlib.textures.GLOESTexture;
import com.martin.ads.vrlib.textures.TextureUtils;
import com.martin.ads.vrlib.utils.StatusHelper;

/**
 * Created by Ads on 2016/11/19.
 * this code is reserved for future use
 */
@Deprecated
public class GGSpherePlugin extends GGAbsFilter {

    private Sphere sphere;
    private GLSphereProgram glSphereProgram;
    private GLOESTexture gloesTexture;
    private SensorEventHandler sensorEventHandler;
    private StatusHelper statusHelper;

    private float[] rotationMatrix = new float[16];

    private float[] modelMatrix = new float[16];    //球体 拖动、传感器数据改变时改变
    private float[] viewMatrix = new float[16];     //观看角度
    private float[] projectionMatrix = new float[16];//投影变换，用于缩放

    private float[] modelViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float[] mSTMatrix = new float[16];      //videoTextureMatrix

    private float ratio;

    //Touch Control
    private float mDeltaX;
    private float mDeltaY;
    private float mScale;

    public GGSpherePlugin(StatusHelper statusHelper) {
        this.statusHelper=statusHelper;
        mDeltaX=mDeltaY=0;
        mScale=1;
        sphere=new Sphere(18,75,150);

        Matrix.setIdentityM(mSTMatrix, 0);

        sensorEventHandler=new SensorEventHandler();
        sensorEventHandler.setStatusHelper(statusHelper);
        sensorEventHandler.setSensorHandlerCallback(new SensorEventHandler.SensorHandlerCallback() {
            @Override
            public void updateSensorMatrix(float[] sensorMatrix) {
                System.arraycopy(sensorMatrix,0,rotationMatrix,0,16);
            }
        });
        sensorEventHandler.init();

        glSphereProgram =new GLSphereProgram(statusHelper.getContext());
        gloesTexture=new GLOESTexture();

        initMatrix();
    }

    @Override
    public void init() {
        glSphereProgram.create();

        gloesTexture.loadTexture();
    }

    @Override
    public void onPreDrawElements() {
    }

    @Override
    public void destroy() {
        glSphereProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {

        glSphereProgram.use();
        sphere.uploadTexCoordinateBuffer(glSphereProgram.getMaTextureHandle());
        sphere.uploadVerticesBuffer(glSphereProgram.getMaPositionHandle());

/*
            content of getTransformMatrix(mSTMatrix) in col-major order
            1.0 0.0 0.0 0.0
            0.0 -1.0 0.0 1.0
            0.0 0.0 1.0 0.0
            0.0 0.0 0.0 1.0
        */
        //Matrix.setIdentityM(mSTMatrix,0);

        //视角从90度到14度
        float currentDegree= (float) (Math.toDegrees(Math.atan(mScale))*2);
        if(statusHelper.getPanoDisPlayMode()== PanoMode.DUAL_SCREEN)
            Matrix.perspectiveM(projectionMatrix, 0, currentDegree, ratio/2, 1f, 500f);
        else Matrix.perspectiveM(projectionMatrix, 0, currentDegree, ratio, 1f, 500f);
        /**
         *如果要使用更小的视角，下面两种方法基本等价
         *perspectiveM视角从70度到14度对应frustumM近平面从0.7到4.0
         * 70 is atan(0.5/0.7)*2_to_degrees
         if(statusHelper.getPanoDisPlayMode()==PanoMode.DUAL_SCREEN)
         Matrix.perspectiveM(projectionMatrix, 0, 70f, ratio/2, 0.7f, 500f);
         else Matrix.perspectiveM(projectionMatrix, 0, 70f, ratio, 0.7f, 500f);

         near scale is from 0.7 to 5.6 (1-8)
         if(statusHelper.getPanoDisPlayMode()==PanoMode.DUAL_SCREEN)
         Matrix.frustumM(projectionMatrix, 0, -ratio/4, ratio/4, -0.5f, 0.5f, 0.7f, 500);
         else Matrix.frustumM(projectionMatrix, 0, -ratio/2, ratio/2, -0.5f, 0.5f, 0.7f, 500);
         */

        Matrix.setIdentityM(modelMatrix, 0);
        if (statusHelper.getPanoInteractiveMode()==PanoMode.MOTION){
            System.arraycopy(rotationMatrix, 0, modelMatrix, 0, 16);
        }
        else{
            Matrix.rotateM(modelMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(modelMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        }
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        GLES20.glUniformMatrix4fv(glSphereProgram.getMuMVPMatrixHandle(), 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(glSphereProgram.getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);

        TextureUtils.bindTextureOES(gloesTexture.getTextureId(),GLES20.GL_TEXTURE0,glSphereProgram.getUTextureSamplerHandle(),0);

        onPreDrawElements();
        if (statusHelper.getPanoDisPlayMode()== PanoMode.DUAL_SCREEN){
            GLES20.glViewport(0,0,width/2,height);
            sphere.draw();
            GLES20.glViewport(width/2,0,width-width/2,height);
            sphere.draw();

            /**
             * 分出rows*cols个屏幕，如果你觉得有必要的话。。
             *
             final int rows=2,cols=4;
             for(int j=0;j<rows;j++){
             for(int i=0;i<cols;i++){
             GLES20.glViewport(width*i/cols,height*j/rows,width/cols,height/rows);
             sphere.draw();
             }
             }
             */

        }else{
            GLES20.glViewport(0,0,width,height);
            sphere.draw();
        }

    }

    @Override
    public void onFilterChanged(int width, int height) {
        super.onFilterChanged(width,height);
        ratio=(float)width/ height;
    }



    private void initMatrix() {
        Matrix.setIdentityM(rotationMatrix,0);
        Matrix.setIdentityM(modelMatrix,0);
        Matrix.setIdentityM(projectionMatrix,0);
        Matrix.setIdentityM(viewMatrix, 0);
        Matrix.setLookAtM(viewMatrix, 0,
                0.0f, 0.0f, 0f,
                0.0f, 0.0f,-1.0f,
                0.0f, 1.0f, 0.0f);
    }

    public SensorEventHandler getSensorEventHandler(){
        return sensorEventHandler;
    }

    public float getDeltaX() {
        return mDeltaX;
    }

    public void setDeltaX(float mDeltaX) {
        this.mDeltaX = mDeltaX;
    }

    public float getDeltaY() {
        return mDeltaY;
    }

    public void setDeltaY(float mDeltaY) {
        this.mDeltaY = mDeltaY;
    }

    public void updateScale(float scaleFactor){
        mScale=mScale+(1.0f-scaleFactor);
        mScale=Math.max(0.122f,Math.min(1.0f,mScale));
    }

    public GLOESTexture getGloesTexture() {
        return gloesTexture;
    }

    public float[] getmSTMatrix() {
        return mSTMatrix;
    }

}
