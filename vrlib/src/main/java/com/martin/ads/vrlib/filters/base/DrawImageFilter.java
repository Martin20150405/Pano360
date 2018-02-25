package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import com.martin.ads.vrlib.PanoRender;
import com.martin.ads.vrlib.constant.AdjustingMode;
import com.martin.ads.vrlib.object.Plane;
import com.martin.ads.vrlib.textures.BitmapTexture;
import com.martin.ads.vrlib.utils.MatrixUtils;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2017/1/27.
 * Draw an image on the scene.
 */

public class DrawImageFilter extends PassThroughFilter {

    private Plane imagePlane;
    private BitmapTexture bitmapTexture;
    private Bitmap bitmap;
    private int adjustingMode;

    private PanoRender.OnTextureSizeChangedCallback onTextureSizeChangedCallback;

    public DrawImageFilter(Context context,Bitmap bitmap,int adjustingMode) {
        super(context);
        bitmapTexture=new BitmapTexture();
        imagePlane =new Plane(false);
        this.bitmap=bitmap;
        this.adjustingMode=adjustingMode;
    }

    @Override
    public void init() {
        super.init();
        bitmapTexture.loadBitmap(bitmap);
        if(onTextureSizeChangedCallback!=null)
            onTextureSizeChangedCallback.notifyTextureSizeChanged(bitmapTexture.getImageWidth(),bitmapTexture.getImageHeight());
    }

    @Override
    public void onDrawFrame(int textureId) {
        //If we want to draw the image fullscreen,
        //the previous texture can be ignored.
        if(adjustingMode!= AdjustingMode.ADJUSTING_MODE_STRETCH){
            super.onDrawFrame(textureId);
        }else{
            onPreDrawElements();
            glPassThroughProgram.use();
            Matrix.setIdentityM(projectionMatrix,0);
            GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        }
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        TextureUtils.bindTexture2D(bitmapTexture.getImageTextureId(), GLES20.GL_TEXTURE0,glPassThroughProgram.getTextureSamplerHandle(),0);
        imagePlane.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        imagePlane.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());
        MatrixUtils.updateProjection(
                bitmapTexture.getImageWidth(),
                bitmapTexture.getImageHeight(),
                surfaceWidth,
                surfaceHeight,
                adjustingMode,
                projectionMatrix);
        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, projectionMatrix, 0);
        imagePlane.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    public void setOnTextureSizeChangedCallback(PanoRender.OnTextureSizeChangedCallback onTextureSizeChangedCallback) {
        this.onTextureSizeChangedCallback = onTextureSizeChangedCallback;
    }
}
