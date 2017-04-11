package com.martin.ads.pano360demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.Toast;

import com.github.rubensousa.viewpagercards.CardItem;
import com.github.rubensousa.viewpagercards.CardPagerAdapter;
import com.github.rubensousa.viewpagercards.ShadowTransformer;
import com.martin.ads.vrlib.PanoPlayerActivity;
import com.martin.ads.vrlib.ext.GirlFriendNotFoundException;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;

import java.util.regex.Pattern;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
    private ViewPager mViewPager;

    private CardPagerAdapter mCardAdapter;
    private ShadowTransformer mCardShadowTransformer;

    private CheckBox planeMode;
    private CheckBox windowMode;
    private String filePath="~(～￣▽￣)～";
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.content_text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.content_text_2));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.content_text_3));
        mCardAdapter.addCardItem(new CardItem(R.string.title_4, R.string.content_text_4));
        mCardAdapter.addCardItem(new CardItem(R.string.title_5, R.string.content_text_5));
        mCardAdapter.addCardItem(new CardItem(R.string.title_6, R.string.content_text_6));

        planeMode= (CheckBox) findViewById(R.id.plane_mode);
        windowMode = (CheckBox) findViewById(R.id.window_mode);

        mCardAdapter.setOnClickCallback(new CardPagerAdapter.OnClickCallback() {
            @Override
            public void onClick(int position) {
                boolean imageMode=false;
                Intent intent=new Intent();
                intent.setClass(HomeActivity.this, PanoPlayerActivity.class);
                Log.d(TAG, "onClick: "+position);
                switch (position){
                    case 0:
                        filePath= "android.resource://" + getPackageName() + "/" + R.raw.demo_video;
                        break;
                    case 1:
                        intent.setClass(HomeActivity.this, FilePickerActivity.class);
                        intent.putExtra(FilePickerActivity.ARG_FILTER, Pattern.compile("(.*\\.mp4$)||(.*\\.avi$)||(.*\\.wmv$)"));
                        startActivityForResult(intent, 1);
                        return;
                    case 2:
                        filePath="images/vr_cinema.jpg";
                        intent.putExtra(PanoPlayerActivity.VIDEO_HOTSPOT_PATH, "android.resource://" + getPackageName() + "/" + R.raw.demo_video);
                        imageMode=true;
                        break;
                    case 3:
                        filePath="images/texture_360_n.jpg";
                        imageMode=true;
                        break;
                    case 4:
                        filePath="http://cache.utovr.com/201508270528174780.m3u8";
                        if(windowMode.isChecked()){
                            intent.setClass(HomeActivity.this, PlanePlayerActivity.class);
                        }
                        break;
                    case 5:
                        if(flag) throw new GirlFriendNotFoundException();
                        else {
                            Toast.makeText(HomeActivity.this,"再点会点坏的哦~",Toast.LENGTH_LONG).show();
                            flag=true;
                        }
                        return;
                }
                intent.putExtra(PanoPlayerActivity.FILE_PATH, filePath);
                intent.putExtra(PanoPlayerActivity.IMAGE_MODE, imageMode);
                intent.putExtra(PanoPlayerActivity.PLANE_MODE, planeMode.isChecked());
                intent.putExtra(PanoPlayerActivity.WINDOW_MODE, windowMode.isChecked());
                intent.putExtra("removeHotspot", false);
                startActivity(intent);
            }
        });
        mCardShadowTransformer = new ShadowTransformer(mViewPager, mCardAdapter);

        mViewPager.setAdapter(mCardAdapter);
        mViewPager.setPageTransformer(false, mCardShadowTransformer);

        mViewPager.setOffscreenPageLimit(3);

        mCardShadowTransformer.enableScaling(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            String filePath = data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH);
            Intent intent=new Intent(HomeActivity.this,PanoPlayerActivity.class);
            //Intent intent=new Intent(HomeActivity.this,DemoWithGLSurfaceView.class);
            intent.putExtra(PanoPlayerActivity.FILE_PATH, filePath);
            intent.putExtra(PanoPlayerActivity.IMAGE_MODE, false);
            intent.putExtra(PanoPlayerActivity.PLANE_MODE, planeMode.isChecked());
            intent.putExtra("removeHotspot", true);
            startActivity(intent);
        }
    }

}
