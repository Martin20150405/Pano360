package com.martin.ads.vrlib.filters.vr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.vrlib.constant.AdjustingMode;
import com.martin.ads.vrlib.filters.base.AbsFilter;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.object.Plane;
import com.martin.ads.vrlib.programs.GLPassThroughProgram;
import com.martin.ads.vrlib.textures.BitmapTexture;
import com.martin.ads.vrlib.utils.MatrixUtils;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2017/3/11.
 */

public abstract class AbsHotspot extends AbsFilter{
    protected static final int DEFAULT_SCALE_FACTOR=5000;

    protected Plane imagePlane;
    protected Context context;

    protected float[] hotSpotModelMatrix = new float[16];
    protected float[] tmpMatrix = new float[16];
    protected float[] resultMatrix = new float[16];
    protected float[] modelMatrix = new float[16];
    protected float[] viewMatrix = new float[16];
    protected float[] projectionMatrix = new float[16];
    protected float[] hotspotOrthoProjectionMatrix = new float[16];
    protected float[] modelViewMatrix = new float[16];
    protected float[] mMVPMatrix = new float[16];

    protected PositionOrientation positionOrientation;

    protected float assumedScreenWidth;
    protected float assumedScreenHeight;

    public AbsHotspot(Context context) {
        this.context = context;
        Matrix.setIdentityM(hotspotOrthoProjectionMatrix,0);
        assumedScreenWidth=assumedScreenHeight=2;
    }

    public void init(){
        imagePlane.resetTrianglesDataWithRect(
                new RectF(
                        -assumedScreenWidth/2,
                        assumedScreenHeight/2,
                        assumedScreenWidth/2,
                        -assumedScreenHeight/2
                    )
                )
                .scale(4.5f);
    }

    protected void updateMatrix(){
        positionOrientation.updateModelMatrix(hotSpotModelMatrix);
        Matrix.multiplyMM(resultMatrix,0,hotSpotModelMatrix,0,hotspotOrthoProjectionMatrix,0);
        Matrix.multiplyMM(tmpMatrix, 0,modelMatrix , 0,resultMatrix , 0);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, tmpMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);
    }

    public void setViewMatrix(float[] viewMatrix) {
        System.arraycopy(viewMatrix,
                0,
                this.viewMatrix,
                0,
                this.viewMatrix.length);
    }

    public void setProjectionMatrix(float[] projectionMatrix) {
        System.arraycopy(projectionMatrix,0,this.projectionMatrix,0,this.projectionMatrix.length);
    }

    public void setModelMatrix(float[] modelMatrix) {
        System.arraycopy(modelMatrix,
                0,
                this.modelMatrix,
                0,
                this.modelMatrix.length);
    }

    public void notifyOnPause(){

    }

    public void notifyOnResume(){

    }

    public void notifyOnDestroy(){

    }
}
