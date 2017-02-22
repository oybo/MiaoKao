package com.miaokao.android.app.widget;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Dialog;
import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.base.CommonAdapter;
import com.miaokao.android.app.adapter.base.ViewHolder;
import com.miaokao.android.app.entity.PlayTour;
import com.miaokao.android.app.util.FixedSpeedScroller;
import com.miaokao.android.app.util.PlayTourComparator;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.ToastFactory;
import com.miaokao.android.app.widget.MGirdView;
import com.miaokao.android.app.widget.NoScrollViewPager;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @TODO 弹出评论 和 打赏 Dialog
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2016-1-30 上午11:07:49
 */
public class LastCommentDialog extends Dialog implements android.view.View.OnClickListener {

	private Context mContext;
	private String mData;
	private RoundAngleImageView mHeadImage;
	private TextView mNameTxt;
	private TextView mTimeTxt;
	private String coach_id, r_date, time_node;
	private MyPlayTourAdapter mAdapter;
	private List<PlayTour> mPlayTours;
	private TextView mPTNameTxt, mPTPriceTxt;
	private int mPosition = -1;
	private int mPTCount;
	private PlayTour mPlayTour;
	private ImageView mPage1, mPage2;
	private NoScrollViewPager mViewPager;
	private CommentCallBack mCommentCallBack;

	public LastCommentDialog(Context context, String data) {
		super(context, R.style.signin_dialog_style);

		mContext = context;
		mData = data;
		init();
	}

	private void init() {
		setContentView(R.layout.activity_operation_view);

		Window window = getWindow();
		window.getAttributes().width = LayoutParams.MATCH_PARENT;
		window.getAttributes().height = LayoutParams.MATCH_PARENT;
		window.setWindowAnimations(R.style.anim_activity_dialog_dir);

		initView();
		initData();
		getPlayTours();
	}

