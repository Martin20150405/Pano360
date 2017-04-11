package com.martin.ads.vrlib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.filters.vr.AbsHotspot;
import com.martin.ads.vrlib.filters.vr.ImageHotspot;
import com.martin.ads.vrlib.filters.vr.VideoHotspot;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.utils.StatusHelper;
import com.martin.ads.vrlib.utils.TextImageGenerator;

import java.util.ArrayList;
import java.util.List;


public class PanoViewWrapper {

    public static String TAG = "PanoViewWrapper";
    private PanoRender mRenderer;
    private PanoMediaPlayerWrapper mPnoVideoPlayer;
    private StatusHelper statusHelper;
    private GLSurfaceView glSurfaceView;
    private TouchHelper touchHelper;

    private boolean imageMode;
    private String videoHotspotPath;
    private boolean planeMode;

    private Context context;
    private String filePath;
    private List<AbsHotspot> hotspotList;

    private PanoViewWrapper(Context context) {
        this.context=context;
    }

    public PanoViewWrapper init(){
        Uri uri=Uri.parse(filePath);
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
                .setFilePath(uri.toString())
                .setFilterMode(PanoRender.FILTER_MODE_AFTER_PROJECTION)
                .init();

        hotspotList =new ArrayList<>();

        if(videoHotspotPath!=null && !videoHotspotPath.isEmpty()){
            hotspotList.add(VideoHotspot.with(statusHelper.getContext())
                    .setPositionOrientation(
                            PositionOrientation.newInstance()
                                    .setX(-7.8f).setY(1.2f).setAngleY(-90)
                    )
                    .setUri(Uri.parse(videoHotspotPath))
                    .setAssumedScreenSize(2.0f,1.0f)
            );
        }else{
            hotspotList.add(ImageHotspot.with(statusHelper.getContext())
                    .setPositionOrientation(
                            PositionOrientation.newInstance()
                                    .setY(15).setAngleX(90).setAngleY(-90)
                    )
                    .setBitmap( TextImageGenerator.newInstance()
                            .setPadding(25)
                            .setTextColor(Color.parseColor("#FFCE54"))
                            .setBackgroundColor(Color.parseColor("#22000000"))
                            .setTypeface(Typeface.createFromAsset(
                                    statusHelper.getContext().getAssets(),
                                    "fonts/font_26.ttf")
                            )
                            .setTextSize(55)
                            .addTextToImage("I'm a text hotspot~")
                    )
            );
        }

        hotspotList.add(ImageHotspot.with(statusHelper.getContext())
                .setPositionOrientation(
                        PositionOrientation.newInstance()
                                .setY(-15).setAngleX(-90).setAngleY(-90)
                )
                .setImagePath("imgs/hotspot_logo.png")
        );

        mRenderer.getSpherePlugin().setHotspotList(hotspotList);

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
        for(AbsHotspot hotspot:hotspotList){
            hotspot.notifyOnPause();
        }
    }

    public void onResume() {
        glSurfaceView.onResume();
        if (mPnoVideoPlayer!=null){
            if(statusHelper.getPanoStatus()==PanoStatus.PAUSED){
                mPnoVideoPlayer.start();
            }
        }
        for(AbsHotspot hotspot:hotspotList){
            hotspot.notifyOnResume();
        }
    }

    public void releaseResources(){
        for(AbsHotspot hotspot:hotspotList){
            hotspot.notifyOnDestroy();
        }
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

    public PanoViewWrapper setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public PanoViewWrapper setVideoHotspotPath(String videoHotspotPath) {
        this.videoHotspotPath = videoHotspotPath;
        return this;
    }

    public boolean clearHotSpot(){
        if(hotspotList ==null) return false;
        hotspotList.clear();
        return true;
    }

    //TODO:add real interface to control hot spot
    // & head pose control
    public PanoViewWrapper removeDefaultHotSpot(){
        clearHotSpot();
        return this;
    }

    public static PanoViewWrapper with(Context context){
        return new PanoViewWrapper(context);
    }
}