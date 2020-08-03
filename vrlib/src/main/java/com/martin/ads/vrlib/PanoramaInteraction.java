package com.martin.ads.vrlib;

import com.martin.ads.vrlib.filters.vr.AbsHotspot;
import com.martin.ads.vrlib.filters.vr.ImageHotspot;

import java.util.List;

public abstract class PanoramaInteraction {
    private List<AbsHotspot> hotspotList;
    private float searchArea = 10;// Hotspot drawable's max width

    public void setHotspotList(List<AbsHotspot> hotspotList) {
        this.hotspotList = hotspotList;
    }

    abstract public void createHotspot(float yaw,float pinch);

    public abstract void hotspotClicked(AbsHotspot hotspot);

    void checkClickArea(float yaw, float pinch){
        for (AbsHotspot absHotspot : hotspotList) {
            if (absHotspot.getPositionOrientation().aroundPosition(yaw,pinch,searchArea))
            {
                hotspotClicked(absHotspot);
                return;
            }
        }
    }

}
