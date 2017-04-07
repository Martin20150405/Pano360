package com.martin.ads.vrlib.filters.advanced.mx;

import android.content.Context;

import com.martin.ads.vrlib.filters.base.SimpleFragmentShaderFilter;

/**
 * Created by Ads on 2017/1/31.
 * BlackWhiteFilter (黑白)
 */

public class BlackWhiteFilter extends SimpleFragmentShaderFilter {
    public BlackWhiteFilter(Context context) {
        super(context, "filter/fsh/mx_black_white.glsl");
    }
}
