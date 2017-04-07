package com.martin.ads.vrlib.filters.advanced.mx;

import android.content.Context;

import com.martin.ads.vrlib.filters.base.SimpleFragmentShaderFilter;


/**
 * Created by Ads on 2017/1/31.
 * GreenHouseFilter (温室)
 */

public class GreenHouseFilter extends SimpleFragmentShaderFilter {
    public GreenHouseFilter(Context context) {
        super(context, "filter/fsh/mx_green_house.glsl");
    }
}
