package com.martin.ads.vrlib;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.martin.ads.vrlib.filters.advanced.GGGrayScaleFilter;
import com.martin.ads.vrlib.filters.advanced.GGRiseFilter;
import com.martin.ads.vrlib.filters.advanced.GGSphere2DPlugin;
import com.martin.ads.vrlib.filters.base.GGFilterGroup;
import com.martin.ads.vrlib.filters.base.GGOESFilter;
import com.martin.ads.vrlib.utils.BitmapUtils;
import com.martin.ads.vrlib.utils.OrientationHelper;
import com.martin.ads.vrlib.utils.StatusHelper;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ads on 2016/6/25.
 */
public class PanoRender
        implements GLSurfaceView.Renderer {
    public static String TAG = "PanoRender";

    private StatusHelper statusHelper;
    private PanoMediaPlayerWrapper panoMediaPlayerWrapper;
    private GGSphere2DPlugin spherePlugin;
    private GGFilterGroup filterGroup;
    private GGOESFilter ggoesFilter;

    private int width,height;

    private boolean saveImg;

    public PanoRender(StatusHelper statusHelper,PanoMediaPlayerWrapper panoMediaPlayerWrapper) {
        this.statusHelper=statusHelper;
        this.panoMediaPlayerWrapper = panoMediaPlayerWrapper;
        saveImg=false;
        spherePlugin=new GGSphere2DPlugin(statusHelper);
        filterGroup=new GGFilterGroup();
        ggoesFilter=new GGOESFilter(statusHelper.getContext(),GGOESFilter.ADJUSTING_MODE_STRETCH);

        filterGroup.addFilter(ggoesFilter);
        //you can add filters here

        //like filterGroup.addFilter(new GGRiseFilter(statusHelper.getContext()))

        filterGroup.addFilter(spherePlugin);

        //filterGroup.addFilter(new GGGrayScaleFilter(statusHelper.getContext()));

        //you can also add filters here
        //pay attention to the order of execution

        //TODO:remove to outer layer
        //if you want to play a plane video,remove
        //filterGroup.addFilter(spherePlugin);
        //and reset adjustingMode of GGOESFilter
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused,EGLConfig config) {
        filterGroup.init();
        panoMediaPlayerWrapper.setSurface(ggoesFilter.getGloesTexture().getTextureId());
    }


    @Override
    public void onDrawFrame(GL10 glUnused) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear( GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        panoMediaPlayerWrapper.doTextureUpdate(ggoesFilter.getmSTMatrix());
        filterGroup.onDrawFrame(ggoesFilter.getGloesTexture().getTextureId());
        if (saveImg){
            BitmapUtils.sendImage(width,height,statusHelper.getContext());
            saveImg=false;
        }
        GLES20.glFinish();
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        this.width=width;
        this.height=height;

        GLES20.glViewport(0,0,width,height);
        filterGroup.onFilterChanged(width,height);
    }

    public void saveImg(){
        saveImg=true;
    }

    public GGSphere2DPlugin getSpherePlugin() {
        return spherePlugin;
    }

    public GGFilterGroup getFilterGroup() {
        return filterGroup;
    }
}