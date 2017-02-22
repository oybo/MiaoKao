package com.miaokao.android.app.widget;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.CountDownTimer;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.miaokao.android.app.R;

/**
 * @TODO 评论弹出
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-22 下午10:07:00
 */
public class SelectGradePopupWindow extends PopupWindow implements OnClickListener {

	private int mCount;
	
	// type = 1:驾校评论，只有“喜欢和不喜欢” ， type = 2：教练评论，有“好评，中评，差评”
	public SelectGradePopupWindow(final Activity context, int type, OnClickListener itemsOnClick) {
		super(context);

		final View popupView = View.inflate(context, R.layout.view_choose_comment, null);
		// 好评
		TextView hpTxt = (TextView) popupView.findViewById(R.id.pop_choose_comment_xh_txt);
		hpTxt.setOnClickListener(itemsOnClick);
		// 中评
		TextView zpTxt = (TextView) popupView.findViewById(R.id.pop_choose_comment_zp_txt);
		zpTxt.setOnClickListener(itemsOnClick);
		// 差评
		TextView cpTxt = (TextView) popupView.findViewById(R.id.pop_choose_comment_bxh_txt);
		cpTxt.setOnClickListener(itemsOnClick);
		// 取消按钮
		popupView.findViewById(R.id.pop_choose_comment_cancel_txt).setOnClickListener(this);

		switch (type) {
		case 1:
			hpTxt.setText("喜欢");
			popupView.findViewById(R.id.pop_choose_comment_zp_layout).setVisibility(View.GONE);
			cpTxt.setText("不喜欢");
			break;
		case 2:
			hpTxt.setText("好评");
			zpTxt.setText("中评");
			cpTxt.setText("差评");
			break;
		}
		
		// 设置SelectPicPopupWindow的View
		this.setContentView(popupView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.anim_popup_dir);
		setBackgroundDrawable(new BitmapDrawable());

		new CountDownTimer(175, 25) {
			@Override
			public void onTick(long millisUntilFinished) {
				mCount++;
				backgroundAlpha(context, (float) (1 - (0.1 * mCount)));
			}
			
			@Override
			public void onFinish() {
				
			}
		}.start();

		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				backgroundAlpha(context, 1f);
			}
		});

	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(Activity context, float bgAlpha) {
		WindowManager.LayoutParams lp = context.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		context.getWindow().setAttributes(lp);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.pop_choose_comment_cancel_txt:
			// 销毁弹出框
			dismiss();
			break;

		}
	}

}