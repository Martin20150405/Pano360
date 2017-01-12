package com.martin.ads.vrlib.textures;

import android.opengl.GLES20;
import android.util.Log;

import com.martin.ads.vrlib.constant.Constants;

/**
 * Created by Ads on 2016/11/19.
 */

public class TextureUtils {
    public static void bindTexture2D(int textureId,int activeTextureID,int handle,int idx){
        if (textureId != Constants.NO_TEXTURE) {
            GLES20.glActiveTexture(activeTextureID);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glUniform1i(handle, idx);
        }
    }

    public static void bindTextureOES(int textureId,int activeTextureID,int handle,int idx){
        if (textureId != Constants.NO_TEXTURE) {
            GLES20.glActiveTexture(activeTextureID);
            GLES20.glBindTexture(Constants.GL_TEXTURE_EXTERNAL_OES, textureId);
            GLES20.glUniform1i(handle, idx);
        }
    }
}
