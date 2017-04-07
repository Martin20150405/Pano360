package com.martin.ads.vrlib.filters.advanced.mx;

import android.content.Context;

import com.martin.ads.vrlib.filters.base.SimpleFragmentShaderFilter;


/**
 * Created by Ads on 2017/1/31.
 * ShiftColorFilter (提取红色)
 */

public class ShiftColorFilter extends SimpleFragmentShaderFilter {
    public ShiftColorFilter(Context context) {
        super(context, "filter/fsh/mx_shift_color.glsl");
    }
}
