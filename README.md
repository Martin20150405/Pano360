# Pano360
[![Build Status](https://travis-ci.org/Martin20150405/Pano360.svg?branch=master)](https://travis-ci.org/Martin20150405/Pano360)
Pure Java library to play 360 degree panorama video (VR video) on Android. Using OpenGL ES 2.0 
  
Pano 360 是一个Android平台下纯Java的全景（360度/VR）视频播放库，使用OpenGL ES 2.0来进行视频渲染，没有使用第三方库

###Read this in other languages: [English](README.en.md)

## [系列教程：从零开始写一个Android平台下的全景视频播放器](http://blog.csdn.net/Martin20150405/article/details/53149578)


## 平台需求
* OpenGL ES 2.0 
* Android 4.0.3 (API-15) 以上

##特性
* 单、双屏切换
    * 支持单屏、双屏切换，通过配置rows和cols可以实现任意行任意列的分屏数目
* 陀螺仪、触控(拖动、缩放)两种交互模式切换
* 播放进度控制，控制栏自动隐藏
* 类似GPUImage的滤镜组，支持多个滤镜叠加，滤镜顺序可在渲染到球体之前或之后
    * 目前项目中包含三个滤镜，更多滤镜请参见[这里(还没做链接)](http://)
* 支持原视频渲染（铺满屏幕）
* 视频在线截图
* 在线视频播放（你可能需要自行处理多种格式的解码问题）
* 支持锁定任意坐标轴,用户从不同角度进入，看到的是同一个场景
    * **LOCK_MODE_AXIS_Y**： 和Cardboard Motion类似
* 支持忽略任意坐标轴的旋转角度
	
##截图
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/player_screen.jpg)
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/preview.gif)
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
GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);
panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView, PanoFilter.NORMAL);
glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return panoViewWrapper.handleTouchEvent(event);
	}
});
```

##未来特性（不要期望过高- -|||）
* 加速度+电子罗盘支持（适合没有陀螺仪的手机）
* 快速切换使用的解码器，例如IjkMediaPlayer
* jcenter/maven
* 小窗口/fragment播放
* Handler+MessageQueue
	* 目前并没有使用线程间的消息传递和锁机制，但是系统工作正常，因为不存在写写冲突，而且命令的执行顺序并不是那么重要
* 全景图片
* 多种全景格式
* 热点支持（Hotspot）、头控支持
* Anti Distortion
* RTSP RTMP (with VLC/Vitamio)

## [历史版本](https://github.com/Martin20150405/Pano360/releases)

## [更新日志](https://github.com/Martin20150405/Pano360/wiki/ChangeLog)



##反馈交流
>可能回复不及时，但是我承诺一定会回复!

* 开启一个issue
* 如果觉得这个项目对你有帮助，欢迎star

