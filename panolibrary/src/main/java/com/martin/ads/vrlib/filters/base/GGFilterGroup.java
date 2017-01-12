package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;
import android.util.Log;

import com.martin.ads.vrlib.constant.Rotation;
import com.martin.ads.vrlib.filters.advanced.GGSphere2DPlugin;
import com.martin.ads.vrlib.utils.BufferUtils;
import com.martin.ads.vrlib.utils.PlainTextureRotationUtil;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ads on 2016/11/19.
 */

public class GGFilterGroup extends GGAbsFilter{

    protected static int[] frameBuffers = null;
    protected static int[] frameBufferTextures = null;
    protected List<GGAbsFilter> filters;
    private boolean isRunning;

    private final FloatBuffer mTextureVerticalFlipBuffer;
    private final FloatBuffer mTextureVerticalOriginalBuffer;

    public GGFilterGroup() {
        super();
        filters=new ArrayList<GGAbsFilter> ();
        mTextureVerticalFlipBuffer= BufferUtils.getFloatBuffer(
                PlainTextureRotationUtil.getRotation(Rotation.NORMAL, false, true),
                0
        );
        mTextureVerticalOriginalBuffer = BufferUtils.getFloatBuffer(
                PlainTextureRotationUtil.TEXTURE_NO_ROTATION,
                0
        );
    }

    @Override
    public void init() {
        for (GGAbsFilter filter : filters) {
            filter.init();
        }
        isRunning=true;
    }

    @Override
    public void onPreDrawElements() {
    }

    @Override
    public void destroy() {
        isRunning=false;
        destroyFrameBuffers();
        for (GGAbsFilter filter : filters) {
            filter.destroy();
        }
    }

    @Override
    public void onDrawFrame(int textureId) {
        runPreDrawTasks();
        if (frameBuffers == null || frameBufferTextures == null) {
            return ;
        }
        int size = filters.size();
        int previousTexture = textureId;
        for (int i = 0; i < size; i++) {
            GGAbsFilter filter = filters.get(i);
            if (i < size - 1) {
                GLES20.glViewport(0, 0, width, height);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[i]);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                filter.onDrawFrame(previousTexture);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                previousTexture = frameBufferTextures[i];
            }else{
                GLES20.glViewport(0, 0 ,width, height);
                filter.onDrawFrame(previousTexture);
            }
        }
    }

    @Override
    public void onFilterChanged(int width, int height) {
        super.onFilterChanged(width, height);
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            filters.get(i).onFilterChanged(width, height);
        }
//        if (size%2==0){
//            if (filters.get(0) instanceof GGOESFilter){
//                ((GGOESFilter) filters.get(0)).
//                        getPlain().setTexCoordinateBuffer(mTextureVerticalFlipBuffer);
//            }
//        }else{
//            if (filters.get(0) instanceof GGOESFilter){
//                ((GGOESFilter) filters.get(0)).
//                        getPlain().setTexCoordinateBuffer(mTextureVerticalOriginalBuffer);
//            }
//        }
        if(frameBuffers != null){
            destroyFrameBuffers();
        }
        if (frameBuffers == null) {
            frameBuffers = new int[size-1];
            frameBufferTextures = new int[size-1];

            for (int i = 0; i < size-1; i++) {
                GLES20.glGenFramebuffers(1, frameBuffers, i);

                GLES20.glGenTextures(1, frameBufferTextures, i);
                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, frameBufferTextures[i]);
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
                        GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
                GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
                        GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[i]);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                        GLES20.GL_TEXTURE_2D, frameBufferTextures[i], 0);

                GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            }
        }
    }

    private void destroyFrameBuffers() {
        if (frameBufferTextures != null) {
            GLES20.glDeleteTextures(frameBufferTextures.length, frameBufferTextures, 0);
            frameBufferTextures = null;
        }
        if (frameBuffers != null) {
            GLES20.glDeleteFramebuffers(frameBuffers.length, frameBuffers, 0);
            frameBuffers = null;
        }
    }

    public void addFilter(final GGAbsFilter filter){
        if (filter==null) return;
        //Warning: if one filter is added multiple times
        //it will executed the same times
        //BTW: the order of execution matters
        if (!isRunning){
            filters.add(filter);
        }
        else
            addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                filter.init();
                //make sure sphere renders last(you can implement in other ways)
                filters.add(filter);
                onFilterChanged(width,height);
            }
        });
    }

    //TODO : Not correct
    public void removeFilter(GGAbsFilter filter){
        if (filter!=null && filters.contains(filter)) filters.remove(filter);
    }
}
