package com.martin.ads.vrlib.filters.base;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.vrlib.filters.advanced.FilterFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ads on 2016/11/19.
 */

public class FilterGroup extends AbsFilter {
    private static final String TAG = "FilterGroup";
    private int[] frameBuffers = null;
    private int[] frameBufferTextures = null;
    private List<AbsFilter> filters;
    private boolean isRunning;

    public FilterGroup() {
        super();
        filters=new ArrayList<AbsFilter>();
    }

    @Override
    public void init() {
        for (AbsFilter filter : filters) {
            filter.init();
        }
        isRunning=true;
    }

    @Override
    public void onPreDrawElements() {
    }

    @Override
    public void destroy() {
        destroyFrameBuffers();
        for (AbsFilter filter : filters) {
            filter.destroy();
        }
        isRunning=false;
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
            AbsFilter filter = filters.get(i);
            if (i < size - 1) {
                GLES20.glViewport(0, 0, surfaceWidth, surfaceHeight);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBuffers[i]);
                GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
                filter.onDrawFrame(previousTexture);
                GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
                previousTexture = frameBufferTextures[i];
            }else{
                GLES20.glViewport(0, 0 ,surfaceWidth, surfaceHeight);
                filter.onDrawFrame(previousTexture);
            }
        }
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
        int size = filters.size();
        for (int i = 0; i < size; i++) {
            filters.get(i).onFilterChanged(surfaceWidth, surfaceHeight);
        }
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
                GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, surfaceWidth, surfaceHeight, 0,
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

    public void addFilter(final AbsFilter filter){
        if (filter==null) return;
        //If one filter is added multiple times,
        //it will execute the same times
        //BTW: Pay attention to the order of execution
        if (!isRunning){
            filters.add(filter);
        }
        else
            addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                filter.init();
                filters.add(filter);
                onFilterChanged(surfaceWidth,surfaceHeight);
            }
        });
    }

    public void randomSwitchFilter(Context context){
        final AbsFilter filter= FilterFactory.randomlyCreateFilter(context);

        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                for(AbsFilter absFilter:filters){
                    absFilter.destroy();
                }
                filters.clear();
                filter.init();
                filters.add(filter);
                onFilterChanged(surfaceWidth,surfaceHeight);
            }
        });
    }

}
