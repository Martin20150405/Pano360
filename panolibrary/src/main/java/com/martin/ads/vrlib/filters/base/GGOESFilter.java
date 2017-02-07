package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.object.Sphere;
import com.martin.ads.vrlib.programs.GLOESProgram;
import com.martin.ads.vrlib.programs.GLSphereProgram;
import com.martin.ads.vrlib.textures.GLOESTexture;
import com.martin.ads.vrlib.textures.TextureUtils;
import com.martin.ads.vrlib.utils.Logger;

/**
 * Created by Ads on 2016/11/19.
 */

public class GGOESFilter extends GGAbsFilter{
    private GLOESTexture gloesTexture;
    private GLOESProgram gloesProgram;
    private Plain plain;

    private float[] mSTMatrix = new float[16];      //videoTextureMatrix

    private float[] projectionMatrix = new float[16];

    public GGOESFilter(Context context) {
        plain=new Plain();
        gloesProgram=new GLOESProgram(context);
        gloesTexture=new GLOESTexture();
        Matrix.setIdentityM(mSTMatrix, 0);
    }

    @Override
    public void init() {
        gloesProgram.create();

        gloesTexture.loadTexture();
    }

    @Override
    public void onPreDrawElements() {
    }

    @Override
    public void destroy() {
        gloesProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        gloesProgram.use();
        plain.uploadTexCoordinateBuffer(gloesProgram.getMaTextureHandle());
        plain.uploadVerticesBuffer(gloesProgram.getMaPositionHandle());
        GLES20.glUniformMatrix4fv(gloesProgram.getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);

        TextureUtils.bindTextureOES(textureId, GLES20.GL_TEXTURE0,gloesProgram.getUTextureSamplerHandle(),0);
        onPreDrawElements();
        GLES20.glViewport(0,0,width,height);
        plain.draw();
    }


    public float[] getmSTMatrix() {
        return mSTMatrix;
    }

    public GLOESTexture getGloesTexture() {
        return gloesTexture;
    }

    public Plain getPlain() {
        return plain;
    }

}