	private void initView() {
		mPage1 = (ImageView) findViewById(R.id.operation_page_1);
		mPage2 = (ImageView) findViewById(R.id.operation_page_2);
		mPage1.setSelected(false);
		mPage2.setSelected(true);
		
		mViewPager = (NoScrollViewPager) findViewById(R.id.operation_viewpager);
		// 禁止左右滑动
		mViewPager.setNoScroll(true);
		// 修改viewpager滑动
		initScroll();
		// 评论
		View commentView = View.inflate(mContext, R.layout.activity_last_comment, null);
		mHeadImage = (RoundAngleImageView) commentView.findViewById(R.id.last_comment_icon_image);
		mNameTxt = (TextView) commentView.findViewById(R.id.last_comment_name_txt);
		mTimeTxt = (TextView) commentView.findViewById(R.id.last_comment_time_txt);
		commentView.findViewById(R.id.last_comment_hp_layout).setOnClickListener(this);
		commentView.findViewById(R.id.last_comment_zp_layout).setOnClickListener(this);
		commentView.findViewById(R.id.last_comment_cp_layout).setOnClickListener(this);

		// 打赏
		View playTourView = View.inflate(mContext, R.layout.activity_play_tour_view, null);
		playTourView.findViewById(R.id.play_tour_subtract_bt).setOnClickListener(this);
		playTourView.findViewById(R.id.play_tour_ok_bt).setOnClickListener(this);
		playTourView.findViewById(R.id.play_tour_add_bt).setOnClickListener(this);
		playTourView.findViewById(R.id.play_tour_exit).setOnClickListener(this);
		mPTNameTxt = (TextView) playTourView.findViewById(R.id.play_tour_name);
		mPTPriceTxt = (TextView) playTourView.findViewById(R.id.play_tour_price);
		MGirdView girdView = (MGirdView) playTourView.findViewById(R.id.play_tour_gridview);
		mPlayTours = new ArrayList<>();
		mAdapter = new MyPlayTourAdapter(mContext, mPlayTours, R.layout.item_play_tour_activity);
		girdView.setAdapter(mAdapter);
		girdView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if (mPosition == arg2) {
					return;
				}
				mPosition = arg2;
				for (PlayTour playTour : mPlayTours) {
					playTour.setSelect(false);
				}
				mPlayTour = mPlayTours.get(arg2);
				mPlayTour.setSelect(true);
				mAdapter.notifyDataSetChanged();

				mPTCount = 1;
				calculate();
			}
		});

		// 初始化ViewPager
		List<View> tempViewList = new ArrayList<>();
		tempViewList.add(commentView);
		tempViewList.add(playTourView);

		mViewPager.setAdapter(new ViewPagerAdapter(tempViewList));
	}

	private void calculate() {
		mPTNameTxt.setText(mPTCount + "个" + mPlayTour.getName());
		mPTPriceTxt.setText("￥" + (mPTCount * Float.parseFloat(mPlayTour.getPrice())));
	}

	/**
	 * 网络获取打赏物品
	 */
	private void getPlayTours() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_index_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "prize_icon");
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if (jsonArray != null) {
					int len = jsonArray.length();
					for (int i = 0; i < len; i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						PlayTour playTour = new PlayTour();
						try {
							playTour.setIcon(object.getString("icon"));
							playTour.setId(object.getString("id"));
							playTour.setName(object.getString("name"));
							playTour.setIcon(object.getString("icon"));
							playTour.setPrice(object.getString("price"));
							playTour.setRate(object.getString("rate"));
						} catch (JSONException e) {
							e.printStackTrace();
						}

						mPlayTours.add(playTour);
					}
					// 排序
					Collections.sort(mPlayTours, new PlayTourComparator());
					// 默认选中第一个
					mPlayTour = mPlayTours.get(0);
					mPlayTour.setSelect(true);
					// 刷新列表
					mAdapter.notifyDataSetChanged();
					mPTCount = 1;
					calculate();
				}
			}

			@Override
			public void responseError() {

			}
		}, true, "getPlayTours");
	}

	private void initData() {
		try {
			JSONObject jsonObject = new JSONObject(mData);

			ImageLoader.getInstance().displayImage(jsonObject.optString("head_img"), mHeadImage,
					AppContext.getInstance().getHeadImageOptions());
			mNameTxt.setText(jsonObject.optString("name"));
			mTimeTxt.setText(jsonObject.optString("r_date"));

			coach_id = jsonObject.optString("coach_id");
			r_date = jsonObject.optString("r_date");
			time_node = jsonObject.optString("time_node");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.last_comment_hp_layout:
			// 好评
			comment("1");
			break;
		case R.id.last_comment_zp_layout:
			// 中评
			comment("0");
			break;
		case R.id.last_comment_cp_layout:
			// 差评
			comment("-1");
			break;
		case R.id.play_tour_subtract_bt:
			// 减数量
			if (mPTCount == 0) {
				return;
			}
			mPTCount--;
			calculate();
			break;
		case R.id.play_tour_add_bt:
			// 加数量
			mPTCount++;
			calculate();
			break;
		case R.id.play_tour_ok_bt:
			// 打赏
			if (mPTCount == 0) {
				return;
			}
			JSONObject jsonObject = new JSONObject();
			try {
				jsonObject.put("price", mPTCount * Float.parseFloat(mPlayTour.getPrice()));
				jsonObject.put("subject", mPlayTour.getName());
				jsonObject.put("body", mPTCount);
				jsonObject.put("prize_id", mPlayTour.getId());
				jsonObject.put("coach_id", coach_id);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mCommentCallBack.pay(jsonObject.toString());
			break;
		case R.id.play_tour_exit:
			// 退出
			dismiss();
			break;
		}
	}

	private void comment(String rate) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "comment_course");
		postData.put("coach_id", coach_id);
		postData.put("r_date", r_date);
		postData.put("rate", rate);
		postData.put("time_node", time_node);
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("message");
					if (jsonArray != null || !"null".equals(jsonArray)) {
						JSONObject object = jsonArray.getJSONObject(0);
						String result = object.optString("result");
						if ("ok".equals(result)) {
							// 跳转到打赏
							mViewPager.setCurrentItem(1);
							mPage1.setSelected(true);
							mPage2.setSelected(false);
						} else {
							ToastFactory.getToast(mContext, "评论失败").show();
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void responseError() {

			}
		}, true, "sunbitComment");
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

	class MyPlayTourAdapter extends CommonAdapter<PlayTour> {

		public MyPlayTourAdapter(Context context, List<PlayTour> mDatas, int itemLayoutId) {
			super(context, mDatas, itemLayoutId);
		}

		@Override
		public void convert(ViewHolder helper, PlayTour item) {

			ImageView image = helper.getView(R.id.item_play_tour_image);
			ImageLoader.getInstance().displayImage(item.getIcon(), image, AppContext.getInstance().getOptions());

			// 是否选中
			ImageView select = helper.getView(R.id.item_play_tour_bt);
			if (item.isSelect()) {
				select.setSelected(true);
			} else {
				select.setSelected(false);
			}
		}

	}

	public void setonPayListenner(CommentCallBack callBack) {
		mCommentCallBack = callBack;
	}
	
	public interface CommentCallBack {
		public void pay(String data);
	}
}
