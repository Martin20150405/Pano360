/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.martin.ads.pano360demo.util;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by zhouyou on 2016/6/24.
 * Class desc:
 *
 * 尺寸工具箱，提供与Android尺寸相关的工具方法
 */
public class DimenUtils {

	private DimenUtils() {
		throw new UnsupportedOperationException("cannot be instantiated");
	}

	/**
	 * dp单位转换为px
	 */
	public static int dp2px(Context context, float dpValue){
		return (int)(dpValue * (context.getResources().getDisplayMetrics().density) + 0.5f);
	}
	
	/**
	 * px单位转换为dp
	 */
	public static int px2dp(Context context, float pxValue){
		return (int)(pxValue / (context.getResources().getDisplayMetrics().density) + 0.5f);
	}

	/**
	 * sp单位转换为px
	 */
	public static int sp2px(Context context, float spVal) {
		if (context == null) {
			return -1;
		}
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
				spVal, context.getResources().getDisplayMetrics());
	}

	/**
	 * px单位转换为sp
	 */
	public static float px2sp(Context context, float pxVal) {
		if (context == null) {
			return -1;
		}
		return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
	}

}