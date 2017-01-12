package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.panolibrary.R;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;

/**
 * Created by Ads on 2016/11/8.
 * original with:STM MVP / OES
 * this code is reserved for future use
 */
@Deprecated
public class GLSphereProgram extends GLAbsProgram{

    private int muMVPMatrixHandle;
    private int muSTMatrixHandle;

    private int uTextureSamplerHandle;

    public GLSphereProgram(Context context){
        super(context, R.raw.original_vertex_shader_sphere_oes,R.raw.fragment_shader_oes);
    }

    @Override
    public void create(){
        super.create();

        muMVPMatrixHandle = GLES20.glGetUniformLocation(getProgramId(), "uMVPMatrix");
        checkGlError("glGetUniformLocation uMVPMatrix");
        if (muMVPMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uMVPMatrix");
        }

        muSTMatrixHandle = GLES20.glGetUniformLocation(getProgramId(), "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        uTextureSamplerHandle=GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");
    }

    public int getMuMVPMatrixHandle() {
        return muMVPMatrixHandle;
    }

    public int getMuSTMatrixHandle() {
        return muSTMatrixHandle;
    }

    public int getUTextureSamplerHandle() { return uTextureSamplerHandle; }
}
