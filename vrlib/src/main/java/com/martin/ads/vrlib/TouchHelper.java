package com.martin.ads.vrlib;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.ui.PanoUIController;
import com.martin.ads.vrlib.utils.StatusHelper;

/**
 * Created by Ads on 2016/11/7.
 */
public class TouchHelper {
    private GestureDetector gestureDetector;
    private StatusHelper statusHelper;
    private PanoRender mRenderer;
    private PanoUIController panoUIController;
    private PanoramaInteraction pano;
    private ScaleGestureDetector scaleGestureDetector;
    //modified from Asha
    //hzqiujiadi ashqalcn@gmail.com
    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;
    private static final float cameraDistance = 3000;
    private float centerWidth, centerHeight,verticalFov=90,horizontalFov=120;



    public TouchHelper(final StatusHelper statusHelper, final PanoRender mRenderer,PanoramaInteraction pano) {
        this.statusHelper = statusHelper;
        this.mRenderer = mRenderer;
        this.pano = pano;
        Display display = ((Activity)statusHelper.getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        centerWidth = size.x/2;
        centerHeight = size.y/2;

        init();
    }

    private void init(){
        gestureDetector=new GestureDetector(statusHelper.getContext(),new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (panoUIController!=null){
                    if (panoUIController.isVisible()) panoUIController.hide();
                    else panoUIController.show();
                }

                float distanceX = e.getX() - centerWidth;
                float distanceY = e.getY() - centerHeight;

                float yaw = (mRenderer.getSpherePlugin().getDeltaX() + distanceX / sDensity * sDamping);
                float pitch = (mRenderer.getSpherePlugin().getDeltaY() + distanceY / sDensity * sDamping);

                pano.checkClickArea(yaw,pitch);

                Log.e("click",e.getX()+" / "+e.getY());
                Log.e("deltas",mRenderer.getSpherePlugin().getDeltaX() + " / " + mRenderer.getSpherePlugin().getDeltaY());

//                mRenderer.getSpherePlugin().setDeltaX(mRenderer.getSpherePlugin().getDeltaX() + distanceX / sDensity * sDamping);
//                mRenderer.getSpherePlugin().setDeltaY(mRenderer.getSpherePlugin().getDeltaY() + distanceY / sDensity * sDamping);

                return super.onSingleTapConfirmed(e);
            }



           /* private float getYaw(float eventX, float centerW) {
                float b = getHorizontalB(centerW,1);
                double innerDist = eventX - centerW;

                double rad =  Math.atan(b/
                        (innerDist > 0 ? innerDist : -innerDist ));

                float deg= (float) Math.toDegrees(rad);
                deg = 180 - 90 - deg;
                deg = innerDist > 0 ? deg : -deg;
                return deg;
            }

            private float getPitch(float eventY, float centerH) {

                float b = getVerticalB(centerH,1);
                double innerDist = eventY - centerH;

                double rad =  Math.atan(b/
                        (innerDist > 0 ? innerDist : -innerDist ));

                float deg= (float) Math.toDegrees(rad);
                deg = 180 - 90 - deg;
                deg = innerDist > 0 ? -deg : deg;
                return deg;
            }


            private float getHorizontalB(float centerW, float ratio) {
                double radius = cameraDistance;
                float C = (180 - (horizontalFov * ratio)) / 2;
                double c = Math.sqrt((radius*radius) + (centerW*centerW) - (2 * radius*centerW * Math.cos(Math.toRadians(C))));

                double CosB = ((centerW*centerW) + (c*c) - (radius*radius)) / (2 * centerW * c);
                double B = Math.toDegrees(Math.acos(CosB));

                double BPrime = 180 - B;
                double b = centerW * Math.tan(Math.toRadians(BPrime));

                return (float) b;
            }

            private float getVerticalB(float centerH, float ratio) {
                double radius = cameraDistance;
                float C = (180 - (verticalFov * ratio)) / 2;
                double c = Math.sqrt((radius*radius) + (centerH*centerH) - (2 * radius*centerH * Math.cos(Math.toRadians(C))));

                double CosB = ((centerH*centerH) + (c*c) - (radius*radius)) / (2 * centerH * c);
                double B = Math.toDegrees(Math.acos(CosB));

                double BPrime = 180 - B;
                double b = centerH * Math.tan(Math.toRadians(BPrime));

                return (float) b;
            }
*/

            @Override
            public void onLongPress(MotionEvent e) {

                float distanceX = e.getX() - centerWidth;
                float distanceY = e.getY() - centerHeight;

                float yawMove = (mRenderer.getSpherePlugin().getDeltaX() + distanceX / sDensity * sDamping);
                float pitchMove = (mRenderer.getSpherePlugin().getDeltaY() + distanceY / sDensity * sDamping);

                pano.createHotspot(yawMove,pitchMove);
                super.onLongPress(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (statusHelper.getPanoInteractiveMode()==PanoMode.TOUCH){
                    mRenderer.getSpherePlugin().setDeltaX(mRenderer.getSpherePlugin().getDeltaX() + distanceX / sDensity * sDamping);
                    mRenderer.getSpherePlugin().setDeltaY(mRenderer.getSpherePlugin().getDeltaY() + distanceY / sDensity * sDamping);
                }
                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });

        scaleGestureDetector=new ScaleGestureDetector(statusHelper.getContext(), new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                float scaleFactor=detector.getScaleFactor();
                mRenderer.getSpherePlugin().updateScale(scaleFactor);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                //return true to enter onScale()
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {

            }
        });
    }

    public boolean handleTouchEvent(MotionEvent event) {
        //int action = event.getActionMasked();
        //也可以通过event.getPointerCount()来判断是双指缩放还是单指触控
        boolean ret=scaleGestureDetector.onTouchEvent(event);
        if (!scaleGestureDetector.isInProgress()){
            ret=gestureDetector.onTouchEvent(event);
        }
        return ret;
    }

    public void setPanoUIController(PanoUIController panoUIController) {
        this.panoUIController = panoUIController;
    }

    public void shotScreen(){
        mRenderer.saveImg();
    }

    public void setPano(PanoramaInteraction pano) {
        this.pano = pano;
    }
}
