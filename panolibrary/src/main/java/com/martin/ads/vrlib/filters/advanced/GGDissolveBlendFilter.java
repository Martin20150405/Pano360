package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;

import com.example.panolibrary.R;

/**
 * Created by Ads on 2016/11/20.
 */

public class GGDissolveBlendFilter extends GGMixBlendFilter {
    public GGDissolveBlendFilter(Context context, float mMixturePercent) {
        super(context, R.raw.fragment_shader_two_input_dissolve_blend, mMixturePercent);
    }

    public GGDissolveBlendFilter(Context context) {
        super(context, R.raw.fragment_shader_two_input_dissolve_blend, 0.5f);
    }
}
