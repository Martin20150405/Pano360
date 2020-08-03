package com.martin.ads.pano360demo;

import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.martin.ads.vrlib.PanoramaInteraction;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.filters.vr.AbsHotspot;
import com.martin.ads.vrlib.filters.vr.ImageHotspot;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.ui.Pano360ConfigBundle;
import com.martin.ads.vrlib.ui.PanoPlayerActivity;
import com.martin.ads.vrlib.PanoViewWrapper;

import java.util.List;

/**
 * Created by Ads on 2016/6/25.
 */
public class DemoWithGLSurfaceView extends AppCompatActivity {
    private PanoViewWrapper panoViewWrapper;

    public static String TAG = "DemoWithGLSurfaceView";
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
        Pano360ConfigBundle configBundle= (Pano360ConfigBundle) getIntent().getSerializableExtra(PanoPlayerActivity.CONFIG_BUNDLE);
        if(configBundle==null){
            configBundle=Pano360ConfigBundle.newInstance();
        }
        configBundle.setRemoveHotspot(true);
        GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);
        panoViewWrapper =PanoViewWrapper.with(this)
                .setConfig(configBundle)
                .setGlSurfaceView(glSurfaceView)
                .setPanoramaInteraction(new PanoramaInteraction() {

                    @Override
                    public void createHotspot(float yaw, float pinch) {

                    }

                    @Override
                    public void hotspotClicked(AbsHotspot hotspot) {
                        Toast.makeText(DemoWithGLSurfaceView.this, "Click grabbed", Toast.LENGTH_SHORT).show();
                    }
                })
                .init();
        panoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.SINGLE_SCREEN);
        panoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.TOUCH);
        panoViewWrapper.addHotspot(ImageHotspot.with(this)
                .setPositionOrientation(

                        PositionOrientation.newInstance().fromTriangularSystem(180,-15,30)

                )
                .setBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.home_logo)));

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Logger.logTouchEvent(v,event);
                return panoViewWrapper.handleTouchEvent(event);
            }
        });
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
