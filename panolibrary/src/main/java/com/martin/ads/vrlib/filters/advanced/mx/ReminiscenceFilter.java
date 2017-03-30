package com.martin.ads.vrlib.filters.advanced.mx;

import android.content.Context;

import com.martin.ads.vrlib.filters.base.SimpleFragmentShaderFilter;


/**
 * Created by Ads on 2017/1/31.
 * ReminiscenceFilter (回忆)
 */

public class ReminiscenceFilter extends SimpleFragmentShaderFilter {
    public ReminiscenceFilter(Context context) {
        super(context, "filter/fsh/mx_reminiscence.glsl");
    }
}
