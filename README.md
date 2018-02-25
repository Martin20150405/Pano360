# Pano360
[![Build Status](https://travis-ci.org/Martin20150405/Pano360.svg?branch=master)](https://travis-ci.org/Martin20150405/Pano360) [![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LECENSE) [![](https://jitpack.io/v/Martin20150405/Pano360.svg)](https://jitpack.io/#Martin20150405/Pano360)  ![progress](http://progressed.io/bar/62?title=Progress)

Pure Java library to play 360 degree panorama video (VR video) on Android. Using OpenGL ES 2.0 
  
Pano 360 是一个Android平台下纯Java的全景（360度/VR）视频播放库，使用OpenGL ES 2.0来进行视频渲染，没有使用第三方库

**Demo App [在这里下](https://raw.githubusercontent.com/Martin20150405/Pano360/master/pano360demo/release/pano360demo-release.apk)~**

### Read this in other languages: [English](README.en.md)

## [系列教程：从零开始写一个Android平台下的全景视频播放器](http://blog.csdn.net/Martin20150405/article/details/53149578)


## 平台需求
* OpenGL ES 2.0 
* Android 4.0.3 (API-15) 以上

## 特性
* 单、双屏切换
* 陀螺仪、触控(拖动、缩放)两种交互模式切换
* 播放进度控制，控制栏自动隐藏
* 类似GPUImage的滤镜组，支持多个滤镜叠加，滤镜顺序可在渲染到球体之前或之后
* 支持原视频渲染（铺满屏幕/剪切/自适应）
* 支持播放全景图片
* 视频实时截图
* 在线视频播放（你可能需要自行处理多种格式的解码问题）
* 支持锁定任意坐标轴,用户从不同角度进入，看到的是同一个场景
    * **LOCK_MODE_AXIS_Y**： 和Cardboard Motion类似
* 支持忽略任意坐标轴的旋转角度
* 2D视频VR影院模式 
* 支持简单热点(图片/视频)
	
## 截图
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/player_screen.png)

[**Youtube**](https://youtu.be/kTJfI_dRLUk)
[**优酷**](http://v.youku.com/v_show/id_XMjY4ODI4OTM3Mg==?spm=a2h3j.8428770.3416059.1)

## 适用对象
*  如果你对于如何实现一个Android平台下的全景视频播放器感兴趣，或者急于使用一个带播放控制功能的全景视频播放器，或者有意在全景视频播放器中加入各种奇怪的功能，这个项目可能会对你有帮助。

## 如何使用
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
	dependencies {
	        compile 'com.github.Martin20150405.Pano360:vrlib:v1.1.2'
	}
	
* 有两种方法可以使用该库，详情请参考Demo App  

* 一行代码使用带播放控制的`Activity`  （由类库提供）
```java
Pano360ConfigBundle.newInstance().setFilePath(filePath).startEmbeddedActivity(this);
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
panoViewWrapper =PanoViewWrapper.with(this)
		.setConfig(configBundle)
		.setGlSurfaceView(glSurfaceView)
		.init();
glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return panoViewWrapper.handleTouchEvent(event);
	}
});
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
```

## 未来特性（不要期望过高- -|||）
* 加速度+电子罗盘支持（适合没有陀螺仪的手机）
* 快速切换使用的解码器，例如IjkMediaPlayer
* 小窗口/fragment播放
* Handler+MessageQueue
* 多种全景格式
* 热点支持（Hotspot）、头控支持
* Anti Distortion
* RTSP RTMP (with VLC/Vitamio)
* 完整播放控制功能
* 视频录制/转码/倍速播放 (Mediacodec/ffmpeg)

## [历史版本](https://github.com/Martin20150405/Pano360/releases)

## [更新日志](https://github.com/Martin20150405/Pano360/wiki/ChangeLog)



## 反馈交流

* 开启一个issue
* 发送邮件至martin20150405@163.com
* 如果觉得这个项目对你有帮助，欢迎star,欢迎来一起改进这个项目

