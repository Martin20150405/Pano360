package com.martin.ads.vrlib.textures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.martin.ads.vrlib.constant.Constants;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;

/**
 * Created by Ads on 2016/11/19.
 */

public class GLOESTexture {
    public final static String TAG = "GL2DTexture";

    private int textureId;

    public GLOESTexture() {
        textureId= Constants.NO_TEXTURE;
    }

    //only call once for a single video texture (camera, media_decoder,etc.)
    public void loadTexture(){
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        GLES20.glBindTexture(Constants.GL_TEXTURE_EXTERNAL_OES, textureId);
        checkGlError("glBindTexture mTextureID");

        GLES20.glTexParameterf(Constants.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(Constants.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
    }

    public int getTextureId() {
        return textureId;
    }
}
