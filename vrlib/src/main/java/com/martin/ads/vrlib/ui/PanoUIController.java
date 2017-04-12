package com.martin.ads.vrlib.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.martin.ads.vrlib.R;
import com.martin.ads.vrlib.filters.advanced.FilterType;
import com.martin.ads.vrlib.utils.UIUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Ads on 2016/11/10.
 */
public class PanoUIController {

    private RelativeLayout controlToolbar;
    private ToggleButton gyroBtn;             // 陀螺仪控制按钮
    private ToggleButton dualScreenBtn;       // 单双屏
    private ImageView backBtn;
    private ImageView screenshotBtn;

    private RelativeLayout progressToolbar;
    private SeekBar processSeekBar;                    // 播放进度条
    private TextView currTimeText;                  // 当前播放时间
    private TextView totalTimeText;             // 时间总长度
    private ToggleButton playBtn;        // 启动、暂停按钮

    private boolean visible;

    private UICallback uiCallback;
    private boolean seekBarTouched;

    private String lengthStr;

    private Timer hideControllerTimer;
    private HideControllerTimerTask hideControllerTimerTask;
    private boolean autoHideController;

    private Context context;

    private boolean imageMode;

    public PanoUIController(RelativeLayout controlToolbar,RelativeLayout progressToolbar,Context context,boolean imageMode) {
        this.controlToolbar=controlToolbar;
        this.progressToolbar=progressToolbar;
        this.context=context;
        visible=true;
        this.imageMode=imageMode;
        initView();
    }

    private void initView(){
        //controlToolbar
        gyroBtn= (ToggleButton) controlToolbar.findViewById(R.id.gyro_btn);
        dualScreenBtn= (ToggleButton) controlToolbar.findViewById(R.id.dualScreen_btn);
        backBtn= (ImageView) controlToolbar.findViewById(R.id.back_btn);
        screenshotBtn= (ImageView) controlToolbar.findViewById(R.id.screenshot_btn);
        //progressToolbar
        processSeekBar= (SeekBar) progressToolbar.findViewById(R.id.progress_seek_bar);
        currTimeText = (TextView) progressToolbar.findViewById(R.id.txt_time_curr);
        totalTimeText = (TextView) progressToolbar.findViewById(R.id.txt_time_total);
        playBtn= (ToggleButton) progressToolbar.findViewById(R.id.play_btn);

        seekBarTouched=false;
        processSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                cancelHideControllerTimer();
                seekBarTouched=true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                uiCallback.playerSeekTo(seekBar.getProgress());
                seekBarTouched=false;
                startHideControllerTimer();
            }
        });
        gyroBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHideControllerTimer();
                uiCallback.changeInteractiveMode();
            }
        });

        dualScreenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHideControllerTimer();
                uiCallback.changeDisPlayMode();
            }
        });

        screenshotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHideControllerTimer();
                uiCallback.requestScreenshot();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHideControllerTimer();
                uiCallback.requestFinish();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHideControllerTimer();
                uiCallback.changePlayingStatus();
            }
        });

        controlToolbar.findViewById(R.id.add_filter_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startHideControllerTimer();
                uiCallback.addFilter(null);
            }
        });

        if(imageMode) progressToolbar.setVisibility(View.GONE);
    }
    public void hide(){
        if (!visible) return;
        visible=false;
        progressToolbar.setVisibility(View.GONE);
        controlToolbar.setVisibility(View.GONE);
    }

    public void show(){
        if (visible) return;
        visible=true;
        if(!imageMode) progressToolbar.setVisibility(View.VISIBLE);
        controlToolbar.setVisibility(View.VISIBLE);
    }

    public boolean isVisible() {
        return visible;
    }

    public interface UICallback{
        void requestScreenshot();
        void requestFinish();
        void changeDisPlayMode();
        void changeInteractiveMode();
        void changePlayingStatus();
        void playerSeekTo(int pos);
        int getPlayerDuration();
        int getPlayerCurrentPosition();
        void addFilter(FilterType filterType);
    }

    public void setInfo(){
        processSeekBar.setProgress(0);
        int duration=uiCallback.getPlayerDuration();
        processSeekBar.setMax(duration);

        lengthStr = UIUtils.getShowTime(duration);
        currTimeText.setText("00:00:00");
        totalTimeText.setText(lengthStr);
    }

    public void setUiCallback(UICallback uiCallback) {
        this.uiCallback = uiCallback;
    }


    public void updateProgress(){
        if (!seekBarTouched)
            handleProgress.sendEmptyMessage(0);
    }
    private Handler handleProgress = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what)
            {
                case 0://更新进度条
                    int position = uiCallback.getPlayerCurrentPosition();
                    if (position >= 0)
                    {
                        processSeekBar.setProgress(position);
                        String cur = UIUtils.getShowTime(position);
                        currTimeText.setText(cur);
                    }
                    break;
            }
        }
    };

    public void startHideControllerTimer(){
        if (!autoHideController) return;
        cancelHideControllerTimer();
        hideControllerTimer=new Timer();
        hideControllerTimerTask=new HideControllerTimerTask();
        hideControllerTimer.schedule(hideControllerTimerTask,2666);
    }

    public void cancelHideControllerTimer(){
        if (hideControllerTimer!=null) {
            hideControllerTimer.cancel();
        }
        if (hideControllerTimerTask!=null) {
            hideControllerTimerTask.cancel();
        }
    }

    public class HideControllerTimerTask extends TimerTask{
        @Override
        public void run() {
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hide();
                }
            });
        }
    }

    public boolean isAutoHideController() {
        return autoHideController;
    }

    public void setAutoHideController(boolean autoHideController) {
        this.autoHideController = autoHideController;
    }
}
