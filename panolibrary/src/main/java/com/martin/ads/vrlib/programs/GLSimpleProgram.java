package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.vrlib.utils.ShaderUtils;


/**
 * Created by Ads on 2016/11/19.
 * with Sampler2D
 */

public class GLSimpleProgram extends GLAbsProgram {

    private int uTextureSamplerHandle;

    public GLSimpleProgram(Context context,
                           final String vertexShaderPath,
                           final String fragmentShaderPath) {
        super(context, vertexShaderPath, fragmentShaderPath);
    }

    @Override
    public void create() {
        super.create();
        uTextureSamplerHandle= GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        ShaderUtils.checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");
    }

    public int getTextureSamplerHandle() {
        return uTextureSamplerHandle;
    }
}
