package com.martin.ads.vrlib.filters.advanced.mx;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.martin.ads.vrlib.filters.base.SimpleFragmentShaderFilter;
import com.martin.ads.vrlib.textures.BitmapTexture;
import com.martin.ads.vrlib.utils.ShaderUtils;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2017/1/31.
 */

class MxOneHashBaseFilter extends SimpleFragmentShaderFilter {
    private static final int HISTOGRAM_SIZE = 256;
    private static int[] mHistogram = new int[HISTOGRAM_SIZE];
    static int[] rgbMap=null;
    private BitmapTexture bitmapTexture;

    private int uTextureSamplerHandle2;

    public MxOneHashBaseFilter(Context context, String fragmentShaderPath) {
        super(context, fragmentShaderPath);
    }

    @Override
    public void init() {
        super.init();
        for(int i=0;i<HISTOGRAM_SIZE;i++){
            mHistogram[i]=(rgbMap[i] << 24);
        }

        bitmapTexture=new BitmapTexture();
        bitmapTexture.loadBitmap(Bitmap.createBitmap(mHistogram, 256, 1, Bitmap.Config.ARGB_8888));

        uTextureSamplerHandle2= GLES20.glGetUniformLocation(glSimpleProgram.getProgramId(),"sTexture2");
        ShaderUtils.checkGlError("glGetUniformLocation sTexture2");
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        TextureUtils.bindTexture2D(bitmapTexture.getImageTextureId(),
                GLES20.GL_TEXTURE1,
                uTextureSamplerHandle2,1);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }
}
