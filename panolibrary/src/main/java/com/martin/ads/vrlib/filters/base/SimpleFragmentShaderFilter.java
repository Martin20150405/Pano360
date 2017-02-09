package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLSimpleProgram;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2017/1/31.
 */

public class SimpleFragmentShaderFilter extends AbsFilter {

    protected GLSimpleProgram glSimpleProgram;
    protected Plain plain;

    public SimpleFragmentShaderFilter(Context context,
                                      final String fragmentShaderPath) {
        glSimpleProgram=new GLSimpleProgram(context, "filter/vsh/simple.glsl",fragmentShaderPath);
        plain=new Plain(true);
    }

    @Override
    public void init() {
        glSimpleProgram.create();
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        glSimpleProgram.use();
        plain.uploadTexCoordinateBuffer(glSimpleProgram.getTextureCoordinateHandle());
        plain.uploadVerticesBuffer(glSimpleProgram.getPositionHandle());
    }

    @Override
    public void destroy() {
        glSimpleProgram.onDestroy();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();
    }
}
