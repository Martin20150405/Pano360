# Pano360
[![Build Status](https://travis-ci.org/Martin20150405/Pano360.svg?branch=master)](https://travis-ci.org/Martin20150405/Pano360)

[![license](https://img.shields.io/github/license/mashape/apistatus.svg)](LECENSE) 

![progress](http://progressed.io/bar/55?title=Progress)

Pure Java library to play 360 degree panorama video (VR video) on Android. Using OpenGL ES 2.0 

**Download Demo App [HERE](https://github.com/Martin20150405/Pano360/blob/master/app/app-release.apk)~**

## Platform Requirements
* OpenGL ES 2.0 
* At least Android 4.0.3 (API-15) 

##Features
* Single/Dual Screen support
    * by configuring rows and cols you can get as many screens as you want
* Support two modes: Gyroscope(Motion) or Pinch,Scroll(Touch)
* Player control
* GPUImage-like real-time on-screen filter group
* Panorama photo
* Screenshot support
* Online video support (m3u8, etc.), you may have to deal with media decoding yourself.
* Lock any axis, user can enter view in any rotation, and will see the same view firstly
    * **LOCK_MODE_AXIS_Y**:similar to Cardboard Motion
* Ignore rotation of any axis

##Preview (Screenshots)
![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/player_screen.png)

![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/preview.gif)

![ScreenShot](https://github.com/Martin20150405/Pano360/blob/master/screenshots/main_screen.png)

##Target user
* If you are interested in implementing a panorama video player on Android, or you are urged yo use a Panorama video player with playing control, or you want to add more functions to Panorama video player, you may find this project helpful.

##Integration (How to use)
* There are two ways to integrate this library, you can compile demo app for more details.

* Start an `Activity` provided by library 
```java
Intent intent=new Intent(MainActivity.this,PanoPlayerActivity.class);
intent.putExtra("videoPath",filePath);
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
GLSurfaceView glSurfaceView=(GLSurfaceView) findViewById(R.id.surface_view);
panoViewWrapper =new PanoViewWrapper(this,videoPath, glSurfaceView);
glSurfaceView.setOnTouchListener(new View.OnTouchListener() {
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return panoViewWrapper.handleTouchEvent(event);
	}
});
```

##Future works (Don't expect too much- -|||)
* Acc+Mag support（used for phones without Gyroscope）
* MediaPlayer switch (like IjkMediaPlayer)
* jcenter/maven
* Tiny window / Fragment playing
* Handler+MessageQueue
* More Panorama format support
* Hotspot
* Anti Distortion
* RTSP RTMP (with VLC/Vitamio)

## [Releases](https://github.com/Martin20150405/Pano360/releases)

## [ChangeLog](https://github.com/Martin20150405/Pano360/wiki/ChangeLog)

##Feedback

* Open an issue
* Send an E-mail to martin20150405@163.com
* If you found this project helpful, star is highly welcomed. Also, let's improve this project together.
