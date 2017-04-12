package com.martin.ads.vrlib.ui;

import android.content.Context;
import android.content.Intent;

import java.io.Serializable;

/**
 * Created by Ads on 2017/4/12.
 */

public class Pano360ConfigBundle implements Serializable{
    private String filePath;
    private String videoHotspotPath;
    private boolean imageModeEnabled;
    private boolean planeModeEnabled;
    private boolean windowModeEnabled;
    private boolean removeHotspot;

    public Pano360ConfigBundle() {
        filePath=null;
        videoHotspotPath=null;
        imageModeEnabled=false;
        planeModeEnabled=false;
        windowModeEnabled=false;
        removeHotspot=false;
    }

    public static Pano360ConfigBundle newInstance(){
        return new Pano360ConfigBundle();
    }

    public void startEmbeddedActivity(Context context){
        Intent intent=new Intent(context,PanoPlayerActivity.class);
        intent.putExtra(PanoPlayerActivity.CONFIG_BUNDLE,this);
        context.startActivity(intent);
    }

    public String getFilePath() {
        return filePath;
    }

    public Pano360ConfigBundle setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public String getVideoHotspotPath() {
        return videoHotspotPath;
    }

    public Pano360ConfigBundle setVideoHotspotPath(String videoHotspotPath) {
        this.videoHotspotPath = videoHotspotPath;
        return this;
    }

    public boolean isImageModeEnabled() {
        return imageModeEnabled;
    }

    public Pano360ConfigBundle setImageModeEnabled(boolean imageModeEnabled) {
        this.imageModeEnabled = imageModeEnabled;
        return this;
    }

    public boolean isPlaneModeEnabled() {
        return planeModeEnabled;
    }

    public Pano360ConfigBundle setPlaneModeEnabled(boolean planeModeEnabled) {
        this.planeModeEnabled = planeModeEnabled;
        return this;
    }

    public boolean isWindowModeEnabled() {
        return windowModeEnabled;
    }

    public boolean isRemoveHotspot() {
        return removeHotspot;
    }

    public Pano360ConfigBundle setRemoveHotspot(boolean removeHotspot) {
        this.removeHotspot = removeHotspot;
        return this;
    }
}
