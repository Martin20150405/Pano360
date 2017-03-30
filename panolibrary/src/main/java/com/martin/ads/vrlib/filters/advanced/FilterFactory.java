package com.martin.ads.vrlib.filters.advanced;

import android.content.Context;

import com.martin.ads.vrlib.filters.advanced.mx.BlackWhiteFilter;
import com.martin.ads.vrlib.filters.advanced.mx.BrightnessFilter;
import com.martin.ads.vrlib.filters.advanced.mx.FillLightFilter;
import com.martin.ads.vrlib.filters.advanced.mx.GreenHouseFilter;
import com.martin.ads.vrlib.filters.advanced.mx.MoonLightFilter;
import com.martin.ads.vrlib.filters.advanced.mx.MultiplyFilter;
import com.martin.ads.vrlib.filters.advanced.mx.MxFaceBeautyFilter;
import com.martin.ads.vrlib.filters.advanced.mx.MxLomoFilter;
import com.martin.ads.vrlib.filters.advanced.mx.MxProFilter;
import com.martin.ads.vrlib.filters.advanced.mx.PastTimeFilter;
import com.martin.ads.vrlib.filters.advanced.mx.PrintingFilter;
import com.martin.ads.vrlib.filters.advanced.mx.ReminiscenceFilter;
import com.martin.ads.vrlib.filters.advanced.mx.ShiftColorFilter;
import com.martin.ads.vrlib.filters.advanced.mx.SunnyFilter;
import com.martin.ads.vrlib.filters.advanced.mx.ToyFilter;
import com.martin.ads.vrlib.filters.advanced.mx.VignetteFilter;
import com.martin.ads.vrlib.filters.base.AbsFilter;
import com.martin.ads.vrlib.filters.base.PassThroughFilter;

/**
 * Created by Ads on 2017/2/13.
 */

public class FilterFactory {
    public static AbsFilter createFilter(FilterType filterType, Context context){
        switch (filterType){
            //Effects
            case FILL_LIGHT_FILTER:
                return new FillLightFilter(context);
            case GREEN_HOUSE_FILTER:
                return new GreenHouseFilter(context);
            case BLACK_WHITE_FILTER:
                return new BlackWhiteFilter(context);
            case PAST_TIME_FILTER:
                return new PastTimeFilter(context);
            case MOON_LIGHT_FILTER:
                return new MoonLightFilter(context);
            case PRINTING_FILTER:
                return new PrintingFilter(context);
            case TOY_FILTER:
                return new ToyFilter(context);
            case BRIGHTNESS_FILTER:
                return new BrightnessFilter(context);
            case VIGNETTE_FILTER:
                return new VignetteFilter(context);
            case MULTIPLY_FILTER:
                return new MultiplyFilter(context);
            case REMINISCENCE_FILTER:
                return new ReminiscenceFilter(context);
            case SUNNY_FILTER:
                return new SunnyFilter(context);
            case MX_LOMO_FILTER:
                return new MxLomoFilter(context);
            case SHIFT_COLOR_FILTER:
                return new ShiftColorFilter(context);
            case MX_FACE_BEAUTY_FILTER:
                return new MxFaceBeautyFilter(context);
            case MX_PRO_FILTER:
                return new MxProFilter(context);
            default:
                return new PassThroughFilter(context);
        }
    }

    public static AbsFilter randomlyCreateFilter(Context context){
        int pos= ((int) (Math.random()*Integer.MAX_VALUE))%FilterType.values().length;
        return createFilter(FilterType.values()[pos],context);
    }
}
