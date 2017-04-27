package com.martin.ads.vrlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

import com.martin.ads.vrlib.constant.MimeType;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.filters.vr.AbsHotspot;
import com.martin.ads.vrlib.filters.vr.ImageHotspot;
import com.martin.ads.vrlib.filters.vr.VideoHotspot;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.ui.Pano360ConfigBundle;
import com.martin.ads.vrlib.utils.BitmapUtils;
import com.martin.ads.vrlib.utils.StatusHelper;
import com.martin.ads.vrlib.utils.TextImageGenerator;

import java.io.IOException;
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
    private int mimeType;
    private Pano360ConfigBundle configBundle;
    private Bitmap bitmap;

    private PanoViewWrapper(Context context) {
        this.context=context;
    }

    public PanoViewWrapper init(){
        Uri uri=Uri.parse(filePath);
        init(context,uri);
        return this;
    }

    public PanoViewWrapper setConfig(Pano360ConfigBundle configBundle){
        this.configBundle=configBundle;
        filePath=configBundle.getFilePath();
        videoHotspotPath=configBundle.getVideoHotspotPath();
        planeMode=configBundle.isPlaneModeEnabled();
        imageMode=configBundle.isImageModeEnabled();
        mimeType=configBundle.getMimeType();
        //Log.d(TAG, "setConfig: MimeType is "+mimeType);
        return this;
    }

    private void init(Context context, Uri uri){
        glSurfaceView.setEGLContextClientVersion(2);

        statusHelper=new StatusHelper(context);

        if(!imageMode){
            mPnoVideoPlayer = new PanoMediaPlayerWrapper();
            mPnoVideoPlayer.setStatusHelper(statusHelper);
            if((mimeType & MimeType.ASSETS)!=0)
                try {
                    mPnoVideoPlayer.setMediaPlayerFromAssets(context.getAssets().openFd(uri.toString()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else if((mimeType & MimeType.ONLINE)!=0)
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

        if(imageMode){
            if((mimeType & MimeType.ASSETS)!=0)
                bitmap=BitmapUtils.loadBitmapFromAssets(context,filePath);
            else if((mimeType & MimeType.BITMAP)!=0);

            else if((mimeType & MimeType.RAW)!=0)
                bitmap= BitmapUtils.loadBitmapFromRaw(context,
                        Integer.valueOf(uri.getLastPathSegment()));
            else throw new RuntimeException("not implemented yet!");
        }

        mRenderer = PanoRender.newInstance()
                .setStatusHelper(statusHelper)
                .setPanoMediaPlayerWrapper(mPnoVideoPlayer)
                .setImageMode(imageMode)
                .setPlaneMode(planeMode)
                .setBitmap(bitmap)
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

    public PanoViewWrapper setGlSurfaceView(GLSurfaceView glSurfaceView) {
        this.glSurfaceView = glSurfaceView;
        return this;
    }

    public boolean clearHotSpot(){
        if(hotspotList ==null) return false;
        hotspotList.clear();
        return true;
    }

    public PanoViewWrapper setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        return this;
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