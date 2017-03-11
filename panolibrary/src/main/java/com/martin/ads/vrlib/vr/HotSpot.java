package com.martin.ads.vrlib.vr;

import android.content.Context;

import com.martin.ads.vrlib.object.Plain;
import com.martin.ads.vrlib.programs.GLPassThroughProgram;

/**
 * Created by Ads on 2017/3/11.
 */

public class HotSpot{
    private GLPassThroughProgram glPassThroughProgram;
    private Plain plain;
    private Context context;

    private HotSpot(Context context) {
        this.context = context;
    }

    public HotSpot with(Context context){
        return new HotSpot(context);
    }
}
