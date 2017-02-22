package com.miaokao.android.app.util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.TopADImage;
import com.miaokao.android.app.widget.MGirdView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * @TODO 轮播图片 viewpager 封装 工具类
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-20 下午1:03:53
 */
public class ViewPagerSlideUtils {

	private ViewPager mViewPager;
	/** 当前滑动页 */
	private int currentItem = 0;
	private boolean isStopSlide;
	// 定时任务
	private ScheduledExecutorService scheduledExecutorService;
	private MGirdView mPageGirdView;
	private PageAdapter mPageAdapter;
	private OnItemSlideOnclickListenner mItemSlideOnclickListenner;

	@SuppressLint("HandlerLeak")
	private Handler viewHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mViewPager.setCurrentItem(currentItem);
		};
	};

	public void init(View view, final Context context, List<TopADImage> topADImages, OnItemSlideOnclickListenner itemSlideOnclickListenner) {
		mItemSlideOnclickListenner = itemSlideOnclickListenner;
		mViewPager = (ViewPager) view.findViewById(R.id.view_slide_viewpager);
		mPageGirdView = (MGirdView) view.findViewById(R.id.view_slide_gridview);
		// 反射更改viewpager的滑动速度
		initScroll();
		mViewPager.setAdapter(new ViewPagerSwitAdapter(context, topADImages));
		mViewPager.addOnPageChangeListener(new MyOnPageChangeListener());
		mViewPager.setCurrentItem(100 * topADImages.size());
		// 监听ViewPager 触摸时停止播放，抬起开始
		mViewPager.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					// 触摸按下
					viewHandler.removeCallbacks(myRunnable);
					isStopSlide = true;
					break;
				case MotionEvent.ACTION_UP:
					// 触摸松开，延迟个500毫秒吧
					viewHandler.postDelayed(myRunnable, 800);
					break;

				}
				return false;
			}
		});
		// 
		if(topADImages.size() > 1) {
			final int len = topADImages.size();
			mPageGirdView.setNumColumns(len);
			mPageAdapter = new PageAdapter(context, len);
			mPageGirdView.setAdapter(mPageAdapter);
			// 自适应长度
			mPageGirdView.post(new Runnable() {
				@Override
				public void run() {
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(PubUtils.dip2px(context,
							20 * len), PubUtils.dip2px(context, 20));
					params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
					params.addRule(RelativeLayout.CENTER_HORIZONTAL);
					params.setMargins(0, 0, 0, PubUtils.dip2px(context, 5));
					mPageGirdView.setLayoutParams(params);
				}
			});
		}
	}

	// 反射更改viewpager的滑动速度
	private void initScroll() {
		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			FixedSpeedScroller scroller = new FixedSpeedScroller(mViewPager.getContext());
			mScroller.set(mViewPager, scroller);
		} catch (Exception e) {
		}
	}

	private Runnable myRunnable = new Runnable() {

		@Override
		public void run() {
			isStopSlide = false;
		}
	};

	/** 执行轮播图切换任务 */
	private class SlideShowTask implements Runnable {

		@Override
		public void run() {
			synchronized (mViewPager) {
				if (!isStopSlide) {
					currentItem++;
					viewHandler.obtainMessage().sendToTarget();
				}
			}
		}

	}

	public void setStopSlide(boolean isStopSlide) {
		this.isStopSlide = isStopSlide;
	}

	/**
	 * 开始轮播图切换
	 */
	public void startPlay() {
		scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
		scheduledExecutorService.scheduleAtFixedRate(new SlideShowTask(), 2000, 3000, TimeUnit.MILLISECONDS);
	}

	/**
	 * 停止轮播图切换
	 */
	public void stopPlay() {
		scheduledExecutorService.shutdown();
	}

	/**
	 * ViewPager的监听器 当ViewPager中页面的状态发生改变时调用
	 * 
	 */
	private class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int pos) {
			currentItem = pos;
			
			if(mPageAdapter != null) {
				mPageAdapter.notifyDataSetChanged();
			}
		}

	}

	private class ViewPagerSwitAdapter extends PagerAdapter {

		private Context mContext;
		private List<TopADImage> mTopADImages;

		public ViewPagerSwitAdapter(Context context, List<TopADImage> topADImages) {
			this.mContext = context;
			this.mTopADImages = topADImages;
		}

		@Override
		public int getCount() {
			int len = mTopADImages.size();
			if (len == 0) {
				return 0;
			} else if (len == 1) {
				return 1;
			}
			return Integer.MAX_VALUE;
		}

		@Override
		public void destroyItem(View container, int position, Object object) {
			ImageView view = (ImageView) object;
			((ViewPager) container).removeView(view);
		}

		@Override
		public View instantiateItem(View container, int position) {
			if (mTopADImages.size() > 1) {
				position = position % mTopADImages.size();
			}

			TopADImage topADImage = mTopADImages.get(position);

			ImageView view = new ImageView(mContext);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			view.setLayoutParams(params);
			view.setScaleType(ScaleType.CENTER);
			ImageLoader.getInstance().displayImage(topADImage.getAd_img_url(), view,
					AppContext.getInstance().getAllOptions(R.drawable.image_location_loading), new ImageLoadingListener() {
						
						@Override
						public void onLoadingStarted(String arg0, View arg1) {
							
						}
						
						@Override
						public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
							
						}
						
						@Override
						public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
							((ImageView) arg1).setScaleType(ScaleType.FIT_XY);
						}
						
						@Override
						public void onLoadingCancelled(String arg0, View arg1) {
							
						}
					});

			((ViewPager) container).addView(view, RelativeLayout.LayoutParams.MATCH_PARENT,
					RelativeLayout.LayoutParams.MATCH_PARENT);

			final int pos = position;
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(mItemSlideOnclickListenner != null) {
						mItemSlideOnclickListenner.onClick(pos);
					}
				}
			});
			
			return view;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}
	
	private class PageAdapter extends BaseAdapter {

		private Context mContext;
		private int mLen;
		
		public PageAdapter(Context context, int len) {
			this.mContext = context;
			this.mLen = len;
		}
		
		@Override
		public int getCount() {
			return mLen;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null) {
				ImageView pageView = new ImageView(mContext);
				convertView = pageView;
			}
			
			ImageView pageView = (ImageView) convertView;
			pageView.setImageResource(R.drawable.page_focused_normal);
			
			int temp = currentItem % mLen;
			if(temp == position) {
				pageView.setImageResource(R.drawable.page_focused_selected);
			}
			
			return convertView;
		}
		
	}

	public interface OnItemSlideOnclickListenner {
		public void onClick(int position);
	}
}
