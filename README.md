# Pano360
Pure Java library to play 360 degree panorama video (VR video) on Android. Using OpenGL ES 2.0 
  
Pano 360 是一个Android平台下纯Java的全景（360度/VR）视频播放库，使用OpenGL ES 2.0来进行视频渲染，没有使用第三方库

###Scroll down to read English version Readme

## [系列教程：从零开始写一个Android平台下的全景视频播放器]()
* 如果不能打开说明还没放链接

## 平台需求
* OpenGLES 2.0 
* Android 4.0.3 (API-15) 以上

##特性
* 单、双屏切换
    * 支持单屏、双屏切换，通过配置rows和cols可以实现任意行任意列的分屏数目（虽然我不知道多于2个有什么用）
* 陀螺仪、触控(拖动、缩放)两种交互模式切换
* 播放进度控制，控制栏自动隐藏
* 简单的实时滤镜（逐渐完善中）
    * 目前项目中包含一个黑白滤镜，一个反色滤镜  
* 视频在线截图
* 在线视频播放（你可能需要自行处理多种格式的解码问题，例如rmvb）

##截图
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/main_screen.jpg)
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/player_screen.jpg)

##适用对象
* 如果你对于如何实现一个Android平台下的全景视频播放器感兴趣，或者急于使用一个带播放控制功能的全景视频播放器，或者有意在全景视频播放器中加入各种奇怪的功能，这个项目可能会对你有帮助。

##如何使用
* 有两种方法可以使用该库，详情请参考demo app  

* 使用带播放控制的`Activity`  （由类库提供）
```java
Intent intent=new Intent(MainActivity.this,PanoPlayerActivity.class);
intent.putExtra("videoPath",filePath);
intent.putExtra("filter","NORMAL");
startActivity(intent);
```

* 提供一个`GLSurfaceView`,你可以在任意地方使用，但是需要自己处理播放控制和模式切换
```java
<android.opengl.GLSurfaceView
    android:id="@+id/surface_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
```java

public class DemoWithGLSurfaceView extends AppCompatActivity {
    private PanoViewWrapper panoViewWrapper;

    public static String TAG = "DemoWithGLSurfaceView";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getSupportActionBar().hide();
        setContentView(R.layout.player_layout);

        init();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);
    }

    private void init(){

        String videoPath=getIntent().getStringExtra("videoPath");
        GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);
        String filter=getIntent().getStringExtra("filter");
        if (filter.equals("NORMAL"))
            panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView, PanoFilter.NORMAL);
        else if (filter.equals("GRAY_SCALE")) panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView, PanoFilter.GRAY_SCALE);
        else if (filter.equals("INVERSE_COLOR")) panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView, PanoFilter.INVERSE_COLOR);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Logger.logTouchEvent(v,event);
                return panoViewWrapper.handleTouchEvent(event);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        panoViewWrapper.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        panoViewWrapper.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        panoViewWrapper.releaseResources();
    }

}
```

##未来特性（不要期望过高- -|||）
* 锁定坐标轴
    * 用户从不同角度进入，看到的是同一个场景
* 加速度+电子罗盘支持（适合没有陀螺仪的手机）
* 快速切换使用的解码器，例如IjkMediaPlayer
* jcenter/maven
* 缩短截图保存时间
* 原视频渲染
* 小窗口/fragment播放
* Handler+MessageQueue
	* 目前并没有使用线程间的消息传递和锁机制，但是系统工作正常，因为不存在写写冲突，而且命令的执行顺序并不是那么重要
* 全景图片
* 多种全景格式
* 更好的实时滤镜（多种滤镜支持、在线切换）
* 热点支持（Hotspot）、头控支持
* Anti Distortion
* RTSP RTMP (with VLC/Vitamio)

##反馈交流
>可能回复不及时，但是我承诺一定会回复!

* 开启一个issue
* 或者发送邮件至1036040418@qq.com
* **如果觉得这个项目对你有帮助，欢迎star**


#Sorry for my poor english
## Platform Requirements
* OpenGLES 2.0 
* At least Android 4.0.3 (API-15) 

##Features
* Single/Dual Screen support
    * by configuring rows and cols you can get as many screens as you want
* Support two modes: Gyroscope(Motion) or Pinch,Scroll(Touch)
* Player control, tool bar auto-hide (T_T)
* Simple real-time on-screen filter (improving)
    * now support gray-scale filter and invert-color filter
* Screenshot support
* Online video support (m3u8, etc.), you may have to deal media decode your self (like when playing rmvb).

##Preview (Screenshots)
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/main_screen.jpg)
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/player_screen.jpg)

##Target user
* If you are interested in implementing a panorama video player on Android, or you are urged yo use a Panorama video player with playing control, or you want to add more functions to Panorama video player, you may find this project helpful.

##Integration (How to use)
* There are two ways to integrate this library, you can compile demo app for more details.

* Start an `Activity` provided by library 
```java
Intent intent=new Intent(MainActivity.this,PanoPlayerActivity.class);
intent.putExtra("videoPath",filePath);
intent.putExtra("filter","NORMAL");
startActivity(intent);
```

* Or provide a `GLSurfaceView`,you can use it anywhere，but you have to deal player control and mode switch yourself
```java
<android.opengl.GLSurfaceView
    android:id="@+id/surface_view"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```
```java

public class DemoWithGLSurfaceView extends AppCompatActivity {
    private PanoViewWrapper panoViewWrapper;

    public static String TAG = "DemoWithGLSurfaceView";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getSupportActionBar().hide();
        setContentView(R.layout.player_layout);

        init();

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        getWindow().setAttributes(params);
    }

    private void init(){

        String videoPath=getIntent().getStringExtra("videoPath");
        GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);
        String filter=getIntent().getStringExtra("filter");
        if (filter.equals("NORMAL"))
            panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView, PanoFilter.NORMAL);
        else if (filter.equals("GRAY_SCALE")) panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView, PanoFilter.GRAY_SCALE);
        else if (filter.equals("INVERSE_COLOR")) panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView, PanoFilter.INVERSE_COLOR);
        glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //Logger.logTouchEvent(v,event);
                return panoViewWrapper.handleTouchEvent(event);
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        panoViewWrapper.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();

        panoViewWrapper.onResume();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        panoViewWrapper.releaseResources();
    }

}
```

##Future works (Don't expect too much- -|||)
* Lock axis( User can enter view in any rotation, and will see the same view firstly)
* Acc+Mag support（used for phones without Gyroscope）
* MediaPlayer switch (like IjkMediaPlayer)
* jcenter/maven
* Reduce time to save screenshots
* Render original video
* Tiny window / Fragment playing
* Handler+MessageQueue
* Panorama photo
* More Panorama format support
* Better filter support
* Hotspot
* Anti Distortion
* RTSP RTMP (with VLC/Vitamio)

##Feedback
>I promise to reply every single message (not in time maybe). Sorry again for my poor english.

* Open an issue
* Or send an e-mail to 1036040418@qq.com
* **If you found this project helpful, star is highly welcomed.**
