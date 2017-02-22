/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.coracle_photopicker_library.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coracle_photopicker_library.CoracleManager;
import com.coracle_photopicker_library.R;
import com.coracle_photopicker_library.photoview.PhotoView;
import com.coracle_photopicker_library.photoview.PhotoViewAttacher.OnPhotoTapListener;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 图片详情页
 * 
 * @author ouyangbo
 * 
 */
public class PhotoViewActivity extends Activity implements OnClickListener {

	private Context mContext;
	private TextView mTitleTxt;
	private ViewPager mViewPager;
	private SamplePagerAdapter pagerAdapter;
	private int mIndex, mLenPage, mCount;
	private List<String> mPaths, mSelectPaths;
	private ImageButton mSelectBT;

	private CoracleManager mCoracleManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_view);

		mContext = this;
		mCoracleManager = CoracleManager.getInstance();

		initView();
		initViewPageAdapter();
	}

	@SuppressWarnings("unchecked")
	private void initViewPageAdapter() {
		Bundle bundle = getIntent().getExtras();
		mPaths = (List<String>) bundle.getSerializable("paths");
		mSelectPaths = (List<String>) bundle.getSerializable("selectPaths");
		mIndex = bundle.getInt("curIndex", 0);
		mCount = bundle.getInt("count", 1);

		mLenPage = mPaths.size();

		pagerAdapter = new SamplePagerAdapter(mPaths);
		mViewPager.setAdapter(pagerAdapter);
		mViewPager.setCurrentItem(mIndex);
		mViewPager.setOnPageChangeListener(new onPageChangeListener());

		mTitleTxt.setText((mIndex + 1) + "/" + mLenPage);

		if(mSelectPaths != null) {
			if (mSelectPaths.contains(mPaths.get(mIndex))) {
				mSelectBT.setImageResource(R.drawable.pictures_selected);
			} else {
				mSelectBT.setImageResource(R.drawable.picture_unselected);
			}
		}

		if (mCount == 1) {
			mSelectBT.setVisibility(View.GONE);
		}
	}

	private void initView() {
		mTitleTxt = (TextView) findViewById(R.id.main_photo_look_title);
		mSelectBT = (ImageButton) findViewById(R.id.main_photo_look_select);
		mViewPager = (ViewPager) findViewById(R.id.main_photo_viewpager);
		mViewPager.setOffscreenPageLimit(2);

		findViewById(R.id.main_photo_look_back).setOnClickListener(this);
		mSelectBT.setOnClickListener(this);
	}

	class onPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageSelected(int arg0) {
			mIndex = arg0;

			mTitleTxt.setText((mIndex + 1) + "/" + mLenPage);

			if(mSelectPaths != null) {
				String path = mPaths.get(mIndex);

				if (mSelectPaths.contains(path)) {
					mSelectBT.setImageResource(R.drawable.pictures_selected);
				} else {
					mSelectBT.setImageResource(R.drawable.picture_unselected);
				}
			}
		}

	}

	class SamplePagerAdapter extends PagerAdapter {
		List<String> items = null;

		public SamplePagerAdapter(List<String> resources) {
			this.items = resources;
		}

		@Override
		public int getCount() {
			return items.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			final PhotoView photoView = new PhotoView(container.getContext());
			String path = items.get(position);

//			ImageLoader.getInstance(3, Type.LIFO).loadImage(path, (ImageView) photoView);

			ImageLoader.getInstance().displayImage(path, photoView);
			
			photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View view, float x, float y) {
					finish();
				}
			});

			container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			return photoView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			PhotoView photoView = (PhotoView) object;
			// photoView.gc();
			container.removeView(photoView);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.main_photo_look_back) {

			finish();
		} else if (v.getId() == R.id.main_photo_look_select) {
			CoracleManager.onSelectPictureListenner listenner = mCoracleManager.getSelectListenner();

			if(mSelectPaths != null) {
				String path = mPaths.get(mIndex);

				if (mSelectPaths.contains(path)) {

					mSelectPaths.remove(path);
					mSelectBT.setImageResource(R.drawable.picture_unselected);
					if(listenner != null) {
						listenner.del(path);
					}
				} else {
					if (mSelectPaths.size() >= mCount) {
						Toast.makeText(mContext, "最多选择" + mCount + "张图片!", Toast.LENGTH_SHORT).show();
						return;
					}
					mSelectPaths.add(path);
					mSelectBT.setImageResource(R.drawable.pictures_selected);
					if(listenner != null) {
						listenner.add(path);
					}
				}
			}
		}
	}

}
