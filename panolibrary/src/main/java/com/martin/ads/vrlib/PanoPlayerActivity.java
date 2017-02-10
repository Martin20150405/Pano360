package com.martin.ads.vrlib;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.constant.PanoStatus;
import com.martin.ads.vrlib.utils.UIUtils;

/**
 * Created by Ads on 2016/11/10.
 * UI is modified from UtoVR demo
 */
//FIXME:looks so lame.
public class PanoPlayerActivity extends Activity {
    private PanoViewWrapper panoViewWrapper;
    private PanoUIController panoUIController;
    private ImageView bufferAnim;

    private boolean imageMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.player_activity_layout);

        init();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void init(){
        bufferAnim= (ImageView) findViewById(R.id.activity_imgBuffer);
        String videoPath=getIntent().getStringExtra("videoPath");
        imageMode=getIntent().getBooleanExtra("imageMode",false);
        boolean planeMode=getIntent().getBooleanExtra("planeMode",false);
        if(!imageMode)
            UIUtils.setBufferVisibility(bufferAnim,true);
        else UIUtils.setBufferVisibility(bufferAnim,false);
        panoUIController=new PanoUIController(
                (RelativeLayout)findViewById(R.id.player_toolbar_control),
                (RelativeLayout)findViewById(R.id.player_toolbar_progress),
                this,imageMode);
        TextView title= (TextView) findViewById(R.id.video_title);
        title.setText(Uri.parse(videoPath).getLastPathSegment());
        GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);
        panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView,imageMode,planeMode);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Logger.logTouchEvent(v,event);
                panoUIController.startHideControllerTimer();
                return panoViewWrapper.handleTouchEvent(event);
            }
        });
        panoUIController.setAutoHideController(true);
        panoUIController.setUiCallback(new PanoUIController.UICallback() {
            @Override
            public void requestScreenshot() {
                panoViewWrapper.getTouchHelper().shotScreen();
            }

            @Override
            public void requestFinish() {
                finish();
            }
            @Override
            public void changeDisPlayMode() {
                if (panoViewWrapper.getStatusHelper().getPanoDisPlayMode()==PanoMode.DUAL_SCREEN)
                    panoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.SINGLE_SCREEN);
                else panoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.DUAL_SCREEN);
            }

            @Override
            public void changeInteractiveMode() {
                if (panoViewWrapper.getStatusHelper().getPanoInteractiveMode()==PanoMode.MOTION)
                    panoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.TOUCH);
                else panoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.MOTION);
            }

            @Override
            public void changePlayingStatus() {
                if (panoViewWrapper.getStatusHelper().getPanoStatus()== PanoStatus.PLAYING){
                    panoViewWrapper.getMediaPlayer().pauseByUser();
                }else if (panoViewWrapper.getStatusHelper().getPanoStatus()== PanoStatus.PAUSED_BY_USER){
                    panoViewWrapper.getMediaPlayer().start();
                }
            }

            @Override
            public void playerSeekTo(int pos) {
                panoViewWrapper.getMediaPlayer().seekTo(pos);
            }

            @Override
            public int getPlayerDuration() {
                return panoViewWrapper.getMediaPlayer().getDuration();
            }

            @Override
            public int getPlayerCurrentPosition() {
                return panoViewWrapper.getMediaPlayer().getCurrentPosition();
            }
        });
        panoViewWrapper.getTouchHelper().setPanoUIController(panoUIController);

        if(!imageMode){
            panoViewWrapper.getMediaPlayer().setPlayerCallback(new PanoMediaPlayerWrapper.PlayerCallback() {
                @Override
                public void updateProgress() {
                    panoUIController.updateProgress();
                }

                @Override
                public void updateInfo() {
                    UIUtils.setBufferVisibility(bufferAnim,false);
                    panoUIController.startHideControllerTimer();
                    panoUIController.setInfo();
                }

                @Override
                public void requestFinish() {
                    finish();
                }
            });
        }else panoUIController.startHideControllerTimer();
    }

    @Override
    protected void onPause(){
        super.onPause();
        panoViewWrapper.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        panoViewWrapper.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        panoViewWrapper.releaseResources();
    }


}
