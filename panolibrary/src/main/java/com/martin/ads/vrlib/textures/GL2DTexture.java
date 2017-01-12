package com.martin.ads.vrlib.textures;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import com.martin.ads.vrlib.constant.Constants;
import com.martin.ads.vrlib.utils.TextureHelper;

/**
 * Created by Ads on 2016/11/19.
 */

public class GL2DTexture {
    public final static String TAG = "GL2DTexture";

    private int textureId;

    public GL2DTexture() {
        textureId= Constants.NO_TEXTURE;
    }

    //only call once for a single image texture
    public int loadTexture(Context context, int resourceId){
        this.textureId= TextureHelper.loadTexture(context,resourceId);
        return textureId;
    }

    public int getTextureId() {
        return textureId;
    }
}
