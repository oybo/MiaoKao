package com.miaokao.android.app.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.miaokao.android.app.R;

/**
 * @TODO 信息提示 
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-22 下午10:07:00
 */
public class MessagePopupWindow extends PopupWindow {

	private Handler mHandler = new Handler();

	public MessagePopupWindow(final Activity context, String message) {
		super(context);

		final View popupView = View.inflate(context, R.layout.view_message_pop, null);

		TextView textView = (TextView) popupView.findViewById(R.id.view_message_pop_txt);

		textView.setText(message);

		// 设置SelectPicPopupWindow的View
		this.setContentView(popupView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.anim_popup_dir);
		setBackgroundDrawable(new BitmapDrawable());
		popupView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

	}

	public void showTop(View v) {
		int popupWidth = getContentView().getMeasuredWidth();
		int popupHeight = getContentView().getMeasuredHeight();
		int[] location = new int[2];
		v.getLocationOnScreen(location);
		showAtLocation(v, Gravity.NO_GRAVITY, (location[0] + v.getWidth() / 2) - popupWidth / 2, location[1]
				- popupHeight);

		mHandler.postDelayed(runnable, 1000);
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			if (isShowing()) {
				dismiss();
			}
		}
	};

}