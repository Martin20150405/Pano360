package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.panolibrary.R;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;

/**
 * Created by Ads on 2016/11/19.
 * with Sampler2D
 */

public class GLSimpleProgram extends GLAbsProgram {

    private int uTextureSamplerHandle;

    public GLSimpleProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        super(context, vertexShaderResourceId, fragmentShaderResourceId);
    }

    @Override
    public void create() {
        super.create();
        uTextureSamplerHandle=GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");
    }

    public int getuTextureSamplerHandle() {
        return uTextureSamplerHandle;
    }
}
