package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLPassThroughProgram;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2017/1/27.
 */

public class PassThroughFilter extends AbsFilter {

    protected GLPassThroughProgram glPassThroughProgram;
    private Plain plain;

    protected Context context;
    protected float[] projectionMatrix = new float[16];

    public PassThroughFilter(Context context) {
        this.context=context;
        glPassThroughProgram=new GLPassThroughProgram(context);
        plain=new Plain(true);
    }

    @Override
    public void init() {
        glPassThroughProgram.create();
    }

    @Override
    public void destroy() {
        glPassThroughProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        glPassThroughProgram.use();
        Matrix.setIdentityM(projectionMatrix,0);
        plain.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        plain.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());
        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, projectionMatrix, 0);
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glPassThroughProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
    }
}
