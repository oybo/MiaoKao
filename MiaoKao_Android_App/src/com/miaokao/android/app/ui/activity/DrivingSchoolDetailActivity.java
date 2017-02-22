package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.DSchoolCourseAdapter;
import com.miaokao.android.app.adapter.ItemDiscountAdapter;
import com.miaokao.android.app.adapter.base.CommonAdapter.onInternalClickListener;
import com.miaokao.android.app.entity.DSchoolCourse;
import com.miaokao.android.app.entity.DSchoolDiscount;
import com.miaokao.android.app.entity.DrivingSchool;
import com.miaokao.android.app.entity.MerComment;
import com.miaokao.android.app.inteface.LoginStatusListenner;
import com.miaokao.android.app.map.GaodeMapActivity;
import com.miaokao.android.app.recerver.MyRecerver;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.DialogTips;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.ImageDialog;
import com.miaokao.android.app.widget.MListView;
import com.miaokao.android.app.widget.RatingBarView;
import com.miaokao.android.app.widget.RoundAngleImageView;
import com.miaokao.android.app.widget.SelectGradePopupWindow;
import com.nostra13.universalimageloader.core.ImageLoader;

public class DrivingSchoolDetailActivity extends BaseActivity implements OnClickListener, LoginStatusListenner {

	private Context mContext;
	private DrivingSchool mDrivingSchool;
	private RoundAngleImageView mIconImage;
	private TextView mNameTxt;
	private RatingBarView mRatingBarView;
	private TextView mTypeTxt;
	private TextView mYbmTxt;
	private TextView mPfTxt;
	private TextView mYplfTxt;
	private TextView mYtgTxt;
	private TextView mAddressTxt;
	private TextView mDistanceTxt;
	private TextView mSbssceTxt;
	private TextView mServiceTxt;
	private TextView mGjlxTxt;
	private TextView mInfoTxt;
	private SelectGradePopupWindow mPopupWindow;
	private List<DSchoolCourse> mDSchoolCourses;
	private MListView mDiscountListView;
	private MListView mCourseListView;
	private String mTradingCode;
	private DSchoolCourseAdapter mCourseAdapter;
	private List<MerComment> mComments;
	private Thread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_driving_scholl_detail);

		mContext = this;
		mDrivingSchool = (DrivingSchool) getIntent().getExtras().getSerializable("drivingSchool");
		
		initView();
		initCourseList();
		initDiscountList();
		initDetailData();
		getData();
		initReceriver();
	}

	private void initReceriver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubConstant.SUBMIT_ORDER_SUCCESS_FINISH_KEY);
		registerReceiver(receiver, filter);
	}

	/** 网络获取数据 */
	private void getData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_merchants_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "detail");
		postData.put("mer_id", mDrivingSchool.getMer_account());
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {

				mThread = new Thread() {
					@Override
					public void run() {
						super.run();
						PubUtils.getDrivingSchool(mDrivingSchool, jsonObject);
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								initDetailData();
							}
						});
					}
				};
				mThread.start();
			}

			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void initDetailData() {
		// 头像
		ImageLoader.getInstance().displayImage(mDrivingSchool.getMer_head_img(), mIconImage, AppContext.getInstance().getHeadImageOptions());
		// 名称
		mNameTxt.setText(mDrivingSchool.getMer_name());
		// 评分
		int rate = 0;
		try {
			rate = Integer.parseInt(mDrivingSchool.getMer_rate().substring(0, mDrivingSchool.getMer_rate().indexOf(".")));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		mRatingBarView.setRating(rate);
		// 特征
		StringBuffer stringBuffer = new StringBuffer();
		if("1".equals(mDrivingSchool.getIs_for_return())) {
			// 闪退
			stringBuffer.append("闪退/");
		}
		if("1".equals(mDrivingSchool.getIs_for_bankcard())) {
			// 刷卡
			stringBuffer.append("刷卡/");
		}
		if("1".equals(mDrivingSchool.getIs_for_invoice())) {
			// 发票
			stringBuffer.append("发票/");
		}
		if("1".equals(mDrivingSchool.getIs_for_shuttle())) {
			// 接送
			stringBuffer.append("接送/");
		}
		if("1".equals(mDrivingSchool.getIs_for_fenqi())) {
			// 分期
			stringBuffer.append("分批付款/");
		}
		String str = stringBuffer.toString();
		if(str.indexOf("/") == 0) {
			str = str.substring(1, str.length());
		}
		if(str.lastIndexOf("/") == str.length() - 1) {
			str = str.substring(0, str.lastIndexOf("/"));
		}
		mTypeTxt.setText(str);
		// 已报名学员
		mYbmTxt.setText((TextUtils.isEmpty(mDrivingSchool.getMer_member_num()) ? "0" : mDrivingSchool.getMer_member_num()) + "名");
		// 评分
		mPfTxt.setText((TextUtils.isEmpty(mDrivingSchool.getMer_rate()) ? "0" : mDrivingSchool.getMer_rate()) + "分");
		// 多人已点评
		mYplfTxt.setText((TextUtils.isEmpty(mDrivingSchool.getMer_comment_num()) ? "0" : mDrivingSchool.getMer_comment_num()) + "人已点评");
		// 每月通过
		mYtgTxt.setText((TextUtils.isEmpty(mDrivingSchool.getMer_finish_member()) ? "0" : mDrivingSchool.getMer_finish_member()) + "名");
		// 地址
		mAddressTxt.setText(mDrivingSchool.getMer_addr());
		// 距离
		double longitude = AppContext.getInstance().mAMapLocation.getLongitude();
		double latitude = AppContext.getInstance().mAMapLocation.getLatitude();

		mDistanceTxt.setText(PubUtils.Distance(longitude, latitude,
				Double.parseDouble(mDrivingSchool.getMer_longitude()),
				Double.parseDouble(mDrivingSchool.getMer_latitude())));
		// 配套服务
		// 驾校设施
		mSbssceTxt.setText(mDrivingSchool.getSs_service());
		// 服务
		mServiceTxt.setText(mDrivingSchool.getHd_service());
		// 工具路线
		mGjlxTxt.setText(mDrivingSchool.getRouter());
		// 课程报名
		if(null != mDrivingSchool.getdSchoolCourses()) {
			mDSchoolCourses.addAll(mDrivingSchool.getdSchoolCourses());
		}
		mCourseAdapter.notifyDataSetChanged();
		// 营业执照
		mTradingCode = mDrivingSchool.getMer_licence_pic();
		// 驾校评论
		mComments = new ArrayList<>();
		if(mDrivingSchool.getMerComments() != null) {
			mComments.addAll(mDrivingSchool.getMerComments());
		}
		// 驾校简介
		mInfoTxt.setText(mDrivingSchool.getMer_intro());
	}

	private void initView() {
		initTopBarAll(R.id.d_school_detail_common_actionbar, mDrivingSchool.getMer_name(), R.drawable.heart, new OnRightClickListenner() {
			
			@Override
			public void onClick() {
				// 判断是否登录
				// 需要登录
				if (!mAppContext.isLogin(DrivingSchoolDetailActivity.this)) {
					return;
				}
				// 到评论页面
				mPopupWindow = new SelectGradePopupWindow(DrivingSchoolDetailActivity.this, 1, OnClickListener);
				// 设置layout在PopupWindow中显示的位置
				mPopupWindow.showAtLocation(mIconImage, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); 
			}
		});

		// 头像
		mIconImage = (RoundAngleImageView) findViewById(R.id.d_s_icon_iv);
		// 名称
		mNameTxt = (TextView) findViewById(R.id.d_s_name_tv);
		// 评分
		mRatingBarView = (RatingBarView) findViewById(R.id.d_s_pf_rating);
		// 类别
		mTypeTxt = (TextView) findViewById(R.id.d_s_type_info_tv);
		// 已报名学员
		mYbmTxt = (TextView) findViewById(R.id.d_s_detail_ybm_txt);
		// 评分
		mPfTxt = (TextView) findViewById(R.id.d_s_detail_pf_txt);
		// 多人已点评
		mYplfTxt = (TextView) findViewById(R.id.d_s_detail_ypl_txt);
		// 每月通过
		mYtgTxt = (TextView) findViewById(R.id.d_s_detail_ytg_txt);
		// 地址
		mAddressTxt = (TextView) findViewById(R.id.d_s_detail_address_txt);
		// 距离
		mDistanceTxt = (TextView) findViewById(R.id.d_s_detail_distance_txt);
		// 配套服务
		// 驾校设施
		mSbssceTxt = (TextView) findViewById(R.id.d_s_detail_sbss_txt);
		// 服务
		mServiceTxt = (TextView) findViewById(R.id.d_s_detail_service_txt);
		// 工具路线
		mGjlxTxt = (TextView) findViewById(R.id.d_s_detail_gjlx_txt);
		mCourseListView = (MListView) findViewById(R.id.d_s_course_listview);
		mDiscountListView = (MListView) findViewById(R.id.d_s_discount_listview);
		// 驾校简介
		mInfoTxt = (TextView) findViewById(R.id.d_s_detail_info_txt);

		findViewById(R.id.d_s_detail_address_layout).setOnClickListener(this);
		findViewById(R.id.d_s_detail_look_coach_txt).setOnClickListener(this);
		findViewById(R.id.d_s_detail_license_txt).setOnClickListener(this);
		findViewById(R.id.d_s_detail_comment_txt).setOnClickListener(this);
		
		MyRecerver.mListenner.add(this);
	}

	private void initCourseList() {
		mDSchoolCourses = new ArrayList<>();
		mCourseAdapter = new DSchoolCourseAdapter(mContext, mDSchoolCourses, R.layout.item_d_s_detail_activity);
		mCourseListView.setAdapter(mCourseAdapter);

		// 课程报名 item 报名按钮事件
		mCourseAdapter.setOnInViewClickListener(R.id.item_d_s_detail_apply_bt, new onInternalClickListener() {
			@Override
			public void OnClickListener(View parentV, View v, Integer position, Object values) {
				DSchoolCourse schoolCourse = mDSchoolCourses.get(position);

				Intent intent = new Intent(mContext, ConfirmOrderActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("drivingSchool", (Serializable) mDrivingSchool);
				bundle.putSerializable("schoolCourse", (Serializable) schoolCourse);
				intent.putExtras(bundle);
				startActivity(intent);
			}

		});
		
		// 课程报名 item 班别按钮事件
		mCourseAdapter.setOnInViewClickListener(R.id.item_d_s_detail_class_bt, new onInternalClickListener() {
			@Override
			public void OnClickListener(View parentV, View v, Integer position, Object values) {
				DSchoolCourse schoolCourse = mDSchoolCourses.get(position);
				DialogTips dialogTips = new DialogTips(mContext, "详细说明", schoolCourse.getP_intro());
				dialogTips.show();
			}
			
		});
	}
	
	private void initDiscountList() {
		List<DSchoolDiscount> schoolDiscounts = mDrivingSchool.getdSchoolDiscounts();
		if (schoolDiscounts != null && schoolDiscounts.size() > 0) {
			mDiscountListView.setAdapter(new ItemDiscountAdapter(mContext, schoolDiscounts, R.layout.item_discount_activity));
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.d_s_detail_address_layout:
			// 查看位置
			Intent intent = new Intent(mContext, GaodeMapActivity.class);
			intent.putExtra("title", mDrivingSchool.getMer_name());
			intent.putExtra("latitude", mDrivingSchool.getMer_latitude());
			intent.putExtra("longitude", mDrivingSchool.getMer_longitude());
			startActivity(intent);
			break;
		case R.id.d_s_detail_look_coach_txt:
			// 查看教练
			intent =new Intent(mContext, CoachActivity.class);
			intent.putExtra("mer_id", mDrivingSchool.getMer_account());
			startActivity(intent);
			break;
		case R.id.d_s_detail_license_txt:
			// 查看驾校营业执照
			ImageDialog imageDialog = new ImageDialog(mContext, mTradingCode);
			imageDialog.show();
			break;
		case R.id.d_s_detail_comment_txt:
			// 查看驾校评论
			intent = new Intent(mContext, CommentActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("comment_list", (Serializable) mComments);
			intent.putExtra("mer_id", mDrivingSchool.getMer_account());
			intent.putExtras(bundle);
			startActivity(intent);
			break;

		}
	}
	
	private View.OnClickListener OnClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(mContext, TalkDrivingSchoolActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("comment_list", (Serializable) mComments);
			intent.putExtras(bundle);
			switch (v.getId()) {
			case R.id.pop_choose_comment_xh_txt:
				// 喜欢
				intent.putExtra("rate", "1");
				break;
			case R.id.pop_choose_comment_bxh_txt:
				// 不喜欢
				intent.putExtra("rate", "-1");
				break;
			}
			intent.putExtra("mer_id", mDrivingSchool.getMer_account());
			startActivity(intent);
			mPopupWindow.dismiss();
		}
	};

	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(PubConstant.SUBMIT_ORDER_SUCCESS_FINISH_KEY)) {
				finish();
			}
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
		MyRecerver.mListenner.remove(this);
	}

	@Override
	public void login(boolean isLogin) {
		// TODO Auto-generated method stub
		
	};
	
}
