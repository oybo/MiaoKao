package com.miaokao.android.app.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import com.miaokao.android.app.R;
import com.miaokao.android.app.util.PubUtils;

/**
 * 自定义星级评分控件
 * @author ouyangbo
 *
 */
public class RatingBarView extends View {

	private static final int RATING_COUNT = 5;
	private Bitmap mSelectedBmp, mNolmarBmp;

	private int mRatingBarSize;

	public RatingBarView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	@SuppressLint("NewApi")
	public RatingBarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);

		int width = 5;
		/** 获得我们所定义的自定义样式属性 */
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RatingBarView, defStyleAttr, 0);
		int n = a.getIndexCount();
		for (int i = 0; i < n; i++) {
			int attr = a.getIndex(i);
			switch (attr) {
			case R.styleable.RatingBarView_rating_size:
				mRatingBarSize = a.getInteger(attr, 5);
				break;
			case R.styleable.RatingBarView_rating_width:

				int tempWidth = a.getInteger(attr, 5);
				width = PubUtils.dip2px(context, tempWidth);
				break;
			}

		}
		a.recycle();

		mSelectedBmp = BitmapFactory.decodeResource(getResources(), R.drawable.room_rating_bar_press);
		mNolmarBmp = BitmapFactory.decodeResource(getResources(), R.drawable.room_rating_bar_nolmar);

		mSelectedBmp = PubUtils.big(mSelectedBmp, width, width);
		mNolmarBmp = PubUtils.big(mNolmarBmp, width, width);

		setWillNotDraw(false);  

	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mRatingBarSize > 5) {
			mRatingBarSize = 5;
		}

		int len = -1;
		int s = RATING_COUNT - mRatingBarSize;

		for (int i = 0; i < mRatingBarSize; i++) {
			len++;
			canvas.drawBitmap(mSelectedBmp, len * PubUtils.dip2px(getContext(), 12), 0, null);
		}

		for (int i = 0; i < s; i++) {
			len++;
			canvas.drawBitmap(mNolmarBmp, len * PubUtils.dip2px(getContext(), 12), 0, null);
		}

	}

	public void setRating(int rating) {
		
		mRatingBarSize = rating;
		
		invalidate();
	}
	
}
