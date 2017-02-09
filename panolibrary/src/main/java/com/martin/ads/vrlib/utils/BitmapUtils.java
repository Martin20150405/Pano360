package com.martin.ads.vrlib.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ads on 2016/11/8.
 */
public class BitmapUtils {


    public static void sendImage(int width, int height, Context context) {
        final IntBuffer pixelBuffer = IntBuffer.allocate(width * height);

        //depends on the resolution of screen, about 20-50ms (1280x720)
        long start = System.nanoTime();
        GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE,
                pixelBuffer);
        long end = System.nanoTime();

        Log.d("TryOpenGL", "glReadPixels time: " + (end - start)/1000000+" ms");

        //about 700-4000ms(png) 200-1000ms(jpeg)
        //use jpeg instead of png to save time
        //it will consume large memory and may take a long time, depends on the phone
        new SaveBitmapTask(pixelBuffer,width,height,context).execute();
    }

    static class SaveBitmapTask extends AsyncTask<Void, Integer, Boolean>{
        long start;

        IntBuffer rgbaBuf;
        int width, height;
        Context context;

        String filePath;

        public SaveBitmapTask(IntBuffer rgbaBuf, int width, int height, Context context) {
            this.rgbaBuf = rgbaBuf;
            this.width = width;
            this.height = height;
            this.context = context;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
            File sdRoot = Environment.getExternalStorageDirectory();
            String dir = "/Pano360Screenshots/";
            File mkDir = new File(sdRoot, dir);
            if (!mkDir.exists())
                mkDir.mkdir();
            String filename="/PanoScreenShot_" +width + "_" + height + "_" + simpleDateFormat.format(new Date())+".jpg";
            filePath= mkDir.getAbsolutePath()+filename;
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
    public static void saveRgb2Bitmap(IntBuffer buf, String filePath, int width, int height) {
        final int[] pixelMirroredArray = new int[width * height];
        Log.d("TryOpenGL", "Creating " + filePath);
        BufferedOutputStream bos = null;
        try {
            int[] pixelArray = buf.array();
            // rotate 180 deg with x axis because y is reversed
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    pixelMirroredArray[(height - i - 1) * width + j] = pixelArray[i * width + j];
                }
            }
            bos = new BufferedOutputStream(new FileOutputStream(filePath));
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bmp.copyPixelsFromBuffer(IntBuffer.wrap(pixelMirroredArray));
            bmp.compress(Bitmap.CompressFormat.JPEG, 90, bos);
            bmp.recycle();
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


    public static Bitmap loadBitmapFromAssets(Context context,String filePath){
        InputStream inputStream = null;
        try {
            inputStream = context.getResources().getAssets().open(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(inputStream==null) return null;
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeStream(inputStream);
        return bitmap;
    }

    public static Bitmap loadBitmapFromRaw(Context context, int resourceId){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inScaled=false;
        Bitmap bitmap= BitmapFactory.decodeResource(context.getResources(),resourceId,options);
        return bitmap;
    }
}
