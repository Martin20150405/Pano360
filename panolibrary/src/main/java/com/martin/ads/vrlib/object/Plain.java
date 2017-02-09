package com.martin.ads.vrlib.object;

import android.opengl.GLES20;


import com.martin.ads.vrlib.constant.Rotation;
import com.martin.ads.vrlib.utils.BufferUtils;
import com.martin.ads.vrlib.utils.PlainTextureRotationUtils;
import com.martin.ads.vrlib.utils.ShaderUtils;

import java.nio.FloatBuffer;

/**
 * Created by Ads on 2016/11/19.
 * This class is assumed to only render in FilterGroup
 * if you want to render it alone, set isInGroup false
 */

public class Plain {
    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mTexCoordinateBuffer;
    private static final float TRIANGLES_DATA[] = {
            -1.0f, -1.0f, 0f,
            1.0f, -1.0f, 0f,
            -1.0f, 1.0f, 0f,
            1.0f, 1.0f, 0f
    };

    public Plain(boolean isInGroup) {
        mVerticesBuffer = BufferUtils.getFloatBuffer(TRIANGLES_DATA,0);
        if (isInGroup)
            mTexCoordinateBuffer = BufferUtils.getFloatBuffer(PlainTextureRotationUtils.getRotation(Rotation.NORMAL, false, true), 0);
        else mTexCoordinateBuffer = BufferUtils.getFloatBuffer(PlainTextureRotationUtils.TEXTURE_NO_ROTATION,0);
    }

    public void uploadVerticesBuffer(int positionHandle){
        FloatBuffer vertexBuffer = getVerticesBuffer();
        if (vertexBuffer == null) return;
        vertexBuffer.position(0);

        GLES20.glVertexAttribPointer(positionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer);
        ShaderUtils.checkGlError("glVertexAttribPointer maPosition");
        GLES20.glEnableVertexAttribArray(positionHandle);
        ShaderUtils.checkGlError("glEnableVertexAttribArray maPositionHandle");
    }

    public void uploadTexCoordinateBuffer(int textureCoordinateHandle){
        FloatBuffer textureBuffer = getTexCoordinateBuffer();
        if (textureBuffer == null) return;
        textureBuffer.position(0);

        GLES20.glVertexAttribPointer(textureCoordinateHandle, 2, GLES20.GL_FLOAT, false, 0, textureBuffer);
        ShaderUtils.checkGlError("glVertexAttribPointer maTextureHandle");
        GLES20.glEnableVertexAttribArray(textureCoordinateHandle);
        ShaderUtils.checkGlError("glEnableVertexAttribArray maTextureHandle");
    }


    public FloatBuffer getVerticesBuffer() {
        return mVerticesBuffer;
    }

    public FloatBuffer getTexCoordinateBuffer() {
        return mTexCoordinateBuffer;
    }

    //only used to flip texture
    public void setTexCoordinateBuffer(FloatBuffer mTexCoordinateBuffer) {
        this.mTexCoordinateBuffer = mTexCoordinateBuffer;
    }

    public void setVerticesBuffer(FloatBuffer mVerticesBuffer) {
        this.mVerticesBuffer = mVerticesBuffer;
    }

    public void draw() {
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }
}
