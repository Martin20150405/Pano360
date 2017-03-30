package com.martin.ads.vrlib.filters.advanced.mx;

import android.content.Context;

import com.martin.ads.vrlib.filters.base.SimpleFragmentShaderFilter;


/**
 * Created by Ads on 2017/1/31.
 * VignetteFilter (炫影)
 */

public class VignetteFilter extends SimpleFragmentShaderFilter {
    public VignetteFilter(Context context) {
        super(context, "filter/fsh/mx_vignette.glsl");
    }
}
