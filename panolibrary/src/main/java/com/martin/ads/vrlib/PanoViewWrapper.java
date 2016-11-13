package com.martin.ads.vrlib;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.MotionEvent;

import com.martin.ads.vrlib.constant.PanoFilter;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.utils.StatusHelper;


public class PanoViewWrapper {

    public static String TAG = "PanoViewWrapper";
    private PanoRender mRenderer;
    private PanoMediaPlayerWrapper mPnoVideoPlayer;
    private StatusHelper statusHelper;
    private GLSurfaceView glSurfaceView;
    private TouchHelper touchHelper;

    public PanoViewWrapper(Context context, String videoPath, GLSurfaceView glSurfaceView, PanoFilter panoFilter) {
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

        mPnoVideoPlayer = new PanoMediaPlayerWrapper();
        mPnoVideoPlayer.setStatusHelper(statusHelper);
        if (uri.toString().startsWith("http"))
            mPnoVideoPlayer.openRemoteFile(uri.toString());
        else mPnoVideoPlayer.setMediaPlayerFromUri(uri);
        mPnoVideoPlayer.setRenderCallBack(new PanoViewWrapper.RenderCallBack() {
            @Override
            public void renderImmediately() {
                glSurfaceView.requestRender();
            }
        });

        mRenderer = new PanoRender(statusHelper,panoFilter,mPnoVideoPlayer);

        glSurfaceView.setRenderer(mRenderer);
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

        //使得onTouch能够监听ACTION_DOWN以外的事件
        //也可以写return panoVideoView.handleTouchEvent(event) || true;
        glSurfaceView.setClickable(true);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB){
            glSurfaceView.setPreserveEGLContextOnPause(true);
        }

        statusHelper.setPanoDisPlayMode(PanoMode.DUAL_SCREEN);
        statusHelper.setPanoInteractiveMode(PanoMode.MOTION);

        touchHelper=new TouchHelper(statusHelper,mRenderer);
        statusHelper.setPanoStatus(PanoStatus.IDLE);
        mPnoVideoPlayer.prepare();
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

    public PanoMediaPlayerWrapper getMediaPlayer(){
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

    public TouchHelper getTouchHelper() {
        return touchHelper;
    }
}