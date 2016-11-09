package com.martin.ads.vrlib;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.utils.StatusHelper;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;
import static com.martin.ads.vrlib.utils.ShaderUtils.createProgram;
import static com.martin.ads.vrlib.utils.ShaderUtils.readRawTextFile;

/**
 * Created by Ads on 2016/11/8.
 */
public class GLProgram {
    private int mProgram;
    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;
    private int maPositionHandle;
    private int maTextureHandle;
    private String mVertexShader;
    private String mFragmentShader;

    public GLProgram(Context context) {
        mVertexShader = readRawTextFile(context, R.raw.vertex_shader);
        mFragmentShader= readRawTextFile(context, R.raw.fragment_shader);
    }

    public GLProgram(Context context
            , final int vertexShaderResourceId
            , final int fragmentShaderResourceId){
        mVertexShader = readRawTextFile(context, vertexShaderResourceId);
        mFragmentShader= readRawTextFile(context, fragmentShaderResourceId);
    }

    public void create(){

        mProgram = createProgram(mVertexShader, mFragmentShader);
        if (mProgram == 0) {
            return;
        }
        maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        checkGlError("glGetAttribLocation aPosition");
        if (maPositionHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aPosition");
        }
        maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTextureHandle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord");
        }

        muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }
    }

    public void use(){
        GLES20.glUseProgram(mProgram);
        checkGlError("glUseProgram");
    }


    public int getMuMVPMatrixHandle() {
        return muMVPMatrixHandle;
    }

    public int getMuSTMatrixHandle() {
        return muSTMatrixHandle;
    }

    public int getMaPositionHandle() {
        return maPositionHandle;
    }

    public int getMaTextureHandle() {
        return maTextureHandle;
    }
}
