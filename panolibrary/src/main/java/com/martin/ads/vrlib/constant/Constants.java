package com.martin.ads.vrlib.constant;

import android.hardware.Sensor;

/**
 * Created by Ads on 2016/6/25.
 */
public class Constants {
    public static final int FLOAT_SIZE_BYTES = 4;

    public static final int SENSOR_ACC= Sensor.TYPE_ACCELEROMETER;
    public static final int SENSOR_MAG=Sensor.TYPE_MAGNETIC_FIELD;
    public static final int SENSOR_ROT=Sensor.TYPE_ROTATION_VECTOR;

    public static final int LOCK_MODE_NONE=1;
    public static final int LOCK_MODE_ALL_AXIS=2;
    public static final int LOCK_MODE_GAME_ROTATION_VECTOR=3;
}
