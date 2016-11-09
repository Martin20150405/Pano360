package com.martin.ads.pano360;

import android.content.pm.ActivityInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;

import com.martin.ads.vrlib.PanoVideoView;
import com.martin.ads.vrlib.constant.PanoFilter;

/**
 * Created by Ads on 2016/6/25.
 */
public class PlayVideoActivity extends AppCompatActivity {
    private PanoVideoView panoVideoView;

    public static String TAG = "PlayVideoActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getSupportActionBar().hide();
        setContentView(R.layout.player_layout);

        init();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);
    }

    private void init(){


        String videoPath=getIntent().getStringExtra("videoPath");
        GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);
        if (getIntent().getBooleanExtra("checked",false))
            panoVideoView =new PanoVideoView(this,videoPath, glSurfaceView, PanoFilter.GRAY_SCALE);
        else panoVideoView =new PanoVideoView(this,videoPath, glSurfaceView, PanoFilter.NORMAL);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return panoVideoView.handleTouchEvent(event);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        panoVideoView.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        panoVideoView.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        panoVideoView.releaseResources();
    }

}
