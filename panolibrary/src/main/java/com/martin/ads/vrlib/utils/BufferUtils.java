package com.martin.ads.vrlib.utils;


import com.martin.ads.vrlib.constant.Constants;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by Ads on 2016/6/25.
 */
public class BufferUtils {
    public static FloatBuffer getFloatBuffer(final float[] array,int offset){
        FloatBuffer bb=ByteBuffer.allocateDirect(
                array.length * Constants.FLOAT_SIZE_BYTES)
                .order(ByteOrder.nativeOrder())
                .asFloatBuffer()
                .put(array);
        bb.position(offset);
        return bb;
    }
}
