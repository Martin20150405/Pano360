package com.martin.ads.vrlib.textures;

import android.opengl.GLES20;

import com.martin.ads.vrlib.constant.GLEtc;
import com.martin.ads.vrlib.utils.ShaderUtils;


/**
 * Created by Ads on 2016/11/19.
 */

public class GLOESTexture {
    public final static String TAG = "GLOESTexture";

    private int textureId;

    //TODO:delete it
    private boolean textureLoaded;
    public GLOESTexture() {
        textureId= GLEtc.NO_TEXTURE;
        textureLoaded=false;
    }

    //only call once for a single video texture (camera, media_decoder,etc.)
    public void loadTexture(){
        if(textureLoaded) return;
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        GLES20.glBindTexture(GLEtc.GL_TEXTURE_EXTERNAL_OES, textureId);
        ShaderUtils.checkGlError("glBindTexture mTextureID");
        GLES20.glTexParameterf(GLEtc.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLEtc.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        textureLoaded=true;
    }

    public void deleteTexture(){
        int[] textures = new int[1];
        textures[0]=textureId;
        GLES20.glDeleteTextures(1,textures,0);
    }

    public int getTextureId() {
        return textureId;
    }
}
