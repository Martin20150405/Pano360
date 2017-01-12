package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;
import android.opengl.GLES20;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.filters.base.GGAbsFilter;
import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLTwoInputProgram;
import com.martin.ads.vrlib.textures.TextureUtils;
import com.martin.ads.vrlib.utils.BufferUtils;
import com.martin.ads.vrlib.utils.PlainTextureRotationUtil;
import com.martin.ads.vrlib.utils.TextureHelper;

import java.nio.FloatBuffer;

/**
 * Created by Ads on 2016/11/20.
 *
 * Using frameBuffer will rotate the image 180 degree around the Y-axis
 * so I rotate the texture before render
 * If you don't have enough filter, use Simpler filter to take position
 * Again,the order of filters matters ! ! !
 */

public class GGMixBlendFilter extends GGAbsFilter {
    private Plain plain;
    private GLTwoInputProgram twoInputProgram;
    private FloatBuffer mTexCoordinateBuffer2;
    private int textureID2;
    private int textureHandle2;
    private Context context;

    //assumed
    private int mMixLocation;
    private float mMixturePercent;

    public GGMixBlendFilter(Context context, int fragmentShaderResourceId,float mMixturePercent) {
        this.context=context;
        plain=new Plain();
        twoInputProgram=new GLTwoInputProgram(context, R.raw.vertex_shader_two_input,fragmentShaderResourceId);
        mTexCoordinateBuffer2= BufferUtils.getFloatBuffer(PlainTextureRotationUtil.TEXTURE_NO_ROTATION,0);
        this.mMixturePercent=mMixturePercent;
    }

    @Override
    public void init() {
        twoInputProgram.create();

        //assumed
        textureHandle2=GLES20.glGetUniformLocation(twoInputProgram.getProgramId()
                , "sTexture2");

        mMixLocation = GLES20.glGetUniformLocation(twoInputProgram.getProgramId(), "mixturePercent");

        textureID2= TextureHelper.loadTexture(context, R.raw.texture_360);
    }

    @Override
    public void onPreDrawElements() {
        mTexCoordinateBuffer2.position(0);
        GLES20.glVertexAttribPointer(twoInputProgram.getMaTexture2Handle(), 2, GLES20.GL_FLOAT, false, 0, mTexCoordinateBuffer2);
        GLES20.glEnableVertexAttribArray(twoInputProgram.getMaTexture2Handle());

        plain.uploadTexCoordinateBuffer(twoInputProgram.getMaTextureHandle());
        plain.uploadVerticesBuffer(twoInputProgram.getMaPositionHandle());

        GLES20.glUniform1f(mMixLocation,mMixturePercent);
    }

    @Override
    public void destroy() {
        twoInputProgram.onDestroy();
        GLES20.glDeleteTextures(1, new int[]{textureID2}, 0);
    }

    @Override
    public void onDrawFrame(int textureId) {
        twoInputProgram.use();
        onPreDrawElements();

        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,twoInputProgram.getuTextureSamplerHandle(),0);
        TextureUtils.bindTexture2D(textureID2,GLES20.GL_TEXTURE1,textureHandle2,1);

        GLES20.glViewport(0,0,width,height);
        plain.draw();
    }

    public void setMixturePercent(float mMixturePercent) {
        this.mMixturePercent = mMixturePercent;
    }

    public void setTextureID2(int textureID2) {
        this.textureID2 = textureID2;
    }
}
