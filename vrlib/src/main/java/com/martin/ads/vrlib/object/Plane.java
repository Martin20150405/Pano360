package com.martin.ads.vrlib.object;

import android.graphics.RectF;
import android.opengl.GLES20;


import com.martin.ads.vrlib.constant.Rotation;
import com.martin.ads.vrlib.utils.BufferUtils;
import com.martin.ads.vrlib.utils.PlaneTextureRotationUtils;
import com.martin.ads.vrlib.utils.ShaderUtils;

import java.nio.FloatBuffer;

/**
 * Created by Ads on 2016/11/19.
 * This class is assumed to only render in FilterGroup
 * if you want to render it alone, set isInGroup false
 */

public class Plane {
    private FloatBuffer mVerticesBuffer;
    private FloatBuffer mTexCoordinateBuffer;
    private final float TRIANGLES_DATA_CW[] = {
            -1.0f, -1.0f, 0f, //LD
            -1.0f, 1.0f, 0f,  //LU
            1.0f, -1.0f, 0f,  //RD
            1.0f, 1.0f, 0f    //RU
    };

    public Plane(boolean isInGroup) {
        mVerticesBuffer = BufferUtils.getFloatBuffer(TRIANGLES_DATA_CW,0);
        if (isInGroup)
            mTexCoordinateBuffer = BufferUtils.getFloatBuffer(PlaneTextureRotationUtils.getRotation(Rotation.NORMAL, false, true), 0);
        else mTexCoordinateBuffer = BufferUtils.getFloatBuffer(PlaneTextureRotationUtils.TEXTURE_NO_ROTATION,0);
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

    public Plane scale(float scalingFactor){
        float[] temp=new float[TRIANGLES_DATA_CW.length];
        System.arraycopy(TRIANGLES_DATA_CW,0,temp,0,TRIANGLES_DATA_CW.length);
        for(int i=0;i<temp.length;i++){
            temp[i]*=scalingFactor;
        }
        mVerticesBuffer = BufferUtils.getFloatBuffer(temp,0);
        return this;
    }

    public Plane resetTrianglesDataWithRect(RectF rectF){
        TRIANGLES_DATA_CW[0]=rectF.left; TRIANGLES_DATA_CW[1]=rectF.bottom;
        TRIANGLES_DATA_CW[3]=rectF.left; TRIANGLES_DATA_CW[4]=rectF.top;
        TRIANGLES_DATA_CW[6]=rectF.right; TRIANGLES_DATA_CW[7]=rectF.bottom;
        TRIANGLES_DATA_CW[9]=rectF.right; TRIANGLES_DATA_CW[10]=rectF.top;
        mVerticesBuffer = BufferUtils.getFloatBuffer(TRIANGLES_DATA_CW,0);
        return this;
    }

}
