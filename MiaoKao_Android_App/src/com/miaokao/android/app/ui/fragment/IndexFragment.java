package com.miaokao.android.app.ui.fragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.inteface.LoginStatusListenner;
import com.miaokao.android.app.recerver.MyRecerver;
import com.miaokao.android.app.ui.BaseFragment;
import com.miaokao.android.app.ui.activity.CourseOneActivity;
import com.miaokao.android.app.ui.activity.CourseThreeActivity;
import com.miaokao.android.app.ui.activity.CourseTwoActivity;
import com.miaokao.android.app.ui.activity.MainActivity.OnMainCallbackListenner;
import com.miaokao.android.app.ui.activity.PrepareActivity;
import com.miaokao.android.app.ui.activity.SignInActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;

public class IndexFragment extends BaseFragment implements OnClickListener, LoginStatusListenner {

	private static final int R_CODE = 10;
	private OnMainCallbackListenner mMainCallBackListenner;
	private PullToRefreshScrollView mRefreshScrollView;
	private TextView mGoDrivingTxt;
	private LinearLayout mServiceLayout;
	private TextView mPrepareStateTxt, mKmOneTxt, mKmTwoTxt, mKmThreeTxt, mKmFourTxt, mKmOkTxt;
	private ImageView mPrepareStateImage, mKmOneImage, mKmTwoImage, mKmThreeImage, mKmFourImage, mKmOkImage;
	private Order mOrder;
	private boolean isOne, isTwo, isThree, isFour, isFive, isOk;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_index, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
		boolean isLogin = AppContext.getInstance().mUser == null ? false : true;
		login(isLogin);
		initReceiver();
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubConstant.MAKE_SUCCESS_FINISH_KEY);
		getActivity().registerReceiver(receiver, filter);
	}
	
	// 检测当前是否有订单
	private void checkOrder() {
		if (mAppContext.mUser == null) {
			mGoDrivingTxt.setVisibility(View.VISIBLE);
			mServiceLayout.setVisibility(View.GONE);
			refreshTitle("喵考");
			return;
		}
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "order");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		mAppContext.netRequest(getActivity(), url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				final JSONArray jsonArray = jsonObject.optJSONArray("message");
				List<Order> orders = new ArrayList<>();
				PubUtils.analysisMyOrder(orders, jsonArray);
				if (orders.size() > 0) {
					int len = orders.size();
					for(int i=0; i<len; i++) {
						Order order = orders.get(i);
						if("1".equals(order.getStatus())) {
							getOrderState(order);
							return;
						}
					}
				} else {
					mGoDrivingTxt.setVisibility(View.VISIBLE);
					mServiceLayout.setVisibility(View.GONE);
					refreshTitle("喵考");
				}
				mRefreshScrollView.onRefreshComplete();
			}

			@Override
			public void responseError() {
				mRefreshScrollView.onRefreshComplete();
			}
		}, false, "checkOrder");
	}

	private void getOrderState(final Order order) {
		mOrder = order;
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "index");
		postData.put("mer_id", order.getMer_id());
		postData.put("order_no", order.getOrder_no());
		mAppContext.netRequest(getActivity(), url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				mRefreshScrollView.onRefreshComplete();
				if(jsonObject == null || "".equals(jsonObject.toString())) {
					mGoDrivingTxt.setVisibility(View.VISIBLE);
					mServiceLayout.setVisibility(View.GONE);
					refreshTitle("喵考");
					return;
				}
				mGoDrivingTxt.setVisibility(View.GONE);
				mServiceLayout.setVisibility(View.VISIBLE);
				initTopBarAll(R.id.index_common_actionbar, order.getMer_name(), "签到", new OnRightClickListenner() {
					@Override
					public void onClick() {
						// 签到
						signin();
					}
				});
				
				// 1 报名
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("sign_up");
					if(jsonArray != null && !"null".equals(jsonArray)) {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							String info = object.optString("info");
							mPrepareStateTxt.setText(info);
							String status = object.optString("status");
							isOne = "ok".equals(status) ? true : false;
							mPrepareStateImage.setSelected(isOne);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 2 科目一
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("course_1");
					if(jsonArray != null && !"null".equals(jsonArray)) {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							String info = object.optString("info");
							mKmOneTxt.setText(info);
							String status = object.optString("status");
							isTwo = "ok".equals(status) ? true : false;
							mKmOneImage.setSelected(isTwo);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 3 科目二
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("course_2");
					if(jsonArray != null && !"null".equals(jsonArray)) {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							String info = object.optString("info");
							mKmTwoTxt.setText(info);
							String status = object.optString("status");
							isThree = "ok".equals(status) ? true : false;
							mKmTwoImage.setSelected(isThree);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 4 科目三
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("course_3");
					if(jsonArray != null && !"null".equals(jsonArray)) {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							String info = object.optString("info");
							mKmThreeTxt.setText(info);
							String status = object.optString("status");
							isFour = "ok".equals(status) ? true : false;
							mKmThreeImage.setSelected(isFour);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 5 科目四
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("course_4");
					if(jsonArray != null && !"null".equals(jsonArray)) {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							String info = object.optString("info");
							mKmFourTxt.setText(info);
							String status = object.optString("status");
							isFive = "ok".equals(status) ? true : false;
							mKmFourImage.setSelected(isFive);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				// 6领取驾照
				try {
					JSONArray jsonArray = jsonObject.optJSONArray("course_5");
					if(jsonArray != null && !"null".equals(jsonArray)) {
						JSONObject object = jsonArray.getJSONObject(0);
						if(object != null && !"null".equals(object)) {
							String info = object.optString("info");
							mKmOkTxt.setText(info);
							String status = object.optString("status");
							isOk = "ok".equals(status) ? true : false;
							mKmOkImage.setSelected(isOk);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void responseError() {
				mRefreshScrollView.onRefreshComplete();
			}
		}, true, "getOrderState");
	}

	private void initView() {
		initTopBarOnlyTitle(R.id.index_common_actionbar, "喵考");

		mRefreshScrollView = (PullToRefreshScrollView) getActivity().findViewById(R.id.index_refresh_scrollview);
		mRefreshScrollView.setMode(Mode.DISABLED);
		mServiceLayout = (LinearLayout) getActivity().findViewById(R.id.index_reserved_service_layout);
		mPrepareStateTxt = (TextView) getActivity().findViewById(R.id.index_prepare_state_txt);
		mKmOneTxt = (TextView) getActivity().findViewById(R.id.index_km_one_state_txt);
		mKmTwoTxt = (TextView) getActivity().findViewById(R.id.index_km_two_state_txt);
		mKmThreeTxt = (TextView) getActivity().findViewById(R.id.index_km_three_state_txt);
		mKmFourTxt = (TextView) getActivity().findViewById(R.id.index_km_four_state_txt);
		mKmOkTxt = (TextView) getActivity().findViewById(R.id.index_km_ok_state_txt);

		mPrepareStateImage = (ImageView) getActivity().findViewById(R.id.index_prepare_state_iv);
		mKmOneImage = (ImageView) getActivity().findViewById(R.id.index_km_one_state_iv);
		mKmTwoImage = (ImageView) getActivity().findViewById(R.id.index_km_two_state_iv);
		mKmThreeImage = (ImageView) getActivity().findViewById(R.id.index_km_three_state_iv);
		mKmFourImage = (ImageView) getActivity().findViewById(R.id.index_km_four_state_iv);
		mKmOkImage = (ImageView) getActivity().findViewById(R.id.index_km_ok_state_iv);
		
		getActivity().findViewById(R.id.index_prepare_state_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.index_km_one_state_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.index_km_two_state_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.index_km_three_state_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.index_km_four_state_layout).setOnClickListener(this);
		getActivity().findViewById(R.id.index_km_ok_state_layout).setOnClickListener(this);

		mGoDrivingTxt = (TextView) getActivity().findViewById(R.id.index_go_driving_txt);
		mGoDrivingTxt.setOnClickListener(this);
		
		// 登录登出回调
		MyRecerver.mListenner.add(this);
		
		mRefreshScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
			@Override
			public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
				checkOrder();
			}
		});
	}

	private void signin() {
		Intent intent = new Intent(getActivity(), SignInActivity.class);
		startActivityForResult(intent, R_CODE);
	}

	@Override
	public void refreshFragment() {
		checkOrder();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.index_go_driving_txt:
			mMainCallBackListenner.onCurrentTab(1);
			break;
		case R.id.index_prepare_state_layout:
			// 报名
			if(!isOne) {
				Intent intent = new Intent(getActivity(), PrepareActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", (Serializable) mOrder);
				intent.putExtras(bundle);
				startActivityForResult(intent, R_CODE);
			}
			
			break;
		case R.id.index_km_one_state_layout:
			// 科目一
			if(!isTwo && isOne) {
				Intent intent = new Intent(getActivity(), CourseOneActivity.class);
				intent.putExtra("bill_txt", "科目一");
				intent.putExtra("bill_num", "1");
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", (Serializable) mOrder);
				intent.putExtras(bundle);
				startActivityForResult(intent, R_CODE);
			}
			break;
		case R.id.index_km_two_state_layout:
			// 科目二
			if(!isThree && isTwo) {
				Intent intent = new Intent(getActivity(), CourseTwoActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", (Serializable) mOrder);
				intent.putExtras(bundle);
				startActivityForResult(intent, R_CODE);
			}
			break;
		case R.id.index_km_three_state_layout:
			// 科目三
			if(!isFour && isThree) {
				Intent intent = new Intent(getActivity(), CourseThreeActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", (Serializable) mOrder);
				intent.putExtras(bundle);
				startActivityForResult(intent, R_CODE);
			}
			break;
		case R.id.index_km_four_state_layout:
			// 科目四
			if(!isFive && isFour) {
				Intent intent = new Intent(getActivity(), CourseOneActivity.class);
				intent.putExtra("bill_txt", "科目四");
				intent.putExtra("bill_num", "4");
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", (Serializable) mOrder);
				intent.putExtras(bundle);
				startActivityForResult(intent, R_CODE);
			}
			break;
		case R.id.index_km_ok_state_layout:
			// 领取驾照
			if(!isOk && isFive) {
				
			}
			break;

		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 刷新
		refreshFragment();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest("checkOrder");
		mAppContext.callRequest("getOrderState");
		getActivity().unregisterReceiver(receiver);
		MyRecerver.mListenner.remove(this);
	}

	@Override
	public void login(boolean isLogin) {
		if(isLogin) {
			mRefreshScrollView.setMode(Mode.PULL_FROM_START);
		} else {
			mRefreshScrollView.setMode(Mode.DISABLED);
			initTopBarOnlyTitle(R.id.index_common_actionbar, "喵考");
		}
		checkOrder();
	}
	
	@Override
	public void setCallBackListenner(OnMainCallbackListenner callbackListenner) {
		this.mMainCallBackListenner = callbackListenner;
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(PubConstant.MAKE_SUCCESS_FINISH_KEY.equals(action)) {
				// 刷新状态
				checkOrder();
			}
		}
	};

}
