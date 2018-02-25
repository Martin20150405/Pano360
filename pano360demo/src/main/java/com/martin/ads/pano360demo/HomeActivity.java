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
public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private boolean USE_DEFAULT_ACTIVITY =true;
    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private CheckBox planeMode;
    private boolean flag;

    private String filePath="~(～￣▽￣)～";
    private String videoHotspotPath;
    private boolean planeModeEnabled;

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
        setContentView(R.layout.activity_main);
        HomeActivityPermissionsDispatcher.initWithPermissionCheck(this);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.content_text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.content_text_2));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.content_text_3));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.content_text_4));
        mCardAdapter.addCardItem(new CardItem(R.string.title_5, R.string.content_text_5));
        mCardAdapter.addCardItem(new CardItem(R.string.title_6, R.string.content_text_6));

        planeMode= (CheckBox) findViewById(R.id.plane_mode);

        mCardAdapter.setOnClickCallback(new CardPagerAdapter.OnClickCallback() {
            @Override
            public void onClick(int position) {
                videoHotspotPath=null;
                switch (position){
                    case 0:
                        //filePath= "gz256.mp4";
                        //mimeType= MimeType.ASSETS | MimeType.VIDEO;
                        filePath= "android.resource://" + getPackageName() + "/" + R.raw.demo_video;
                        mimeType= MimeType.RAW | MimeType.VIDEO;
                        break;
                    case 1:
                        Intent intent=new Intent(HomeActivity.this, FilePickerActivity.class);
                        intent.putExtra(FilePickerActivity.ARG_FILTER, Pattern.compile("(.*\\.mp4$)||(.*\\.avi$)||(.*\\.wmv$)"));
                        startActivityForResult(intent, 1);
                        return;
                    case 2:
                        filePath="images/vr_cinema.jpg";
                        videoHotspotPath="android.resource://" + getPackageName() + "/" + R.raw.demo_video;
                        mimeType= MimeType.ASSETS | MimeType.PICTURE;
                        break;
                    case 3:
                        //filePath= "android.resource://" + getPackageName() + "/" + R.raw.vr_cinema;
                        //mimeType= MimeType.RAW | MimeType.PICTURE;

                        //mimeType= MimeType.BITMAP | MimeType.PICTURE;

                        filePath="images/texture_360_n.jpg";
                        mimeType= MimeType.ASSETS | MimeType.PICTURE;
                        break;
                    case 4:
                        filePath="http://cache.utovr.com/201508270528174780.m3u8";
                        mimeType= MimeType.ONLINE | MimeType.VIDEO;
                        break;
                    case 5:
                        if(flag) throw new GirlFriendNotFoundException();
                        else {
                            Toast.makeText(HomeActivity.this,"再点会点坏的哦~",Toast.LENGTH_LONG).show();
                            flag=true;
                        }
                        return;
                }
                planeModeEnabled=planeMode.isChecked();
                start();
            }
        });
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);

        mViewPager.setOffscreenPageLimit(3);

        mCardShadowTransformer.enableScaling(true);
    }

    private void start(){
        Pano360ConfigBundle configBundle=Pano360ConfigBundle
                .newInstance()
                .setFilePath(filePath)
                .setMimeType(mimeType)
                .setPlaneModeEnabled(planeModeEnabled)
                //set it false to see default hotspot
                .setRemoveHotspot(true)
                .setVideoHotspotPath(videoHotspotPath);

        if((mimeType & MimeType.BITMAP)!=0){
            //add your own picture here
            // this interface may be removed in future version.
            configBundle.startEmbeddedActivityWithSpecifiedBitmap(
                    this,BitmapUtils.loadBitmapFromRaw(this,R.mipmap.ic_launcher));
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
            planeModeEnabled=planeMode.isChecked();
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        HomeActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
}
