package com.miaokao.android.app.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.animation.Interpolator;  
import android.widget.Scroller;

/**
 * @TODO   自定义一个Scroll类，用于控制ViewPager滑动速度
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-20 上午11:45:12
 */
public class FixedSpeedScroller extends Scroller {
	private int mDuration = 1000;

	public FixedSpeedScroller(Context context) {
		super(context);
	}

	public FixedSpeedScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	@SuppressLint("NewApi")
	public FixedSpeedScroller(Context context, Interpolator interpolator, boolean flywheel) {
		super(context, interpolator, flywheel);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy, mDuration);
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy) {
		super.startScroll(startX, startY, dx, dy, mDuration);
	}
}