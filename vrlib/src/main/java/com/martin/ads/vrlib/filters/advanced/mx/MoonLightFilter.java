package com.martin.ads.vrlib.filters.advanced.mx;

import android.content.Context;

import com.martin.ads.vrlib.filters.base.SimpleFragmentShaderFilter;

/**
 * Created by Ads on 2017/1/31.
 * MoonLightFilter (月光)
 */

public class MoonLightFilter extends SimpleFragmentShaderFilter {
    public MoonLightFilter(Context context) {
        super(context, "filter/fsh/mx_moon_light.glsl");
    }
}
