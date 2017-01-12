package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;

import com.example.panolibrary.R;
import com.martin.ads.vrlib.utils.TextureHelper;

/**
 * Created by Ads on 2016/11/20.
 */

public class GGRiseFilter extends GGThreeTextureFilter {
    public GGRiseFilter(Context context) {
        super(context, R.raw.vertex_shader_three_texture,R.raw.fragment_shader_three_texture_rise);
    }
}
