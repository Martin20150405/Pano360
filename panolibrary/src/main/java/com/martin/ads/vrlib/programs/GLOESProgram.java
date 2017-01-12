package com.martin.ads.vrlib.programs;

import android.content.Context;
import android.opengl.GLES20;

import com.example.panolibrary.R;

import static com.martin.ads.vrlib.utils.ShaderUtils.checkGlError;

/**
 * Created by Ads on 2016/11/8.
 * Translate YUV420SP to RGBA
 * with STM/OES
 */
public class GLOESProgram extends GLAbsProgram{

    private int muSTMatrixHandle;
    private int uTextureSamplerHandle;

    public GLOESProgram(Context context){
        super(context, R.raw.vertex_shader_simple_oes,R.raw.fragment_shader_oes);
    }

    @Override
    public void create(){
        super.create();
        muSTMatrixHandle = GLES20.glGetUniformLocation(getProgramId(), "uSTMatrix");
        checkGlError("glGetUniformLocation uSTMatrix");
        if (muSTMatrixHandle == -1) {
            throw new RuntimeException("Could not get attrib location for uSTMatrix");
        }

        uTextureSamplerHandle=GLES20.glGetUniformLocation(getProgramId(),"sTexture");
        checkGlError("glGetUniformLocation uniform samplerExternalOES sTexture");
    }

    public int getMuSTMatrixHandle() {
        return muSTMatrixHandle;
    }

    public int getUTextureSamplerHandle() { return uTextureSamplerHandle; }
}
