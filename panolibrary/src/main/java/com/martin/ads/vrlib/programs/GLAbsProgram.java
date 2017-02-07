package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.martin.ads.vrlib.utils.ShaderUtils;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;
import static com.martin.ads.vrlib.utils.ShaderUtils.createProgram;

/**
 * Created by Ads on 2016/11/19.
 */

//It is optional to split reference to a program
public abstract class GLAbsProgram {
    private int mProgramId;
    private String mVertexShader;
    private String mFragmentShader;

    private int maPositionHandle;
    private int maTextureHandle;

    public GLAbsProgram(Context context
            , final String vertexShaderPath
            , final String fragmentShaderPath){
        mVertexShader = ShaderUtils.readAssetsTextFile(context,vertexShaderPath);
        mFragmentShader= ShaderUtils.readAssetsTextFile(context,fragmentShaderPath);
    }

    public GLAbsProgram(Context context
            , final int vertexShaderResourceId
            , final int fragmentShaderResourceId){
        mVertexShader = ShaderUtils.readRawTextFile(context, vertexShaderResourceId);
        mFragmentShader= ShaderUtils.readRawTextFile(context, fragmentShaderResourceId);
    }

    public void create(){
        mProgramId = createProgram(mVertexShader, mFragmentShader);
        if (mProgramId == 0) {
            return;
        }

        maPositionHandle = GLES20.glGetAttribLocation(getProgramId(), "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(getProgramId(), "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }
    }

    public void use(){
        GLES20.glUseProgram(getProgramId());
        checkGlError("glUseProgram");
    }

    public int getProgramId() {
        return mProgramId;
    }

    public void onDestroy(){
        GLES20.glDeleteProgram(mProgramId);
    }


    public int getMaPositionHandle() {
        return maPositionHandle;
    }

    public int getMaTextureHandle() {
        return maTextureHandle;
    }
}
