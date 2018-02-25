package com.martin.ads.vrlib.filters.base;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.object.Plane;
import com.martin.ads.vrlib.programs.GLPassThroughProgram;
import com.martin.ads.vrlib.utils.MatrixUtils;
import com.martin.ads.vrlib.utils.StatusHelper;
import com.martin.ads.vrlib.utils.TextureUtils;


/**
 * Created by Ads on 2016/11/19.
 * let the image pass through
 * and simply fit the image to the screen
 */

public class OrthoFilter extends AbsFilter {

    private int adjustingMode;

    private GLPassThroughProgram glPassThroughProgram;
    private Plane plane;

    private float[] projectionMatrix = new float[16];

    private int videoWidth,videoHeight;

    private StatusHelper statusHelper;

    public OrthoFilter(StatusHelper statusHelper,int adjustingMode) {
        this.statusHelper=statusHelper;
        glPassThroughProgram=new GLPassThroughProgram(statusHelper.getContext());
        plane =new Plane(true);
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
        int targetSurfaceWidth=surfaceWidth;
        if (statusHelper.getPanoDisPlayMode()== PanoMode.DUAL_SCREEN){
            targetSurfaceWidth/=2;
        }
        MatrixUtils.updateProjection(
                videoWidth,
                videoHeight,
                targetSurfaceWidth,
                surfaceHeight,
                adjustingMode,
                projectionMatrix);
        glPassThroughProgram.use();
        plane.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        plane.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());
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
        if (statusHelper.getPanoDisPlayMode()== PanoMode.DUAL_SCREEN){
            GLES20.glViewport(0,0,surfaceWidth/2,surfaceHeight);
            plane.draw();
            GLES20.glViewport(surfaceWidth/2,0,surfaceWidth-surfaceWidth/2,surfaceHeight);
            plane.draw();
        }else{
            GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
            plane.draw();
        }
    }

    public void updateProjection(int videoWidth,int videoHeight){
        this.videoWidth=videoWidth;
        this.videoHeight=videoHeight;
        //TODO:it's not the right way to switch mode.
    }

}
