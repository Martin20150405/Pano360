package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.vrlib.utils.ShaderUtils;


/**
 * Created by Ads on 2016/11/19.
 * with Sampler2D and MVPMatrix
 */

public class GLPassThroughProgram extends GLAbsProgram {

    private int uMVPMatrixHandle;
    private int uTextureSamplerHandle;

    public GLPassThroughProgram(Context context) {
        super(context, "filter/vsh/pass_through.glsl","filter/fsh/pass_through.glsl");
    }

    @Override
    public void create() {
        super.create();
        uTextureSamplerHandle= GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        ShaderUtils.checkGlError("glGetUniformLocation uniform sTexture");
        uMVPMatrixHandle=GLES20.glGetUniformLocation(getProgramId(),"uMVPMatrix");
        ShaderUtils.checkGlError("glGetUniformLocation uMVPMatrix");
    }


    public int getTextureSamplerHandle() {
        return uTextureSamplerHandle;
    }

    public int getMVPMatrixHandle() {
        return uMVPMatrixHandle;
    }
}
