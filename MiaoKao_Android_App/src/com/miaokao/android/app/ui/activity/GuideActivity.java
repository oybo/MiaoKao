package com.miaokao.android.app.ui.activity;

import java.util.ArrayList;
import java.util.List;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.miaokao.android.app.R;
import com.miaokao.android.app.util.PreferenceUtils;

public class GuideActivity extends Activity {

	private static final String IS_GUIDE = "is_guide_key";
	
	private ViewPager mViewPager;
	private ImageView[] mPageViews;
	private Button mGoBT;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_guide);

		if(PreferenceUtils.getInstance().getBoolean(IS_GUIDE, false)) {
			goWelcome(false);
			return;
		}
		
		initView();
		initViewPager();
	}

	private void goWelcome(boolean isAnimation) {
		startActivity(new Intent(this, WelcomeActivity.class));
		if(isAnimation) {
			overridePendingTransition(R.anim.in_left, R.anim.in_right);
		}
		finish();
	}

	@SuppressWarnings("deprecation")
	private void initView() {
		ImageView b1 = (ImageView) findViewById(R.id.guide_page_1);
		ImageView b2 = (ImageView) findViewById(R.id.guide_page_2);
		ImageView b3 = (ImageView) findViewById(R.id.guide_page_3);
		ImageView b4 = (ImageView) findViewById(R.id.guide_page_4);
		ImageView b5 = (ImageView) findViewById(R.id.guide_page_5);
		mPageViews = new ImageView[]{ b1, b2, b3, b4, b5 };
		mPageViews[0].setSelected(true);
		
		mViewPager = (ViewPager) findViewById(R.id.guide_viewpager);

		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				if(arg0 == mPageViews.length - 1) {
					mGoBT.setVisibility(View.VISIBLE);
				} else {
					mGoBT.setVisibility(View.INVISIBLE);
				}
				
				for(ImageView iv : mPageViews) {
					iv.setSelected(false);
				}
				mPageViews[arg0].setSelected(true);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		
		mGoBT = (Button) findViewById(R.id.guide_go_welcome);
		mGoBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				PreferenceUtils.getInstance().putBoolen(IS_GUIDE, true);
				goWelcome(true);
			}
		});
	}

	private void initViewPager() {
		List<View> tempViewList = new ArrayList<>();

		tempViewList.add(getImageView(R.drawable.gui_1));
		tempViewList.add(getImageView(R.drawable.gui_2));
		tempViewList.add(getImageView(R.drawable.gui_3));
		tempViewList.add(getImageView(R.drawable.gui_4));
		tempViewList.add(getImageView(R.drawable.gui_5));

		mViewPager.setAdapter(new ViewPagerAdapter(tempViewList));
	}

	private ImageView getImageView(int resId) {
		ImageView imageView = new ImageView(this);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		imageView.setLayoutParams(params);

		imageView.setImageResource(resId);

		return imageView;
	}

	/**
	 * 创建 ViewPager 适配类
	 * 
	 * @author ouyangbo
	 * 
	 */
	class ViewPagerAdapter extends PagerAdapter {

		private List<View> mViews;

		public ViewPagerAdapter(List<View> mViews) {
			this.mViews = mViews;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView(mViews.get(position));
		}

		@Override
		public void finishUpdate(View view) {
		}

		@Override
		public int getCount() {
			return mViews.size();
		}

		@Override
		public View instantiateItem(View container, int position) {
			View view = mViews.get(position);
			((ViewPager) container).addView(view);
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {

		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View view) {

		}
	}
}
