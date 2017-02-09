package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;

import com.example.panolibrary.R;

/**
 * Created by Ads on 2016/11/20.
 */

public class RiseFilter extends ThreeTextureFilter {
    public RiseFilter(Context context) {
        super(context, "filter/vsh/simple.glsl","filter/fsh/three_texture_rise.glsl");
    }
}
