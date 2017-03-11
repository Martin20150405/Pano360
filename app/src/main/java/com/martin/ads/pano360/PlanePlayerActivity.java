package com.martin.ads.pano360;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.martin.ads.pano360.util.DimenUtils;
import com.martin.ads.vrlib.PanoMediaPlayerWrapper;
import com.martin.ads.vrlib.PanoPlayerActivity;
import com.martin.ads.vrlib.PanoUIController;
import com.martin.ads.vrlib.PanoViewWrapper;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.utils.UIUtils;

/**
 * Created by zhouyou on 2017/3/2.
 * Class desc:
 */
public class PlanePlayerActivity extends AppCompatActivity implements View.OnClickListener {

    private PanoUIController mPanoUIController;
    private PanoViewWrapper mPanoViewWrapper;
    private RelativeLayout mRltContent;
    private ImageView mImgBufferAnim;

    /** 是否是全屏播放 */
    private boolean mIsFullScreen = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);

        setContentView(R.layout.activity_plane_player);

        getContentResolver().registerContentObserver(Settings.System.getUriFor(
                Settings.System.ACCELEROMETER_ROTATION), true, rotationObserver);

        init();
    }

    private ContentObserver rotationObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (selfChange) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            } else {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_USER);
            }
        }
    };

    private void init(){
        String videoPath = getIntent().getStringExtra(PanoPlayerActivity.VIDEO_PATH);
        boolean imageMode = getIntent().getBooleanExtra(PanoPlayerActivity.IMAGE_MODE, false);
        boolean planeMode = getIntent().getBooleanExtra(PanoPlayerActivity.PLANE_MODE, false);
        boolean windowMode = getIntent().getBooleanExtra(PanoPlayerActivity.WINDOW_MODE, false);

        ImageView imgFullScreen = (ImageView) findViewById(com.example.panolibrary.R.id.img_full_screen);
        imgFullScreen.setVisibility(windowMode ? View.VISIBLE : View.GONE);
        imgFullScreen.setOnClickListener(this);
        mRltContent = (RelativeLayout) findViewById(R.id.rlt_content);

        mImgBufferAnim = (ImageView) findViewById(com.example.panolibrary.R.id.activity_imgBuffer);
        UIUtils.setBufferVisibility(mImgBufferAnim, !imageMode);
        mPanoUIController = new PanoUIController(
                (RelativeLayout)findViewById(com.example.panolibrary.R.id.player_toolbar_control),
                (RelativeLayout)findViewById(com.example.panolibrary.R.id.player_toolbar_progress),
                this, imageMode);

        TextView title = (TextView) findViewById(com.example.panolibrary.R.id.video_title);
        title.setText(Uri.parse(videoPath).getLastPathSegment());

        GLSurfaceView glSurfaceView = (GLSurfaceView) findViewById(com.example.panolibrary.R.id.surface_view);
        mPanoViewWrapper = PanoViewWrapper.with(this)
                .setVideoPath(videoPath)
                .setGlSurfaceView(glSurfaceView)
                .setImageMode(imageMode)
                .setPlaneMode(planeMode)
                .init();
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mPanoUIController.startHideControllerTimer();
                return mPanoViewWrapper.handleTouchEvent(event);
            }
        });
        mPanoUIController.setAutoHideController(true);
        mPanoUIController.setUiCallback(new PanoUIController.UICallback() {
            @Override
            public void requestScreenshot() {
                mPanoViewWrapper.getTouchHelper().shotScreen();
            }

            @Override
            public void requestFinish() {
                if (mIsFullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else{
                    finish();
                }
            }

            @Override
            public void changeDisPlayMode() {
                if (mPanoViewWrapper.getStatusHelper().getPanoDisPlayMode()== PanoMode.DUAL_SCREEN)
                    mPanoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.SINGLE_SCREEN);
                else mPanoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.DUAL_SCREEN);
            }

            @Override
            public void changeInteractiveMode() {
                if (mPanoViewWrapper.getStatusHelper().getPanoInteractiveMode()== PanoMode.MOTION)
                    mPanoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.TOUCH);
                else mPanoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.MOTION);
            }

            @Override
            public void changePlayingStatus() {
                if (mPanoViewWrapper.getStatusHelper().getPanoStatus()== PanoStatus.PLAYING){
                    mPanoViewWrapper.getMediaPlayer().pauseByUser();
                }else if (mPanoViewWrapper.getStatusHelper().getPanoStatus()== PanoStatus.PAUSED_BY_USER){
                    mPanoViewWrapper.getMediaPlayer().start();
                }
            }

            @Override
            public void playerSeekTo(int pos) {
                mPanoViewWrapper.getMediaPlayer().seekTo(pos);
            }

            @Override
            public int getPlayerDuration() {
                return mPanoViewWrapper.getMediaPlayer().getDuration();
            }

            @Override
            public int getPlayerCurrentPosition() {
                return mPanoViewWrapper.getMediaPlayer().getCurrentPosition();
            }
        });
        mPanoViewWrapper.getTouchHelper().setPanoUIController(mPanoUIController);

        if(!imageMode){
            mPanoViewWrapper.getMediaPlayer().setPlayerCallback(new PanoMediaPlayerWrapper.PlayerCallback() {
                @Override
                public void updateProgress() {
                    mPanoUIController.updateProgress();
                }

                @Override
                public void updateInfo() {
                    UIUtils.setBufferVisibility(mImgBufferAnim,false);
                    mPanoUIController.startHideControllerTimer();
                    mPanoUIController.setInfo();
                }

                @Override
                public void requestFinish() {
                    if(mIsFullScreen){
                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    }
                }
            });
        }else mPanoUIController.startHideControllerTimer();
    }

    @Override
    protected void onPause(){
        super.onPause();
        mPanoViewWrapper.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mPanoViewWrapper.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mPanoViewWrapper.releaseResources();
        getContentResolver().unregisterContentObserver(rotationObserver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.img_full_screen:
                if (!mIsFullScreen) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                break;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            setFullScreenPlay();
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            setWindowScreenPlay();
        }
    }

    private void setFullScreenPlay(){
        mIsFullScreen = true;

        // 设置全屏即隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 设置视频充满全屏
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRltContent.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mRltContent.setLayoutParams(layoutParams);
    }

    private void setWindowScreenPlay(){
        mIsFullScreen = false;

        // 恢复状态栏
        WindowManager.LayoutParams attrs = getWindow().getAttributes();
        attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setAttributes(attrs);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        // 设置显示视频固定大小
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mRltContent.getLayoutParams();
        layoutParams.height = DimenUtils.dp2px(this, 230);
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        mRltContent.setLayoutParams(layoutParams);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            if (mIsFullScreen) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
