package com.martin.ads.pano360demo;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.rubensousa.viewpagercards.CardItem;
import com.github.rubensousa.viewpagercards.CardPagerAdapter;
import com.github.rubensousa.viewpagercards.ShadowTransformer;
import com.martin.ads.vrlib.constant.MimeType;
import com.martin.ads.vrlib.ext.GirlFriendNotFoundException;
import com.martin.ads.vrlib.ui.Pano360ConfigBundle;
import com.martin.ads.vrlib.ui.PanoPlayerActivity;
import com.martin.ads.vrlib.utils.BitmapUtils;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MyHomeActivity extends AppCompatActivity {
    private static final String TAG = "MyHomeActivity";

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
        setContentView(R.layout.activity_pano);
        MyHomeActivityPermissionsDispatcher.initWithPermissionCheck(this);


        //filePath= "android.resource://" + getPackageName() + "/" + R.raw.vr_cinema;
        //mimeType= MimeType.RAW | MimeType.PICTURE;

        //mimeType= MimeType.BITMAP | MimeType.PICTURE;

        filePath="images/360_pano.jpg";
        mimeType= MimeType.ASSETS | MimeType.PICTURE;
        USE_DEFAULT_ACTIVITY=false;

        start();
    }

    private void start(){
        Pano360ConfigBundle configBundle=Pano360ConfigBundle
                .newInstance()
                .setFilePath(filePath)
                .setMimeType(mimeType)
                .setPlaneModeEnabled(false)
                //set it false to see default hotspot
                .setRemoveHotspot(false);

        if((mimeType & MimeType.BITMAP)!=0){
            //add your own picture here
            // this interface may be removed in future version.
            configBundle.startEmbeddedActivityWithSpecifiedBitmap(this,BitmapUtils.loadBitmapFromRaw(this,R.mipmap.ic_launcher));
            return;
        }

        if(USE_DEFAULT_ACTIVITY)
            configBundle.startEmbeddedActivity(this);
        else {
            Intent intent=new Intent(this,DemoWithGLSurfaceView.class);
            intent.putExtra(PanoPlayerActivity.CONFIG_BUNDLE,configBundle);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            mimeType= MimeType.LOCAL_FILE | MimeType.VIDEO;
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MyHomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
