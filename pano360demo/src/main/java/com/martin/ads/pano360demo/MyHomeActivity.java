package com.martin.ads.pano360demo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.martin.ads.vrlib.PanoViewWrapper;
import com.martin.ads.vrlib.PanoramaInteraction;
import com.martin.ads.vrlib.constant.MimeType;
import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.filters.vr.ImageHotspot;
import com.martin.ads.vrlib.math.PositionOrientation;
import com.martin.ads.vrlib.ui.Pano360ConfigBundle;
import com.martin.ads.vrlib.ui.PanoPlayerActivity;
import com.martin.ads.vrlib.utils.BitmapUtils;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MyHomeActivity extends AppCompatActivity {
    private static final String TAG = "MyHomeActivity";

    private PanoViewWrapper panoViewWrapper;


    private boolean USE_DEFAULT_ACTIVITY =true;

    private String filePath="~(～￣▽￣)～";

    private int mimeType;


    @NeedsPermission({
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET
    })
    void init(){
        //fake method to require permissions
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        if(this.getSupportActionBar()!=null)this.getSupportActionBar().hide();

        setContentView(R.layout.activity_pano);
        MyHomeActivityPermissionsDispatcher.initWithPermissionCheck(this);


        //filePath= "android.resource://" + getPackageName() + "/" + R.raw.vr_cinema;
        //mimeType= MimeType.RAW | MimeType.PICTURE;

        //mimeType= MimeType.BITMAP | MimeType.PICTURE;

        filePath="images/360_pano.jpg";
        mimeType= MimeType.ASSETS | MimeType.PICTURE;
        USE_DEFAULT_ACTIVITY=false;

        launch();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);
    }

    private void launch(){

        Pano360ConfigBundle configBundle=Pano360ConfigBundle
                .newInstance()
                .setFilePath(filePath)
                .setMimeType(mimeType)
                .setPlaneModeEnabled(false)
                //set it false to see default hotspot

                .setRemoveHotspot(false);

        GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);

        panoViewWrapper =PanoViewWrapper.with(this)
                .setConfig(configBundle)
                .setGlSurfaceView(glSurfaceView)
                .setPanoramaInteraction(new PanoramaInteraction() {

                    @Override
                    public void createHotspot(float yaw, float pinch) {

                    }

                    @Override
                    public void hotspotClicked(ImageHotspot hotspot) {
                        Toast.makeText(MyHomeActivity.this, "Click grabbed", Toast.LENGTH_SHORT).show();
                    }
                })
                .init();

        panoViewWrapper.getStatusHelper().setPanoDisPlayMode(PanoMode.SINGLE_SCREEN);
        panoViewWrapper.getStatusHelper().setPanoInteractiveMode(PanoMode.TOUCH);
        panoViewWrapper.addHotspot(ImageHotspot.with(this)
                .setPositionOrientation(PositionOrientation.newInstance().fromTriangularSystem(180,10,30))
                .setBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.img_58c6f9963d50e)));

        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Logger.logTouchEvent(v,event);
                return panoViewWrapper.handleTouchEvent(event);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            mimeType= MimeType.LOCAL_FILE | MimeType.VIDEO;
            launch();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MyHomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
