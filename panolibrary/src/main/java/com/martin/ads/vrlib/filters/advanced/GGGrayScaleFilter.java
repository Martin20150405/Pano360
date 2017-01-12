package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;
import android.opengl.GLES20;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.filters.base.GGAbsFilter;
import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLSimpleProgram;
import com.martin.ads.vrlib.textures.TextureUtils;

/**
 * Created by Ads on 2016/11/19.
 */

public class GGGrayScaleFilter extends GGAbsFilter {

    private GLSimpleProgram glSimpleProgram;
    private Plain plain;

    public GGGrayScaleFilter(Context context) {
        glSimpleProgram=new GLSimpleProgram(context,R.raw.vertex_shader_simple,R.raw.fragment_shader_simple_gray_scale_2d);
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
