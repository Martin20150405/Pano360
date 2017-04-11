package com.martin.ads.vrlib.filters.vr;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.opengl.GLES20;
import android.util.Log;
import android.view.Surface;

import com.martin.ads.vrlib.constant.AdjustingMode;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.object.Plane;
import com.martin.ads.vrlib.programs.GLOESProgram;
import com.martin.ads.vrlib.textures.GLOESTexture;
import com.martin.ads.vrlib.utils.MatrixUtils;
import com.martin.ads.vrlib.utils.TextureUtils;

import java.io.IOException;

/**
 * Created by Ads on 2017/4/11.
 */

public class VideoHotspot extends AbsHotspot  implements
        SurfaceTexture.OnFrameAvailableListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnVideoSizeChangedListener {
    private SurfaceTexture mSurfaceTexture;
    private MediaPlayer mMediaPlayer;

    private GLOESProgram glOESProgram;
    private GLOESTexture glOESTexture;
    private Uri uri;
    //videoTextureMatrix
    private float[] mSTMatrix = new float[16];

    private VideoHotspot(Context context) {
        super(context);
        imagePlane =new Plane(true);
        glOESTexture=new GLOESTexture();
        glOESProgram =new GLOESProgram(context);
    }

    private void setSurface(int mTextureID){
        mSurfaceTexture = new SurfaceTexture(mTextureID);
        mSurfaceTexture.setOnFrameAvailableListener(this);
        Surface surface = new Surface(mSurfaceTexture);
        mMediaPlayer.setSurface(surface);
        surface.release();
    }

    private void initMediaPlayer(){
        if(mMediaPlayer!=null) return;
        mMediaPlayer=new MediaPlayer();
        try{
            mMediaPlayer.setDataSource(context,uri);
        }catch (IOException e){
            e.printStackTrace();
        }
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnVideoSizeChangedListener(this);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void init() {
        super.init();
        glOESProgram.create();
        glOESTexture.loadTexture();
        initMediaPlayer();
        setSurface(glOESTexture.getTextureId());
    }

    @Override
    public void onDrawFrame(int textureId) {
        runPreDrawTasks();
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        glOESProgram.use();
        TextureUtils.bindTextureOES(glOESTexture.getTextureId(), GLES20.GL_TEXTURE1, glOESProgram.getUTextureSamplerHandle(),1);
        imagePlane.uploadTexCoordinateBuffer(glOESProgram.getTextureCoordinateHandle());
        imagePlane.uploadVerticesBuffer(glOESProgram.getPositionHandle());
        updateMatrix();
        GLES20.glUniformMatrix4fv(glOESProgram.getMuSTMatrixHandle(), 1, false, mSTMatrix, 0);
        GLES20.glUniformMatrix4fv(glOESProgram.getMVPMatrixHandle(), 1, false, mMVPMatrix, 0);
        imagePlane.draw();
        GLES20.glDisable(GLES20.GL_BLEND);
    }

    @Override
    public void destroy() {
        glOESProgram.onDestroy();
    }

    public VideoHotspot setPositionOrientation(PositionOrientation positionOrientation) {
        this.positionOrientation = positionOrientation;
        return this;
    }

    public VideoHotspot setAssumedScreenSize(float assumedScreenWidth, float assumedScreenHeight) {
        this.assumedScreenWidth = assumedScreenWidth;
        this.assumedScreenHeight = assumedScreenHeight;
        return this;
    }

    public static VideoHotspot with(Context context){
        return new VideoHotspot(context);
    }

    @Override
    public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                mSurfaceTexture.updateTexImage();
                mSurfaceTexture.getTransformMatrix(mSTMatrix);
            }
        });
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
        MatrixUtils.updateProjection(
                width,
                height,
                (int)(DEFAULT_SCALE_FACTOR*assumedScreenWidth),
                (int)(DEFAULT_SCALE_FACTOR*assumedScreenHeight),
                AdjustingMode.ADJUSTING_MODE_FIT_TO_SCREEN,
                hotspotOrthoProjectionMatrix);
    }

    public VideoHotspot setUri(Uri uri){
        this.uri=uri;
        return this;
    }


    @Override
    public void notifyOnPause() {
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    @Override
    public void notifyOnResume() {
        if(mMediaPlayer!=null && !mMediaPlayer.isPlaying())
            mMediaPlayer.start();
    }

    @Override
    public void notifyOnDestroy() {
        if(mMediaPlayer!=null && mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
            mMediaPlayer.release();
        }
    }
}
