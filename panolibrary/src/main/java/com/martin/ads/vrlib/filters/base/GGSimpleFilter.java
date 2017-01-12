package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLSimpleProgram;
import com.martin.ads.vrlib.textures.TextureUtils;

/**
 * Created by Ads on 2016/11/19.
 */

//Don't extend this class,extend GGAbsFilter instead !!!
public class GGSimpleFilter extends GGAbsFilter{

    private GLSimpleProgram glSimpleProgram;
    private Plain plain;

    public GGSimpleFilter(Context context) {
        glSimpleProgram=new GLSimpleProgram(context,R.raw.vertex_shader_simple,R.raw.fragment_shader_simple_2d);
        plain=new Plain();
    }

    @Override
    public void init() {
        glSimpleProgram.create();
    }

    @Override
    public void onPreDrawElements() {

    }

    @Override
    public void destroy() {
        glSimpleProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        glSimpleProgram.use();
        plain.uploadTexCoordinateBuffer(glSimpleProgram.getMaTextureHandle());
        plain.uploadVerticesBuffer(glSimpleProgram.getMaPositionHandle());

        TextureUtils.bindTexture2D(textureId,GLES20.GL_TEXTURE0,glSimpleProgram.getuTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,width,height);
        plain.draw();
    }
}
