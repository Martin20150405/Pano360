package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;
import android.opengl.GLES20;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.filters.base.AbsFilter;
import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLSimpleProgram;
import com.martin.ads.vrlib.utils.TextureUtils;

/**
 * Created by Ads on 2016/11/20.
 */

abstract class ThreeTextureFilter extends AbsFilter {
    private GLSimpleProgram threeTextureProgram;
    private Plain plain;
    private int []textureIDs;
    private int []textureHandles;
    private Context context;

    public ThreeTextureFilter(Context context,
                              final String vertexShaderPath,
                              final String fragmentShaderPath) {
        this.context=context;
        threeTextureProgram=new GLSimpleProgram(context,vertexShaderPath,fragmentShaderPath);
        plain=new Plain(true);
    }

    @Override
    public void init() {
        threeTextureProgram.create();

        textureHandles=new int[3];
        textureHandles[0]=GLES20.glGetUniformLocation(threeTextureProgram.getProgramId()
                , "sTexture2");
        textureHandles[1]=GLES20.glGetUniformLocation(threeTextureProgram.getProgramId()
                , "sTexture3");
        textureHandles[2]=GLES20.glGetUniformLocation(threeTextureProgram.getProgramId()
                , "sTexture4");

        textureIDs=new int[3];
        textureIDs[0]= TextureUtils.loadTextureFromAssets(context,"filter/imgs/blackboard.png",null);
        textureIDs[1]= TextureUtils.loadTextureFromAssets(context,"filter/imgs/overlay_map.png",null);
        textureIDs[2]= TextureUtils.loadTextureFromAssets(context,"filter/imgs/rise_map.png",null);
    }

    @Override
    public void onPreDrawElements() {
        plain.uploadTexCoordinateBuffer(threeTextureProgram.getTextureCoordinateHandle());
        plain.uploadVerticesBuffer(threeTextureProgram.getPositionHandle());
    }

    @Override
    public void destroy() {
        threeTextureProgram.onDestroy();
        GLES20.glDeleteTextures(3, textureIDs, 0);
    }

    @Override
    public void onDrawFrame(int textureId) {
        threeTextureProgram.use();
        onPreDrawElements();

        TextureUtils.bindTexture2D(textureId,GLES20.GL_TEXTURE0,threeTextureProgram.getTextureSamplerHandle(),0);
        TextureUtils.bindTexture2D(textureIDs[0],GLES20.GL_TEXTURE1,textureHandles[0],1);
        TextureUtils.bindTexture2D(textureIDs[1],GLES20.GL_TEXTURE2,textureHandles[1],2);
        TextureUtils.bindTexture2D(textureIDs[2],GLES20.GL_TEXTURE3,textureHandles[2],3);

        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plain.draw();
    }

}
