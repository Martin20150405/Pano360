package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;

/**
 * Created by Ads on 2016/11/20.
 */

public class DissolveBlendFilter extends MixBlendFilter {
    public DissolveBlendFilter(Context context) {
        super(context, "filter/fsh/two_input_dissolve_blend.glsl", 0.5f);
    }
}
