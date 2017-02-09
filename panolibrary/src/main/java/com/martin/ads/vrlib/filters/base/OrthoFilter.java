package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLPassThroughProgram;
import com.martin.ads.vrlib.utils.MatrixUtils;
import com.martin.ads.vrlib.utils.TextureUtils;


/**
 * Created by Ads on 2016/11/19.
 * let the image pass through
 * and simply fit the image to the screen
 */

public class OrthoFilter extends AbsFilter {

    public static final int ADJUSTING_MODE_STRETCH=1;
    public static final int ADJUSTING_MODE_CROP=2;
    public static final int ADJUSTING_MODE_FIT_TO_SCREEN=3;

    private int adjustingMode;

    private GLPassThroughProgram glPassThroughProgram;
    private Plain plain;

    private float[] projectionMatrix = new float[16];

    private int videoWidth,videoHeight;

    public OrthoFilter(Context context,int adjustingMode) {
        glPassThroughProgram=new GLPassThroughProgram(context);
        plain=new Plain(true);
        Matrix.setIdentityM(projectionMatrix,0);
        this.adjustingMode=adjustingMode;
    }

    @Override
    public void init() {
        glPassThroughProgram.create();
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        glPassThroughProgram.use();
        plain.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        plain.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());
        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, projectionMatrix, 0);
    }

    @Override
    public void destroy() {
        glPassThroughProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glPassThroughProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();
    }

    public void updateProjection(int videoWidth,int videoHeight){
        this.videoWidth=videoWidth;
        this.videoHeight=videoHeight;
        switch (adjustingMode){
            case ADJUSTING_MODE_STRETCH:
                Matrix.setIdentityM(projectionMatrix,0);
                break;
            case ADJUSTING_MODE_FIT_TO_SCREEN:
                MatrixUtils.updateProjection(videoWidth,videoHeight,
                        surfaceWidth,surfaceHeight,projectionMatrix);
                break;
            case ADJUSTING_MODE_CROP:
                //TODO
                break;
        }
    }

    @Override
    public void onFilterChanged(int width, int height) {
        super.onFilterChanged(width, height);
        updateProjection(videoWidth,videoHeight);
    }
}
