package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.panolibrary.R;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;

/**
 * Created by Ads on 2016/11/8.
 * draw texture2D on sphere
 * with MVP/Sampler2D
 */
public class GLSphere2DProgram extends GLAbsProgram{

    private int muMVPMatrixHandle;
    private int uTextureSamplerHandle;

    public GLSphere2DProgram(Context context){
        super(context, R.raw.vertex_shader_sphere_2d,R.raw.fragment_shader_simple_2d);
    }

    @Override
    public void create(){
        super.create();

        muMVPMatrixHandle = GLES20.glGetUniformLocation(getProgramId(), "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        uTextureSamplerHandle=GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");

    }

    public int getMuMVPMatrixHandle() {
        return muMVPMatrixHandle;
    }

    public int getUTextureSamplerHandle() {
        return uTextureSamplerHandle;
    }
}
