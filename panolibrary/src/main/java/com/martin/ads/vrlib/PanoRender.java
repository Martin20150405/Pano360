package com.martin.ads.vrlib;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.constant.PanoFilter;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.object.Sphere;
import com.martin.ads.vrlib.utils.BitmapUtils;
import com.martin.ads.vrlib.utils.StatusHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;

/**
 * Created by Ads on 2016/6/25.
 */
public class PanoRender
        implements GLSurfaceView.Renderer {
    public static String TAG = "PanoRender";

    private StatusHelper statusHelper;
    private PanoMediaPlayerWrapper panoMediaPlayerWrapper;
    private SensorEventHandler sensorEventHandler;
    private GLProgram glProgram;
    private int width,height;

    private float[] rotationMatrix = new float[16];

    private float[] modelMatrix = new float[16];    //球体 拖动、传感器数据改变时改变
    private float[] viewMatrix = new float[16];     //观看角度
    private float[] projectionMatrix = new float[16];//投影变换，用于缩放

    private float[] modelViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private float[] mSTMatrix = new float[16];      //videoTextureMatrix

    private int mTextureID;

    private float ratio;

    //Touch Control
    private float mDeltaX;
    private float mDeltaY;
    private float mScale;

    //i.e. GLES11Ext.GL_TEXTURE_EXTERNAL_OES
    private static int GL_TEXTURE_EXTERNAL_OES = 0x8D65;

    private Sphere sphere;

    private boolean saveImg;

    public PanoRender(StatusHelper statusHelper,PanoFilter panoFilter,PanoMediaPlayerWrapper panoMediaPlayerWrapper) {
        this.statusHelper=statusHelper;
        this.panoMediaPlayerWrapper = panoMediaPlayerWrapper;
        init(panoFilter);
    }


    private void init(PanoFilter panoFilter){
        saveImg=false;
        mDeltaX=mDeltaY=0;
        mScale=1;
        sphere=new Sphere(18,75,150);

        Matrix.setIdentityM(mSTMatrix, 0);

        sensorEventHandler=new SensorEventHandler();
        sensorEventHandler.setStatusHelper(statusHelper);
        sensorEventHandler.setSensorHandlerCallback(new SensorHandlerCallback() {
            @Override
            public void updateSensorMatrix(float[] sensorMatrix) {
                System.arraycopy(sensorMatrix,0,rotationMatrix,0,16);
            }
        });
        sensorEventHandler.init();

        if (panoFilter==PanoFilter.GRAY_SCALE)
            glProgram=new GLProgram(statusHelper.getContext(), R.raw.vertex_shader,R.raw.fragment_shader_gray_scale);
        else if (panoFilter==PanoFilter.NORMAL) glProgram=new GLProgram(statusHelper.getContext());
        else  if (panoFilter==PanoFilter.INVERSE_COLOR)
            glProgram=new GLProgram(statusHelper.getContext(), R.raw.vertex_shader,R.raw.fragment_shader_inverse_color);
        initMatrix();

    }



    @Override
    public void onSurfaceCreated(GL10 glUnused,EGLConfig config) {

        glProgram.create();
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);
        checkGlError("glBindTexture mTextureID");

        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);


        panoMediaPlayerWrapper.setSurface(mTextureID);


        //------------------//
        glProgram.use();

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GL_TEXTURE_EXTERNAL_OES, mTextureID);


        sphere.uploadTexCoordinateBuffer(glProgram.getMaTextureHandle());
        sphere.uploadVerticesBuffer(glProgram.getMaPositionHandle());

    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        panoMediaPlayerWrapper.doTextureUpdate(mSTMatrix);
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
        if(statusHelper.getPanoDisPlayMode()==PanoMode.DUAL_SCREEN)
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

        GLES20.glUniformMatrix4fv(glProgram.getMuMVPMatrixHandle(), 1, false, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(glProgram.getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);

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

        if (saveImg){
            BitmapUtils.sendImage(width,height,statusHelper.getContext());
            saveImg=false;
        }
        GLES20.glFinish();

    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width=width;
        this.height=height;
        ratio=(float)width/ height;
        GLES20.glViewport(0,0,width,height);
    }

    public interface SensorHandlerCallback{
        void updateSensorMatrix(float[] sensorMatrix);
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

    public void saveImg(){
        saveImg=true;
    }

    public void updateScale(float scaleFactor){
        mScale=mScale+(1.0f-scaleFactor);
        mScale=Math.max(0.122f,Math.min(1.0f,mScale));
    }
}