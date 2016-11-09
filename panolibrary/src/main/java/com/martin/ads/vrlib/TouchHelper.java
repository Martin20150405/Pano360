package com.martin.ads.vrlib;

import android.content.res.Resources;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import com.martin.ads.vrlib.constant.PanoMode;
import com.martin.ads.vrlib.utils.BitmapUtils;
import com.martin.ads.vrlib.utils.StatusHelper;

/**
 * Created by Ads on 2016/11/7.
 */
public class TouchHelper {
    private GestureDetector gestureDetector;
    private StatusHelper statusHelper;
    private PanoRender mRenderer;
    //modified from Asha
    //hzqiujiadi ashqalcn@gmail.com
    private static final float sDensity =  Resources.getSystem().getDisplayMetrics().density;
    private static final float sDamping = 0.2f;

    public TouchHelper(final StatusHelper statusHelper, final PanoRender mRenderer) {
        this.statusHelper = statusHelper;
        this.mRenderer = mRenderer;
        gestureDetector=new GestureDetector(statusHelper.getContext(),new GestureDetector.SimpleOnGestureListener(){

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //mRenderer.saveImg();
                Log.d("lalala","onSingleTapConfirmed"+e.getPointerCount());
                if (statusHelper.getPanoDisPlayMode()== PanoMode.DUAL_SCREEN)
                    statusHelper.setPanoDisPlayMode(PanoMode.SINGLE_SCREEN);
                else statusHelper.setPanoDisPlayMode(PanoMode.DUAL_SCREEN);

                if (statusHelper.getPanoInteractiveMode()==PanoMode.MOTION)
                    statusHelper.setPanoInteractiveMode(PanoMode.TOUCH);
                else statusHelper.setPanoInteractiveMode(PanoMode.MOTION);
                Toast.makeText(statusHelper.getContext(),"不要乱点。。",Toast.LENGTH_LONG).show();
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                Log.d("lalala","onScroll"+e1.getPointerCount()+" "+e2.getPointerCount());
                mRenderer.setDeltaX(mRenderer.getDeltaX() + distanceX / sDensity * sDamping);
                mRenderer.setDeltaY(mRenderer.getDeltaY() + distanceY / sDensity * sDamping);
                return super.onScroll(e1, e2, distanceX, distanceY);
            }


        });
    }

    public boolean handleTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        return gestureDetector.onTouchEvent(event);
    }

}
