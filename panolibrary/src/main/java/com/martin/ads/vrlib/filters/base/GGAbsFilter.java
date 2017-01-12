package com.martin.ads.vrlib.filters.base;

import android.graphics.PointF;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.util.LinkedList;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ads on 2016/11/19.
 */

public abstract class GGAbsFilter {
    private final LinkedList<Runnable> mPreDrawTaskList;
    protected int width,height;

    public GGAbsFilter() {
        mPreDrawTaskList = new LinkedList<Runnable>();
    }

    abstract public void init();

    abstract public void onPreDrawElements();

    abstract public void destroy();

    public void onFilterChanged(int width, int height){
        this.width=width;
        this.height=height;
    }

    abstract public void onDrawFrame(final int textureId);


    public void runPreDrawTasks() {
        while (!mPreDrawTaskList.isEmpty()) {
            mPreDrawTaskList.removeFirst().run();
        }
    }

    public void addPreDrawTask(final Runnable runnable) {
        synchronized (mPreDrawTaskList) {
            mPreDrawTaskList.addLast(runnable);
        }
    }
  /*
    public void setUniform1i(final int location, final int intValue) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1i(location, intValue);
            }
        });
    }

    public void setUniform1f(final int location, final float floatValue) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1f(location, floatValue);
            }
        });
    }

    public void setUniform2f(final int location, final float[] arrayValue) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform2fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    public void setUniform3f(final int location, final float[] arrayValue) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform3fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setUniform4f(final int location, final float[] arrayValue) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform4fv(location, 1, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setUniformFloatArray(final int location, final float[] arrayValue) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                GLES20.glUniform1fv(location, arrayValue.length, FloatBuffer.wrap(arrayValue));
            }
        });
    }

    protected void setPoint(final int location, final PointF point) {
        addPreDrawTask(new Runnable() {
            @Override
            public void run() {
                float[] vec2 = new float[2];
                vec2[0] = point.x;
                vec2[1] = point.y;
                GLES20.glUniform2fv(location, 1, vec2, 0);
            }
        });
    }

    protected void setUniformMatrix3f(final int location, final float[] matrix) {
        addPreDrawTask(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix3fv(location, 1, false, matrix, 0);
            }
        });
    }

    protected void setUniformMatrix4f(final int location, final float[] matrix) {
        addPreDrawTask(new Runnable() {

            @Override
            public void run() {
                GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
            }
        });
    }*/

}
