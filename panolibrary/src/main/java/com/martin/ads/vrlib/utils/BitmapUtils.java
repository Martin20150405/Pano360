package com.martin.ads.vrlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by Ads on 2016/11/8.
 */
public class BitmapUtils {
    public static void sendImage(int width, int height, Context context) {
        ByteBuffer rgbaBuf = ByteBuffer.allocateDirect(width * height * 4);
        rgbaBuf.position(0);
        //about 20-50ms
        long start = System.nanoTime();
        rgbaBuf.order(ByteOrder.LITTLE_ENDIAN);
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                rgbaBuf);
        rgbaBuf.rewind();
        long end = System.nanoTime();

        Log.d("TryOpenGL", "glReadPixels time: " + (end - start)/1000000+" ms");

        //about 700-4000ms
        //it will consume large memory and may take a long time, depends on the phone
        new SaveBitmapTask(rgbaBuf,width,height,context).execute();
    }

    static class SaveBitmapTask extends AsyncTask<Void, Integer, Boolean>{
        long start;

        ByteBuffer rgbaBuf;
        int width, height;
        Context context;

        String filePath;

        public SaveBitmapTask(ByteBuffer rgbaBuf, int width, int height, Context context) {
            this.rgbaBuf = rgbaBuf;
            this.width = width;
            this.height = height;
            this.context = context;
            filePath=Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/Pano360_ScreenShot_" +  +width + "_" + height + "_" + System.currentTimeMillis()+".png";
        }

        @Override
        protected void onPreExecute() {
            start = System.nanoTime();
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            saveRgb2Bitmap(rgbaBuf, filePath , width, height);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            Log.d("TryOpenGL", "saveBitmap time: " + (System.nanoTime() - start)/1000000+" ms");
            Toast.makeText(context,"ScreenShot is saved to "+filePath,Toast.LENGTH_LONG).show();
            super.onPostExecute(aBoolean);
        }
    }
    public static void saveRgb2Bitmap(Buffer buf, String filename, int width, int height) {

        Log.d("TryOpenGL", "Creating " + filename);
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(filename));
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(buf);

            Bitmap outImg = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Matrix rotate = new Matrix();
            rotate.setRotate(180,(float)width/2,(float)height/2);
            Canvas canvas = new Canvas(outImg);
            canvas.drawBitmap(bmp, rotate, new Paint());
            outImg.compress(Bitmap.CompressFormat.PNG, 90, bos);
            bmp.recycle();
            outImg.recycle();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
