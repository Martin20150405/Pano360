package com.martin.ads.vrlib;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.text.method.Touch;
import android.util.Log;
import android.view.MotionEvent;

import com.martin.ads.vrlib.constant.PanoFilter;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.utils.StatusHelper;


public class PanoVideoView {

    public static String TAG = "PanoVideoView";
    private PanoRender mRenderer;
    private PanoVideoPlayer mPnoVideoPlayer;
    private StatusHelper statusHelper;
    private GLSurfaceView glSurfaceView;
    private TouchHelper touchHelper;

    public PanoVideoView(Context context, String videoPath, GLSurfaceView glSurfaceView,PanoFilter panoFilter) {
        this.glSurfaceView=glSurfaceView;
        init(context,videoPath,panoFilter);
    }

    private void init(Context context,String videoPath,PanoFilter panoFilter){
        Uri uri=Uri.parse(videoPath);
        init(context,uri,panoFilter);
    }

    private void init(Context context, Uri uri, PanoFilter panoFilter){
        glSurfaceView.setEGLContextClientVersion(2);

        statusHelper=new StatusHelper(context);

        mRenderer = new PanoRender(statusHelper,panoFilter);

        mPnoVideoPlayer = new PanoVideoPlayer();
        mPnoVideoPlayer.setStatusHelper(statusHelper);
        mPnoVideoPlayer.setMediaPlayerFromUri(uri);
        mPnoVideoPlayer.setRenderCallBack(new PanoVideoView.RenderCallBack() {
            @Override
            public void renderImmediately() {
                glSurfaceView.requestRender();
            }
        });

        mRenderer.setPanoVideoPlayer(mPnoVideoPlayer);
        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        //使得onTouch能够监听ACTION_DOWN以外的事件
        //也可以写return panoVideoView.handleTouchEvent(event) || true;
        glSurfaceView.setClickable(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            glSurfaceView.setPreserveEGLContextOnPause(true);
        }

        statusHelper.setPanoDisPlayMode(PanoMode.DUAL_SCREEN);
        statusHelper.setPanoInteractiveMode(PanoMode.MOTION);

        touchHelper=new TouchHelper(statusHelper,mRenderer);

    }

    public void onPause(){
        glSurfaceView.onPause();
        if(mPnoVideoPlayer!=null && statusHelper.getPanoStatus()== PanoStatus.PLAYING){
            mPnoVideoPlayer.pause();
        }
    }

    public void onResume() {
        glSurfaceView.onResume();
        if (mPnoVideoPlayer!=null){
            if(statusHelper.getPanoStatus()==PanoStatus.PAUSED){
                mPnoVideoPlayer.start();
            }
        }
    }

    public void releaseResources(){
        mPnoVideoPlayer.releaseResource();
        mPnoVideoPlayer=null;
        mRenderer.getSensorEventHandler().releaseResources();
    }

    public PanoVideoPlayer getMediaPlayer(){
        return mPnoVideoPlayer;
    }

    public PanoRender getRenderer(){
        return mRenderer;
    }

    public StatusHelper getStatusHelper(){
        return statusHelper;
    }

    public interface RenderCallBack{
        void renderImmediately();
    }

    public boolean handleTouchEvent(MotionEvent event) {
        return touchHelper.handleTouchEvent(event);
    }

}