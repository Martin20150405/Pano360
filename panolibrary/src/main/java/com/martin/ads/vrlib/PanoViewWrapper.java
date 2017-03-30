package com.martin.ads.vrlib;

import android.content.Context;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.MotionEvent;

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

    private boolean imageMode;
    private boolean planeMode;

    private Context context;
    private String videoPath;

    private PanoViewWrapper(Context context) {
        this.context=context;
    }

    public PanoViewWrapper init(){
        Uri uri=Uri.parse(videoPath);
        init(context,uri);
        return this;
    }

    private void init(Context context, Uri uri){
        glSurfaceView.setEGLContextClientVersion(2);

        statusHelper=new StatusHelper(context);

        if(!imageMode){
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
            statusHelper.setPanoStatus(PanoStatus.IDLE);
            mPnoVideoPlayer.prepare();
        }

        mRenderer = PanoRender.newInstance()
                .setStatusHelper(statusHelper)
                .setPanoMediaPlayerWrapper(mPnoVideoPlayer)
                .setImageMode(imageMode)
                .setPlaneMode(planeMode)
                .setFilterMode(PanoRender.FILTER_MODE_AFTER_PROJECTION)
                .init();

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
        if(mPnoVideoPlayer!=null){
            mPnoVideoPlayer.releaseResource();
            mPnoVideoPlayer=null;
        }
        if(mRenderer.getSpherePlugin()!=null){
            mRenderer.getSpherePlugin().getSensorEventHandler().releaseResources();
        }
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

    public PanoViewWrapper setImageMode(boolean imageMode) {
        this.imageMode = imageMode;
        return this;
    }

    public PanoViewWrapper setPlaneMode(boolean planeMode) {
        this.planeMode = planeMode;
        return this;
    }

    public PanoViewWrapper setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
        return this;
    }

    public PanoViewWrapper setVideoPath(String videoPath) {
        this.videoPath = videoPath;
        return this;
    }

    public static PanoViewWrapper with(Context context){
        return new PanoViewWrapper(context);
    }
}