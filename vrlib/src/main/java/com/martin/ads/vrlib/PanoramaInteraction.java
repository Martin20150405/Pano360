package com.martin.ads.vrlib;

import com.martin.ads.vrlib.filters.vr.AbsHotspot;
import com.martin.ads.vrlib.filters.vr.ImageHotspot;

import java.util.List;

public abstract class PanoramaInteraction {
    private List<AbsHotspot> hotspotList;

    public void setHotspotList(List<AbsHotspot> hotspotList) {
        this.hotspotList = hotspotList;
    }

    abstract public void createHotspot(float yaw,float pinch);

    public abstract void hotspotClicked(ImageHotspot hotspot);

    void checkClickArea(float yaw, float pinch){
        for (AbsHotspot abs : hotspotList) {
            ImageHotspot hotspot= (ImageHotspot) abs;

            if (hotspot.getPositionOrientation().aroundPosition(yaw,pinch)) {
                hotspotClicked(hotspot);
                return;
            }
        }
    }

}
