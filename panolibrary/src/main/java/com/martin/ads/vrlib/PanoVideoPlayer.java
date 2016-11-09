package com.martin.ads.vrlib;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.Surface;


import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.utils.StatusHelper;

import java.io.IOException;

/**
 * Project: Pano360
 * Package: com.martin.ads.pano360
 * Created by Ads on 2016/5/2.
 */
public class PanoVideoPlayer implements SurfaceTexture.OnFrameAvailableListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnVideoSizeChangedListener {
    public static String TAG = "PanoVideoPlayer";

    private StatusHelper statusHelper;
    private PanoVideoView.RenderCallBack renderCallBack;

    private SurfaceTexture mSurfaceTexture;

    private MediaPlayer mMediaPlayer;

    public void setRenderCallBack(PanoVideoView.RenderCallBack renderCallBack){
        this.renderCallBack=renderCallBack;
    }
    public void setSurface(int mTextureID){
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        Surface surface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    public void doTextureUpdate(float[] mSTMatrix){
        mSurfaceTexture.updateTexImage();
        mSurfaceTexture.getTransformMatrix(mSTMatrix);
    }

    public void setMediaPlayerFromUri(Uri uri){
        mMediaPlayer=new MediaPlayer();
        try{
            mMediaPlayer.setDataSource(statusHelper.getContext(),uri);
        }catch (IOException e){
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
    }

    public void setStatusHelper(StatusHelper statusHelper){
        this.statusHelper=statusHelper;
    }

    public void prepare(){
        try {
            mMediaPlayer.prepare();
        } catch (IOException t) {
            Log.e(TAG, "media player prepare failed");
        }
    }

    public void start(){
        mMediaPlayer.start();
        statusHelper.setPanoStatus(PanoStatus.PLAYING);
    }

    public void pause(){
        mMediaPlayer.pause();
        statusHelper.setPanoStatus(PanoStatus.PAUSED);
    }

    public void pauseByUser(){
        mMediaPlayer.pause();
        statusHelper.setPanoStatus(PanoStatus.PAUSED_BY_USER);
    }

    public void stop(){
        mMediaPlayer.stop();
        statusHelper.setPanoStatus(PanoStatus.STOPPED);
    }

    public void releaseResource(){
        if(mMediaPlayer!=null){
            mMediaPlayer.setSurface(null);
            if (mSurfaceTexture!=null) mSurfaceTexture=null;
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        renderCallBack.renderImmediately();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

    }

}
