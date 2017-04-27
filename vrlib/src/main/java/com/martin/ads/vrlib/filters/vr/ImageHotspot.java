package com.martin.ads.vrlib.filters.vr;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.martin.ads.vrlib.constant.AdjustingMode;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.object.Plane;
import com.martin.ads.vrlib.programs.GLPassThroughProgram;
import com.martin.ads.vrlib.textures.BitmapTexture;
import com.martin.ads.vrlib.utils.MatrixUtils;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2017/4/11.
 */

public class ImageHotspot extends AbsHotspot {
    private BitmapTexture bitmapTexture;
    private String imagePath;
    private Bitmap bitmap;
    private GLPassThroughProgram glPassThroughProgram;

    private ImageHotspot(Context context) {
        super(context);
        imagePlane =new Plane(false);
        bitmapTexture=new BitmapTexture();
        glPassThroughProgram=new GLPassThroughProgram(context);
    }

    @Override
    public void init() {
        super.init();
        glPassThroughProgram.create();
        if(imagePath!=null)
            bitmapTexture.loadWithAssetFile(context,imagePath);
        else bitmapTexture.loadBitmap(bitmap);

        MatrixUtils.updateProjection(
                bitmapTexture.getImageWidth(),
                bitmapTexture.getImageHeight(),
                (int)(DEFAULT_SCALE_FACTOR*assumedScreenWidth),
                (int)(DEFAULT_SCALE_FACTOR*assumedScreenHeight),
                AdjustingMode.ADJUSTING_MODE_FIT_TO_SCREEN,
                hotspotOrthoProjectionMatrix);
    }

    @Override
    public void onDrawFrame(int textureId) {
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        glPassThroughProgram.use();
        TextureUtils.bindTexture2D(bitmapTexture.getImageTextureId(), GLES20.GL_TEXTURE1,glPassThroughProgram.getTextureSamplerHandle(),1);
        imagePlane.uploadTexCoordinateBuffer(glPassThroughProgram.getTextureCoordinateHandle());
        imagePlane.uploadVerticesBuffer(glPassThroughProgram.getPositionHandle());
        updateMatrix();
        GLES20.glUniformMatrix4fv(glPassThroughProgram.getMVPMatrixHandle(), 1, false, mMVPMatrix, 0);
        imagePlane.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void destroy() {
        glPassThroughProgram.onDestroy();
    }

    public ImageHotspot setPositionOrientation(PositionOrientation positionOrientation) {
        this.positionOrientation = positionOrientation;
        return this;
    }

    /**
     * if you have set imagePath, the bitmap will not be used.
     * @param imagePath
     * @return
     */
    public ImageHotspot setImagePath(String imagePath) {
        this.imagePath = imagePath;
        return this;
    }

    public ImageHotspot setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public ImageHotspot setAssumedScreenSize(float assumedScreenWidth, float assumedScreenHeight) {
        this.assumedScreenWidth = assumedScreenWidth;
        this.assumedScreenHeight = assumedScreenHeight;
        return this;
    }

    public static ImageHotspot with(Context context){
        return new ImageHotspot(context);
    }
}
