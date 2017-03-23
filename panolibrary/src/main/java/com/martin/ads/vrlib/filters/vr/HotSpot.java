package com.martin.ads.vrlib.filters.vr;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.martin.ads.vrlib.filters.base.AbsFilter;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLPassThroughProgram;
import com.martin.ads.vrlib.textures.BitmapTexture;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2017/3/11.
 */

public class HotSpot extends AbsFilter{
    private GLPassThroughProgram glPassThroughProgram;
    private Plain imagePlain;
    private Context context;

    private float[] hotSpotModelMatrix = new float[16];
    private float[] tmpMatrix = new float[16];
    private float[] modelMatrix = new float[16];
    private float[] viewMatrix = new float[16];
    private float[] projectionMatrix = new float[16];
    private float[] modelViewMatrix = new float[16];
    private float[] mMVPMatrix = new float[16];

    private BitmapTexture bitmapTexture;
    private String imagePath;
    private PositionOrientation positionOrientation;

    private HotSpot(Context context) {
        this.context = context;
        bitmapTexture=new BitmapTexture();
        imagePlain=new Plain(false).scale(4.5f);
        glPassThroughProgram=new GLPassThroughProgram(context);
    }

    public void init(){
        glPassThroughProgram.create();
        bitmapTexture.load(context,imagePath);
    }

    @Override
    public void destroy() {
        glPassThroughProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        glPassThroughProgram.use();
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        TextureUtils.bindTexture2D(bitmapTexture.getImageTextureId(), GLES20.GL_TEXTURE1,glPassThroughProgram.getTextureSamplerHandle(),1);
        imagePlain.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        imagePlain.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());

        positionOrientation.updateModelMatrix(hotSpotModelMatrix);
        Matrix.multiplyMM(tmpMatrix, 0,modelMatrix , 0,hotSpotModelMatrix , 0);
        Matrix.multiplyMM(modelViewMatrix, 0, viewMatrix, 0, tmpMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, projectionMatrix, 0, modelViewMatrix, 0);

        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, mMVPMatrix, 0);
        imagePlain.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public HotSpot setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public HotSpot setPositionOrientation(PositionOrientation positionOrientation) {
        this.positionOrientation = positionOrientation;
        return this;
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

    public static HotSpot with(Context context){
        return new HotSpot(context);
    }
}
