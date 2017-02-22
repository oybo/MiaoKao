package com.miaokao.android.app.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miaokao.android.app.R;

/**
 * 自定义ActionBar ouyangbo
 */
public class HeaderView extends LinearLayout {

	private TextView mTitleTxt;
	private TextView mLeftTxt;
	private ImageView mLeftImg;
	private TextView mRightTxt;
	private ImageView mRightImg;

	private ImageView mColorsLine;
	
	private LinearLayout mLeftLayout;
	private LinearLayout mRightLayout;

	private OnLeftClickListenner mLeftListenner;
	private OnRightClickListenner mRightListenner;

	public HeaderView(Context context) {
		super(context);

		initView(context);
	}

	public HeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		initView(context);
	}

	private void initView(Context context) {
		View headerView = View.inflate(context, R.layout.view_header_layout, null);
		if(headerView != null) {
			addView(headerView);

			mTitleTxt = (TextView) headerView.findViewById(R.id.header_bar_title);
			mLeftTxt = (TextView) headerView.findViewById(R.id.header_bar_left_txt);
			mLeftImg = (ImageView) headerView.findViewById(R.id.header_bar_left_img);
			mRightTxt = (TextView) headerView.findViewById(R.id.header_bar_right_txt);
			mRightImg = (ImageView) headerView.findViewById(R.id.header_bar_right_img);
			
			mColorsLine = (ImageView) headerView.findViewById(R.id.actionbar_colors_line);
			
			mLeftLayout = (LinearLayout) headerView.findViewById(R.id.header_left_layout);
			mRightLayout = (LinearLayout) headerView.findViewById(R.id.header_right_layout);
		}

		if(mLeftLayout != null) {
			mLeftLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(mLeftListenner != null) {
						mLeftListenner.onClick();
					}
				}
			});
		}

		if(mRightLayout != null) {
			mRightLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
					if(mRightListenner != null) {
						mRightListenner.onClick();
					}
				}
			});
		}
	}

	/**
	 * 设置标题
	 * 
	 * @param txt
	 */
	public void setTitile(String txt) {
		mTitleTxt.setText(txt);
	}

	public void addLeftView() {
		mLeftLayout.setVisibility(View.VISIBLE);
	}
	
	public void addRightView() {
		mRightLayout.setVisibility(View.VISIBLE);
	}
	
	public void deleteLeftView() {
		mLeftLayout.setVisibility(View.GONE);
	}

	public void deleteRightView() {
		mRightLayout.setVisibility(View.GONE);
	}
	
	public void setLeftTxt(String txt) {
		mLeftLayout.setVisibility(View.VISIBLE);
		mLeftTxt.setVisibility(View.VISIBLE);
		mLeftTxt.setText(txt);
		mLeftImg.setVisibility(View.GONE);
		if(!TextUtils.isEmpty(txt) && txt.length() >= 4) {
			mLeftTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		}
	}
	
	public void setLeftTxt(String txt, int drawableId) {
		setLeftTxt(txt);
		Drawable drawable = getResources().getDrawable(drawableId);
		// / 这一步必须要做,否则不会显示.
		drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
		mLeftTxt.setCompoundDrawables(null, null, drawable, null);
	}

	/**
	 * 设置左边图片
	 * 
	 * @param resouceId
	 */
	public void setLeftImg(int resouceId) {
		mLeftLayout.setVisibility(View.VISIBLE);
		mLeftImg.setVisibility(View.VISIBLE);
		mLeftImg.setImageResource(resouceId);
		mLeftTxt.setVisibility(View.GONE);
	}

	/**
	 * 设置右边文字
	 * 
	 * @param txt
	 */
	public void setRightTxt(String txt) {
		mRightLayout.setVisibility(View.VISIBLE);
		mRightTxt.setVisibility(View.VISIBLE);
		mRightTxt.setText(txt);
		mRightImg.setVisibility(View.GONE);
		if(!TextUtils.isEmpty(txt) && txt.length() >= 4) {
			mRightTxt.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		}
	}

	/**
	 * 设置右边图片
	 * 
	 * @param resouceId
	 */
	public void setRightImg(int resouceId) {
		mRightLayout.setVisibility(View.VISIBLE);
		mRightImg.setVisibility(View.VISIBLE);
		mRightImg.setImageResource(resouceId);
		mRightTxt.setVisibility(View.GONE);
	}

	/**
	 * 隐藏底部那个彩色的线条
	 */
	public void hindBottomLine() {
		mColorsLine.setVisibility(View.GONE);
	}
	
	public void setOnLeftClickListenner(OnLeftClickListenner listenner) {
		mLeftListenner = listenner;
	}

	public void setOnRightClickListenner(OnRightClickListenner listenner) {
		mRightListenner = listenner;
	}

	public interface OnLeftClickListenner {
		public void onClick();
	}

	public interface OnRightClickListenner {
		public void onClick();
	}
}
