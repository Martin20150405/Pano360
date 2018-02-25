package com.martin.ads.vrlib;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.martin.ads.vrlib.constant.AdjustingMode;
import com.martin.ads.vrlib.filters.advanced.FilterFactory;
import com.martin.ads.vrlib.filters.base.AbsFilter;
import com.martin.ads.vrlib.filters.base.DrawImageFilter;
import com.martin.ads.vrlib.filters.base.FilterGroup;
import com.martin.ads.vrlib.filters.base.OESFilter;
import com.martin.ads.vrlib.filters.base.OrthoFilter;
import com.martin.ads.vrlib.filters.base.PassThroughFilter;
import com.martin.ads.vrlib.filters.vr.Sphere2DPlugin;
import com.martin.ads.vrlib.utils.BitmapUtils;
import com.martin.ads.vrlib.utils.StatusHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ads on 2016/6/25.
 */
public class PanoRender
        implements GLSurfaceView.Renderer {
    public static String TAG = "PanoRender";

    public static final int FILTER_MODE_NONE=0x0001;
    public static final int FILTER_MODE_BEFORE_PROJECTION=0x0002;
    public static final int FILTER_MODE_AFTER_PROJECTION=0x0003;

    //If set to RENDER_SIZE_TEXTURE, the rendering window except the last filter
    //will be resized to fit the size of input texture.
    public static final int RENDER_SIZE_VIEW=0x0004;
    public static final int RENDER_SIZE_TEXTURE=0x0005;

    private StatusHelper statusHelper;
    private PanoMediaPlayerWrapper panoMediaPlayerWrapper;
    private Sphere2DPlugin spherePlugin;
    private FilterGroup filterGroup;
    private AbsFilter firstPassFilter;
    private int surfaceWidth, surfaceHeight;
    private int textureWidth, textureHeight;

    private boolean imageMode;
    private boolean planeMode;
    private boolean saveImg;
    private int filterMode;
    private AbsFilter customizedFilter;
    private Bitmap bitmap;
    private int renderSizeType;

    private PanoRender() {

    }

    public PanoRender init(){
        saveImg=false;
        filterGroup=new FilterGroup();
        customizedFilter=new PassThroughFilter(statusHelper.getContext());

        if(!imageMode) {
            firstPassFilter = new OESFilter(statusHelper.getContext());
        }else{
            DrawImageFilter drawImageFilter=new DrawImageFilter(
                    statusHelper.getContext(),
                    bitmap,
                    AdjustingMode.ADJUSTING_MODE_STRETCH);
            firstPassFilter=drawImageFilter;
            drawImageFilter.setOnTextureSizeChangedCallback(new OnTextureSizeChangedCallback() {
                @Override
                public void notifyTextureSizeChanged(int width, int height) {
                    onTextureSizeChanged(width,height);
                }
            });
        }
        filterGroup.addFilter(firstPassFilter);
        if(filterMode==FILTER_MODE_BEFORE_PROJECTION){
            //the code is becoming more and more messy ┗( T﹏T )┛
            filterGroup.addFilter(customizedFilter);
        }
        spherePlugin=new Sphere2DPlugin(statusHelper);
        //TODO: this should be adjustable
        final OrthoFilter orthoFilter=new OrthoFilter(statusHelper,
                AdjustingMode.ADJUSTING_MODE_FIT_TO_SCREEN);
        if(panoMediaPlayerWrapper!=null){
            panoMediaPlayerWrapper.setOnTextureSizeChangedCallback(new OnTextureSizeChangedCallback() {
                @Override
                public void notifyTextureSizeChanged(int width, int height) {
                    onTextureSizeChanged(width,height);
                    orthoFilter.updateProjection(width,height);
                }
            });
        }
        if(!planeMode){
            filterGroup.addFilter(spherePlugin);
        }else{
            filterGroup.addFilter(orthoFilter);
        }
        if(filterMode==FILTER_MODE_AFTER_PROJECTION){
            filterGroup.addFilter(customizedFilter);
            filterGroup.addPreDrawTask(new Runnable() {
                @Override
                public void run() {
                    customizedFilter=new PassThroughFilter(statusHelper.getContext());
                    alignRenderingAreaWithTexture();
                }
            });
        }
        return this;
    }
    @Override
    public void onSurfaceCreated(GL10 glUnused,EGLConfig config) {
        filterGroup.init();
        if(!imageMode)
            panoMediaPlayerWrapper.setSurface(((OESFilter)firstPassFilter).getGlOESTexture().getTextureId());
    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

        GLES20.glFrontFace(GLES20.GL_CW);
        GLES20.glCullFace(GLES20.GL_BACK);
        GLES20.glEnable(GLES20.GL_CULL_FACE);

        if(!imageMode){
            panoMediaPlayerWrapper.doTextureUpdate(((OESFilter)firstPassFilter).getSTMatrix());
        }
        filterGroup.onDrawFrame(0);

        if (saveImg){
            BitmapUtils.sendImage(surfaceWidth, surfaceHeight,statusHelper.getContext());
            saveImg=false;
        }

        GLES20.glDisable(GLES20.GL_CULL_FACE);
        //GLES20.glFinish();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int surfaceWidth, int surfaceHeight) {
        this.surfaceWidth =surfaceWidth;
        this.surfaceHeight =surfaceHeight;
        alignRenderingAreaWithTexture();
    }

    public void onTextureSizeChanged(int textureWidth,int textureHeight){
        Log.d(TAG, "onTextureSizeChanged: "+textureWidth+" "+textureHeight);
        this.textureWidth=textureWidth;
        this.textureHeight=textureHeight;
        alignRenderingAreaWithTexture();
    }

    public void alignRenderingAreaWithTexture(){
        if(renderSizeType==PanoRender.RENDER_SIZE_TEXTURE && textureWidth>surfaceWidth) {
            double ratio=(double)textureWidth/surfaceWidth;
            filterGroup.onFilterChanged(textureWidth, (int) (surfaceHeight*ratio));
            AbsFilter filter=filterGroup.getLastFilter();
            if(filter!=null) filter.onFilterChanged(surfaceWidth,surfaceHeight);
        }else filterGroup.onFilterChanged(surfaceWidth,surfaceHeight);
    }

    public void saveImg(){
        saveImg=true;
    }

    public Sphere2DPlugin getSpherePlugin() {
        return spherePlugin;
    }

    public FilterGroup getFilterGroup() {
        return filterGroup;
    }

    public PanoRender setStatusHelper(StatusHelper statusHelper) {
        this.statusHelper = statusHelper;
        return this;
    }

    public PanoRender setPanoMediaPlayerWrapper(PanoMediaPlayerWrapper panoMediaPlayerWrapper) {
        this.panoMediaPlayerWrapper = panoMediaPlayerWrapper;
        return this;
    }

    public PanoRender setImageMode(boolean imageMode) {
        this.imageMode = imageMode;
        return this;
    }

    public PanoRender setPlaneMode(boolean planeMode) {
        this.planeMode = planeMode;
        return this;
    }

    public PanoRender setFilterMode(int filterMode) {
        this.filterMode = filterMode;
        return this;
    }

    public PanoRender setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
    }

    public static PanoRender newInstance(){
        return new PanoRender();
    }

    public PanoRender setRenderSizeType(int renderSizeType) {
        this.renderSizeType = renderSizeType;
        return this;
    }

    public void switchFilter(){
        filterGroup.addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                customizedFilter=FilterFactory.randomlyCreateFilter(statusHelper.getContext());
                alignRenderingAreaWithTexture();
            }
        });
    }

    public interface OnTextureSizeChangedCallback{
        void notifyTextureSizeChanged(int width,int height);
    }

    public void addFilter(){
        if(filterGroup!=null){
            //
        }
    }
}