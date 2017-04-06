package com.martin.ads.vrlib.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;

/**
 * Created by Ads on 2017/4/6.
 */

/**
 TextImageGenerator.newInstance()
 .setPadding(25)
 .setTextColor(Color.WHITE)
 .setBackgroundColor(Color.TRANSPARENT)
 .setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/font_26.ttf"))
 .setTextSize(25)
 .addTextToImage("I'm text. 我是文字")
 */
public class TextImageGenerator {
    private int textSize;
    private int textColor;
    private int padding;
    private int backgroundColor;
    private Typeface typeface;

    private TextImageGenerator() {
        textSize=15;
        textColor= Color.TRANSPARENT;
        padding=15;
        backgroundColor=Color.BLACK;
        typeface=null;
    }

    public static TextImageGenerator newInstance(){
        return new TextImageGenerator();
    }

    public Bitmap addTextToImage(String text) {
        Paint paint = new Paint();
        paint.setColor(textColor);
        paint.setTypeface(typeface);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setTextSize(textSize);

        float textWidth=getTextWidth(paint,text);
        float textHeight=getTextHeight(paint);
        int imgWidth=(int)Math.ceil(textWidth)+padding;
        int imgHeight=(int)Math.ceil(textHeight)+padding;
        Bitmap resultBmp = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBmp);
        canvas.drawColor(backgroundColor);

        float tX = (imgWidth-textWidth)/2;
        float tY = (imgHeight - textHeight)/2+getFontLeading(paint);
        canvas.drawText(text,tX,tY,paint);
        return resultBmp;
    }

    public static float getTextWidth(Paint paint, String str) {
        return paint.measureText(str);
    }

    public static float getTextHeight(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.descent - fm.ascent;
    }

    public static float getFontLeading(Paint paint)  {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return fm.leading- fm.ascent;
    }

    public TextImageGenerator setTextSize(int textSize) {
        this.textSize = textSize;
        return this;
    }

    public TextImageGenerator setTextColor(int textColor) {
        this.textColor = textColor;
        return this;
    }

    public TextImageGenerator setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    public TextImageGenerator setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public TextImageGenerator setTypeface(Typeface typeface) {
        this.typeface = typeface;
        return this;
    }
}
