package com.martin.ads.vrlib.filters.advanced;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Handler;
import android.util.Log;

import com.martin.ads.vrlib.SensorEventHandler;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.filters.base.GGAbsFilter;
import com.martin.ads.vrlib.object.Sphere;
import com.martin.ads.vrlib.programs.GLSphere2DProgram;
import com.martin.ads.vrlib.programs.GLSphereProgram;
import com.martin.ads.vrlib.textures.TextureUtils;
import com.martin.ads.vrlib.utils.OrientationHelper;
import com.martin.ads.vrlib.utils.StatusHelper;

/**
 * Created by Ads on 2016/11/19.
 */

public class GGSphere2DPlugin extends GGAbsFilter {
    private Sphere sphere;
    private GLSphere2DProgram glSphereProgram;
    private SensorEventHandler sensorEventHandler;
    private StatusHelper statusHelper;

    private float[] rotationMatrix = new float[16];

    private float[] modelMatrix = new float[16];    //球体 拖动、传感器数据改变时改变
    private float[] viewMatrix = new float[16];     //观看角度
    private float[] projectionMatrix = new float[16];//投影变换，用于缩放

    private float[] modelViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float ratio;

    //Touch Control
    private float mDeltaX;
    private float mDeltaY;
    private float mScale;

    private OrientationHelper orientationHelper;

    public GGSphere2DPlugin(StatusHelper statusHelper) {
        this.statusHelper=statusHelper;
        mDeltaX=mDeltaY=0;
        mScale=1;
        sphere=new Sphere(18,75,150);
        sensorEventHandler=new SensorEventHandler();
        sensorEventHandler.setStatusHelper(statusHelper);
        sensorEventHandler.setSensorHandlerCallback(new SensorEventHandler.SensorHandlerCallback() {
            @Override
            public void updateSensorMatrix(float[] sensorMatrix) {
                System.arraycopy(sensorMatrix,0,rotationMatrix,0,16);
            }
        });
        sensorEventHandler.init();

        glSphereProgram =new GLSphere2DProgram(statusHelper.getContext());
        initMatrix();

        orientationHelper=new OrientationHelper();
        //TODO:move it to outer layer

        //Lock or Ignore parameters can be linked by binary-or operation,
        //see the samples below

        //for example,lock Y to achieve the similar result as Cardboard Motion
        //and GAME_ROTATION_VECTOR
        //orientationHelper.setLockAxisMode(OrientationHelper.LOCK_MODE_AXIS_Y);

        //for example ,lock z and y to detect whether the user
        // is looking at the sky or the ground
        //orientationHelper.setIgnoreRotationMode(OrientationHelper.IGNORE_ROTATION_AXIS_Z | OrientationHelper.IGNORE_ROTATION_AXIS_Y);
    }

    @Override
    public void init() {
        glSphereProgram.create();
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

        float currentDegree= (float) (Math.toDegrees(Math.atan(mScale))*2);
        if(statusHelper.getPanoDisPlayMode()== PanoMode.DUAL_SCREEN)
            Matrix.perspectiveM(projectionMatrix, 0, currentDegree, ratio/2, 1f, 500f);
        else Matrix.perspectiveM(projectionMatrix, 0, currentDegree, ratio, 1f, 500f);

        Matrix.setIdentityM(modelMatrix, 0);
        if (statusHelper.getPanoInteractiveMode()==PanoMode.MOTION){
            orientationHelper.recordRotation(rotationMatrix);
            System.arraycopy(rotationMatrix, 0, modelMatrix, 0, 16);
            orientationHelper.revertRotation(modelMatrix);
        }
        else{
            Matrix.rotateM(modelMatrix, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
            Matrix.rotateM(modelMatrix, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        }
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, modelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        GLES20.glUniformMatrix4fv(glSphereProgram.getMuMVPMatrixHandle(), 1, false, mMVPMatrix, 0);

        TextureUtils.bindTexture2D(textureId,GLES20.GL_TEXTURE0,glSphereProgram.getUTextureSamplerHandle(),0);

        onPreDrawElements();
        if (statusHelper.getPanoDisPlayMode()== PanoMode.DUAL_SCREEN){
            GLES20.glViewport(0,0,width/2,height);
            sphere.draw();
            GLES20.glViewport(width/2,0,width-width/2,height);
            sphere.draw();
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

    public OrientationHelper getOrientationHelper() {
        return orientationHelper;
    }
}
