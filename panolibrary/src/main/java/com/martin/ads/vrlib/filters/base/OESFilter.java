package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLOESProgram;
import com.martin.ads.vrlib.textures.GLOESTexture;
import com.martin.ads.vrlib.utils.BufferUtils;
import com.martin.ads.vrlib.utils.TextureUtils;


/**
 * Created by Ads on 2017/01/26.
 */

public class OESFilter extends AbsFilter{

    private GLOESTexture glOESTexture;
    private GLOESProgram glOESProgram;
    private Plain plain;

    //videoTextureMatrix
    private float[] mSTMatrix = new float[16];

    public OESFilter(Context context) {
        plain=new Plain(true);
        glOESProgram=new GLOESProgram(context);
        glOESTexture=new GLOESTexture();
        Matrix.setIdentityM(mSTMatrix, 0);
    }

    @Override
    public void init() {
        glOESProgram.create();

        glOESTexture.loadTexture();
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        glOESProgram.use();
        plain.uploadTexCoordinateBuffer(glOESProgram.getTextureCoordinateHandle());
        plain.uploadVerticesBuffer(glOESProgram.getPositionHandle());
        GLES20.glUniformMatrix4fv(glOESProgram.getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);
    }

    @Override
    public void destroy() {
        glOESProgram.onDestroy();
        glOESTexture.deleteTexture();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        TextureUtils.bindTextureOES(textureId, GLES20.GL_TEXTURE0,glOESProgram.getUTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();
    }

    public GLOESProgram getGlOESProgram() {
        return glOESProgram;
    }

    public GLOESTexture getGlOESTexture() {
        return glOESTexture;
    }

    public float[] getSTMatrix() {
        return mSTMatrix;
    }
}
