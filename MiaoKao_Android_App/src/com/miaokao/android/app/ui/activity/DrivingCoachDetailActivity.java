package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coracle_photopicker_library.CoracleManager;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.VideoPlayUtil;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.ObservableScrollView;
import com.miaokao.android.app.widget.ObservableScrollView.ScrollViewListener;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.miaokao.android.app.widget.SharePopupWindow;
import com.miaokao.android.app.widget.SharePopupWindow.DissListenner;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 学车页面
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-3-7 下午3:24:47
 */
public class DrivingCoachDetailActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private SharePopupWindow mSharePopupWindow;
	private Coach mCoach;
	private ViewPager mViewPager;
	private RoundAngleImageView mHeadImage;
	private TextView mNameTxt;
	private TextView mInfoTxt;
	private TextView mGradeTxtTxt;
	private TextView mHGradeTxt, mZGradeTxt, mCGradeTxt;
	private TextView mHpTxt, mZpTxt, mCpTxt;
	private LinearLayout mHpLayout, mZpLayout, mCpLayout;
	private TextView mTypeTxt, mAddressTxt, mPriceTxt, mKm2Txt, mKm3Txt, mCommentTxt;
	private ImageView[] mPages;
	private JSONObject mDataJsonObject;
	private boolean mActivityHasFocus;
	private ObservableScrollView mScrollView;
	private VideoPlayUtil mPlayUtil = new VideoPlayUtil();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driving_coach_detail);

		mContext = this;
		mCoach = (Coach) getIntent().getExtras().getSerializable("coach");

		initView();
		// 初始化视频和个人信息
		initViewPager();
		initData();
	}

	private void initData() {
		// 头像
		ImageLoader.getInstance().displayImage(mCoach.getHead_img(), mHeadImage,
				AppContext.getInstance().getHeadImageOptions());
		// 姓名
		mNameTxt.setText(mCoach.getName());
		// 介绍
		mInfoTxt.setText(mCoach.getIntro());
		// 综合评分
		mGradeTxtTxt.setText(mCoach.getRate());
		// 好差评分数
		final String h_c = mCoach.getH_comment();
		final String z_c = mCoach.getM_comment();
		final String c_c = mCoach.getL_comment();
		mHGradeTxt.setText("好评(" + (TextUtils.isEmpty(h_c) ? "0" : h_c) + ")");
		mZGradeTxt.setText("中评(" + (TextUtils.isEmpty(z_c) ? "0" : z_c) + ")");
		mCGradeTxt.setText("差评(" + (TextUtils.isEmpty(c_c) ? "0" : c_c) + ")");
		// 获取其他信息
		getCoachInfo();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.d_c_detail_select_bt:
			if (mDataJsonObject != null) {
				Intent intent = new Intent(mContext, ConfirmOrderByCoachActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("coach", (Serializable) mCoach);
				bundle.putString("date", mDataJsonObject.toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		case R.id.coach_detail_icon_image:
			// 查看头像
			if (!TextUtils.isEmpty(mCoach.getHead_img())) {
				List<String> images = new ArrayList<>();
				images.add(mCoach.getHead_img());
				CoracleManager.getInstance().browserImage(mContext, images, 0);
			}
			break;
		}
	}

	private void initViewPager() {
		mPages = new ImageView[] { (ImageView) findViewById(R.id.coach_viewpager_page_1),
				(ImageView) findViewById(R.id.coach_viewpager_page_2) };
		mPages[0].setSelected(false);
		List<View> views = new ArrayList<>();
		if (!TextUtils.isEmpty(mCoach.getCoach_video())) {
			findViewById(R.id.coach_viewpager_page_layout).setVisibility(View.VISIBLE);
			// 视频
			View videoView = View.inflate(mContext, R.layout.view_coach_detail_viewpager_video, null);
			views.add(videoView);
			// 封装了播放类
			mPlayUtil.init(mContext, videoView, mCoach.getCoach_video());
		}
		// 教练信息
		View infoView = View.inflate(mContext, R.layout.view_coach_detail_viewpager_info, null);
		views.add(infoView);
		mViewPager.setAdapter(new ViewPagerAdapter(views));

		mHeadImage = (RoundAngleImageView) infoView.findViewById(R.id.coach_detail_icon_image);
		mNameTxt = (TextView) infoView.findViewById(R.id.coach_detail_name_txt);
		mInfoTxt = (TextView) infoView.findViewById(R.id.coach_detail_user_info_txt);

		mHeadImage.setOnClickListener(this);
		mViewPager.addOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				mPages[0].setSelected(true);
				mPages[1].setSelected(true);
				mPages[arg0].setSelected(false);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	private void initView() {
		initTopBarAll(R.id.d_coach_detail_common_actionbar, "教练名片", R.drawable.gengduo, new OnRightClickListenner() {

			@Override
			public void onClick() {
				// 点击弹出分享
				if (mDataJsonObject == null) {
					return;
				}
				mPlayUtil.onPause();
				
				String price = "";
				JSONArray productArray = mDataJsonObject.optJSONArray("product");
				int len = productArray.length();
				if (productArray != null && len > 0) {
					try {
						JSONObject productObject = productArray.getJSONObject(len - 1);
						price = productObject.optString("p_price");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

				String title = "我是" + mCoach.getName() + "教练，在喵考找我学车只要" + price + "元";
				String content = "您好，" + mCoach.getName() + "教练是经过喵考认证的教练，服务态度好，技术娴熟...";
				String imageUrl = mCoach.getHead_img();
				String url = "http://www.mewkao.com/wx_pay/example/c.php?s=" + mCoach.getMobile() + "&n="
						+ mCoach.getName() + "&p=" + price;

				mSharePopupWindow = new SharePopupWindow(mContext, getWindow(), title, content, imageUrl, url);
				mSharePopupWindow.showAtLocation(mHeadImage, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
				mSharePopupWindow.setDissListenner(new DissListenner() {
					@Override
					public void dissmiss() {
						mSharePopupWindow.backgroundAlpha(getWindow(), 1f);
						mPlayUtil.onResume();
					}
				});
			}
		});

		mViewPager = (ViewPager) findViewById(R.id.d_c_detail_select_viewpager);

		mGradeTxtTxt = (TextView) findViewById(R.id.coach_detail_grade_txt);
		mHGradeTxt = (TextView) findViewById(R.id.coach_detail_h_grade_txt);
		mZGradeTxt = (TextView) findViewById(R.id.coach_detail_z_grade_txt);
		mCGradeTxt = (TextView) findViewById(R.id.coach_detail_c_grade_txt);
		mHpLayout = (LinearLayout) findViewById(R.id.coach_detail_hp_layout);
		mZpLayout = (LinearLayout) findViewById(R.id.coach_detail_zp_layout);
		mCpLayout = (LinearLayout) findViewById(R.id.coach_detail_cp_layout);
		mHpTxt = (TextView) findViewById(R.id.coach_detail_hp_txt);
		mZpTxt = (TextView) findViewById(R.id.coach_detail_zp_txt);
		mCpTxt = (TextView) findViewById(R.id.coach_detail_cp_txt);

		mTypeTxt = (TextView) findViewById(R.id.d_coach_detail_sclx_txt);
		mAddressTxt = (TextView) findViewById(R.id.d_coach_detail_skdd_txt);
		mPriceTxt = (TextView) findViewById(R.id.d_coach_detail_kcfy_txt);
		mKm2Txt = (TextView) findViewById(R.id.d_coach_detail_kme_txt);
		mKm3Txt = (TextView) findViewById(R.id.d_coach_detail_mks_txt);
		mCommentTxt = (TextView) findViewById(R.id.d_coach_detail_xyqj_txt);

		findViewById(R.id.d_c_detail_select_bt).setOnClickListener(this);
		
		mScrollView = (ObservableScrollView) findViewById(R.id.d_coach_detail_scrollview);
		mScrollView.setScrollViewListener(new ScrollViewListener() {
			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
				if(y != oldy) {
					// 滑动的时候隐藏播放进度条
					mPlayUtil.hide();
				}
			}
		});
	}

	private void getCoachInfo() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "detail");
		postData.put("coach_id", mCoach.getAccount());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				mDataJsonObject = jsonObject;
				// 基础信息
				JSONArray detailArray = jsonObject.optJSONArray("detail");
				if (detailArray != null && detailArray.length() > 0) {
					try {
						JSONObject detailObject = detailArray.getJSONObject(0);
						if (detailObject != null && !"null".equals(detailObject)) {
							// 授课类型
							String type = detailObject.optString("type");
							mTypeTxt.setText(type);
							// 授课地点
							mAddressTxt.setText(mCoach.getMer_name());
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				// 课程信息
				JSONArray productArray = jsonObject.optJSONArray("product");
				int len = productArray.length();
				if (productArray != null && len > 0) {
					try {
						JSONObject productObject = productArray.getJSONObject(len - 1);
						if (productObject != null && !"null".equals(productObject)) {
							// 课程/费用
							String p_name = productObject.optString("p_name");
							String p_price = productObject.optString("p_price");
							mPriceTxt.setText(p_name + " / " + p_price);
							// 科目二
							mKm2Txt.setText(productObject.optString("km2") + "课时");
							// 科目三
							mKm3Txt.setText(productObject.optString("km2") + "课时");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				// 学员评价
				mCommentTxt.setText("累计评论(" + mCoach.getComment_num() + ")");
				// 教练认证

			}

			@Override
			public void responseError() {

			}
		}, false, getClass().getName());
	}

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
	
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if(hasFocus && !mActivityHasFocus) {
			try {
				int txtWidth = mHpTxt.getWidth();
				if (txtWidth == 0) {
					txtWidth = mHpLayout.getWidth();
				}
				int txtHeight = mHpTxt.getHeight();
				if (txtHeight == 0) {
					txtHeight = mHpLayout.getHeight();
				}

				final String h_c = mCoach.getH_comment();
				final String z_c = mCoach.getM_comment();
				final String c_c = mCoach.getL_comment();
				
				// 这里设置各评论比例
				int hp = Integer.parseInt(h_c);
				int zp = Integer.parseInt(z_c);
				int cp = Integer.parseInt(c_c);
				int blLen = hp + zp + cp;
				
				LinearLayout.LayoutParams params = null;
				if (blLen == 0) {
					// 如果3个评价都是0，则都全长度
					params = new LinearLayout.LayoutParams(txtWidth, txtHeight);
					mHpTxt.setLayoutParams(params);
					mZpTxt.setLayoutParams(params);
					mCpTxt.setLayoutParams(params);
					mHpLayout.setLayoutParams(params);
					mZpLayout.setLayoutParams(params);
					mCpLayout.setLayoutParams(params);
					return;
				}
				
				txtWidth = txtWidth / blLen;
				
				params = new LinearLayout.LayoutParams(hp * txtWidth, txtHeight);
				mHpTxt.setLayoutParams(params);

				params = new LinearLayout.LayoutParams(zp * txtWidth, txtHeight);
				mZpTxt.setLayoutParams(params);

				params = new LinearLayout.LayoutParams(cp * txtWidth, txtHeight);
				mCpTxt.setLayoutParams(params);

				params = new LinearLayout.LayoutParams((hp + zp + cp) * txtWidth, txtHeight);
				mHpLayout.setLayoutParams(params);
				mZpLayout.setLayoutParams(params);
				mCpLayout.setLayoutParams(params);
				
				mActivityHasFocus = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mPlayUtil.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mPlayUtil.onResume();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		mPlayUtil.onStart();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mPlayUtil.onStop();
	}

}
