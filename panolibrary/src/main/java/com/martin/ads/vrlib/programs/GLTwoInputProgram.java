package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;

/**
 * Created by Ads on 2016/11/20.
 */

//It is optional to split reference to a program
public class GLTwoInputProgram extends GLAbsProgram {
    private int uTextureSamplerHandle;
    private int maTexture2Handle;

    public GLTwoInputProgram(Context context, int vertexShaderResourceId, int fragmentShaderResourceId) {
        super(context, vertexShaderResourceId, fragmentShaderResourceId);
    }

    @Override
    public void create() {
        super.create();
        maTexture2Handle = GLES20.glGetAttribLocation(getProgramId(), "aTextureCoord2");
        checkGlError("glGetAttribLocation aTextureCoord");
        if (maTexture2Handle == -1) {
            throw new RuntimeException("Could not get attrib location for aTextureCoord2");
        }
        uTextureSamplerHandle= GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");

    }

    public int getuTextureSamplerHandle() {
        return uTextureSamplerHandle;
    }

    public int getMaTexture2Handle() {
        return maTexture2Handle;
    }

}
