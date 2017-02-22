package com.miaokao.android.app.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.recerver.MyRecerver;
import com.miaokao.android.app.ui.BaseFragment;
import com.miaokao.android.app.ui.fragment.DrivingSchoolFragment;
import com.miaokao.android.app.ui.fragment.FindFragment;
import com.miaokao.android.app.ui.fragment.IndexFragment;
import com.miaokao.android.app.ui.fragment.MeFragment;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.DialogTips;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private static final String TAG_INDEX = "index";
	
	private Context mContext;
	private Button[] mTabBts;
	private BaseFragment[] mFragments;
	private IndexFragment mIndexFragment;
	private DrivingSchoolFragment mSchoolFragment;
	private FindFragment mFindFragment;
	private MeFragment mMeFragment;
	private int index, currentTabIndex;
	private DialogTips mDialogTips;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		mContext = this;

		initView();
		initFragment(savedInstanceState);
		// 检测是否有更新
		checkUpgrade();
		initRecerver();
	}

	// 检测是否有更新
	private void checkUpgrade() {
		if (!TextUtils.isEmpty(RequestConstants.VERSION)) {
			PubUtils.showUpgrade(mContext, RequestConstants.VERSION, RequestConstants.UPGRADE, RequestConstants.URL);
			RequestConstants.VERSION = null;
			RequestConstants.UPGRADE = null;
			RequestConstants.URL = null;
		}
	}

	private void initRecerver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		filter.addAction(PubConstant.SHOW_UPDATE_KEY);
		filter.addAction(PubConstant.PUSH_MESSAGE_B_KEY);
		registerReceiver(mReceiver, filter);
	}

	private void initFragment(Bundle savedInstanceState) {
		// 默认先加载第1个
		index = 1;
		if(savedInstanceState != null) {
			// 是否需要重新登录
			if(AppContext.getInstance().mUser == null) {
				RequestConstants.autoLogin(MainActivity.this);
			}
			
			FragmentManager fm = getSupportFragmentManager();
			mIndexFragment = (IndexFragment) fm.findFragmentByTag("IndexFragment");
			mSchoolFragment = (DrivingSchoolFragment) fm.findFragmentByTag("DrivingSchoolFragment");
			mFindFragment = (FindFragment) fm.findFragmentByTag("FindFragment");
			mMeFragment = (MeFragment) fm.findFragmentByTag("MeFragment");
		
			index = savedInstanceState.getInt(TAG_INDEX);
		} 
		if(mIndexFragment == null) {
			mIndexFragment = new IndexFragment();
		}
		if(mSchoolFragment == null) {
			mSchoolFragment = new DrivingSchoolFragment();
		}
		if(mFindFragment == null) {
			mFindFragment = new FindFragment();
		}
		if(mMeFragment == null) {
			mMeFragment = new MeFragment();
		}
		
		mIndexFragment.setCallBackListenner(mainCallbackListenner);
		mSchoolFragment.setCallBackListenner(mainCallbackListenner);
		mFindFragment.setCallBackListenner(mainCallbackListenner);
		mMeFragment.setCallBackListenner(mainCallbackListenner);
		
		mFragments = new BaseFragment[] { mIndexFragment, mSchoolFragment, mFindFragment, mMeFragment };
		
		currentTab();

		if(AppContext.getInstance().mUser != null) {
			checkOrder();
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
		// 存储下标
		outState.putInt(TAG_INDEX, index);
	}

	private void initView() {
		AppContext.getInstance().addActivity(this);

		Button b1 = (Button) findViewById(R.id.main_tab_index_bt);
		Button b2 = (Button) findViewById(R.id.main_tab_jiaxiao_bt);
		Button b3 = (Button) findViewById(R.id.main_tab_find_bt);
		Button b4 = (Button) findViewById(R.id.main_tab_me_bt);
		mTabBts = new Button[] { b1, b2, b3, b4 };

		mTabBts[0].setSelected(true);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		}
	}

	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.main_tab_index_bt:
			index = 0;
			break;
		case R.id.main_tab_jiaxiao_bt:
			index = 1;
			break;
		case R.id.main_tab_find_bt:
			index = 2;
			break;
		case R.id.main_tab_me_bt:
			index = 3;
			break;
		}
		currentTab();
		// 判断是否异常重启过
		AppContext.getInstance().checkProcessStatus();
	}

	private void currentTab() {
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(mFragments[currentTabIndex]);
			if (!mFragments[index].isAdded()) {
				String tag = "tag";
				if(mFragments[index] instanceof IndexFragment) {
					tag = "IndexFragment";
				} else if(mFragments[index] instanceof DrivingSchoolFragment) {
					tag = "DrivingSchoolFragment";
				} else if(mFragments[index] instanceof FindFragment) {
					tag = "FindFragment";
				} else if(mFragments[index] instanceof MeFragment) {
					tag = "MeFragment";
				}
				trx.add(R.id.fragment_container, mFragments[index], tag);
			} else {
				mFragments[index].refreshFragment();
			}
			trx.show(mFragments[index]).commitAllowingStateLoss();

			// 更改底部导航的按钮状态
			mTabBts[currentTabIndex].setSelected(false);
			mTabBts[index].setSelected(true);
			currentTabIndex = index;
		}
	}

	// 检测当前是否有订单
	private void checkOrder() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "order");
		postData.put("user_id", AppContext.getInstance().mUser.getLoginName());
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				final JSONArray jsonArray = jsonObject.optJSONArray("message");
				List<Order> orders = new ArrayList<>();
				PubUtils.analysisMyOrder(orders, jsonArray);
				if (orders.size() > 0) {
					int len = orders.size();
					for (int i = 0; i < len; i++) {
						Order order = orders.get(i);
						if ("1".equals(order.getStatus())) {
							index = 0;
							currentTab();
							return;
						}
					}
				}

			}

			@Override
			public void responseError() {

			}
		}, false, "checkOrder");
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (index == 2) {
			// 位于发现
			if (mFindFragment != null && mFindFragment.isWebSkip()) {
				mFindFragment.goBack();
				return true;
			}
		}
		if (AppContext.getInstance().mUser != null) {
			if (keyCode == KeyEvent.KEYCODE_BACK) {
				// 退出登录 --- 需要调用接口
				mDialogTips = new DialogTips(mContext, "确定退出登录吗?");
				mDialogTips.setOkListenner(new onDialogOkListenner() {
					@Override
					public void onClick() {
						PubUtils.logout(mContext, true);
					}
				});
				mDialogTips.setCancelListenner(null);
				mDialogTips.show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 判断是系统回收内存
		AppContext.getInstance().checkProcessStatus();
				
		if (AppContext.getInstance().mHomeResume && RequestConstants.LOGIN_END) {
			if (AppContext.getInstance().mUser != null) {
				// 登录成功的状态才进行
				// 从home键到重新可见的 - 重新登录
				RequestConstants.LOGIN_END = false;
				RequestConstants.autoLogin(mContext);
				AppContext.getInstance().mHomeResume = false;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		MyRecerver.mListenner.clear();
		AppContext.getInstance().mUser = null;
		AppContext.getInstance().stopLocalService();
		unregisterReceiver(mReceiver);
	}

	private OnMainCallbackListenner mainCallbackListenner = new OnMainCallbackListenner() {
		@Override
		public void onCurrentTab(int cur) {
			index = cur;
			currentTab();
		};
	};

	public interface OnMainCallbackListenner {

		public void onCurrentTab(int cur);
	}

	static final String SYSTEM_REASON = "reason";
	static final String SYSTEM_HOME_KEY = "homekey";// home key
	static final String SYSTEM_RECENT_APPS = "recentapps";// long home key

	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				// home键监听
				String reason = intent.getStringExtra(SYSTEM_REASON);
				if (reason != null) {
					if (reason.equals(SYSTEM_HOME_KEY)) {
						// home key处理点
						AppContext.getInstance().mHomeResume = true;
					} else if (reason.equals(SYSTEM_RECENT_APPS)) {
						// long homekey处理点

					}
				}
			} else if (PubConstant.SHOW_UPDATE_KEY.equals(action)) {
				// 版本升级
				checkUpgrade();
			} else if (PubConstant.PUSH_MESSAGE_B_KEY.equals(action)) {
				String push_result = intent.getStringExtra("push_result");

			}
		}
	};

}
