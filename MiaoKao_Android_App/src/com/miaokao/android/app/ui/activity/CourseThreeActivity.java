package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.TrainingGroundAdapter;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.entity.TrainingGround;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.DialogBuider;
import com.miaokao.android.app.widget.MListView;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;

/**
 * @TODO 科目三预约页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-4 上午9:55:08 
 */
public class CourseThreeActivity extends BaseActivity implements OnClickListener {

	private static final String BILL_NUM = "3";
	private Context mContext;
	private DialogBuider mDialogBuider;
	private TextView mSelectSiteTxt, mCoachTxt;
	private List<TrainingGround> mTrainingGrounds;
	private Order mOrder;
	private Coach mCoach;
	private TrainingGround mTrainingGround;
	// 是否接送
	private CheckBox mCheckBox;
	private MListView mListView;
	private TrainingGroundAdapter mAddressAdapter;
	private List<TrainingGround> mAddresss;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_course_three);
		
		mOrder = (Order) getIntent().getExtras().getSerializable("order");
		mContext = this;
		initView();
		initData();
		initReceiver();
		getAddress();
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubConstant.MAKE_SUCCESS_FINISH_KEY);
		registerReceiver(receiver, filter);
	}
	
	/**
	 * 获取场地
	 */
	private void initData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_place");
		postData.put("mer_id", mOrder.getMer_id());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null && !"null".equals(jsonArray)) {
					mTrainingGrounds = new ArrayList<>();
					int len = jsonArray.length();
					for(int i=0; i<len; i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						if(object != null && !"null".equals(object)) {
							TrainingGround trainingGround = new TrainingGround();
							trainingGround.setSup_no(object.optString("sup_no"));
							trainingGround.setSup_name(object.optString("sup_name"));
							trainingGround.setSup_addr(object.optString("sup_addr"));
							mTrainingGrounds.add(trainingGround);
						}
					}
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
	private void getAddress() {
		// 如果地址为空则去请求地址
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_delivery_place");
		postData.put("mer_id", mOrder.getMer_id());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null) {
					mAddresss = new ArrayList<>();
					int len = jsonArray.length();
					if(len > 0) {
						for(int i=0; i<len; i++) {
							JSONObject object = jsonArray.optJSONObject(i);
							TrainingGround ground = new TrainingGround();
							ground.setSup_no(object.optString("sup_no"));
							ground.setSup_addr(object.optString("sup_addr"));
							String sup_name = object.optString("sup_name");
							ground.setSup_name(sup_name);
							if(sup_name.equals(mAppContext.mUser.getAddress())) {
								ground.setSelect(true);
							}
							
							mAddresss.add(ground);
						}
					} else {
						// 添加默认的
						TrainingGround ground = new TrainingGround();
						ground.setSup_name(mAppContext.mUser.getAddress());
						ground.setSelect(true);
						mAddresss.add(ground);
					}
					
					mAddressAdapter = new TrainingGroundAdapter(mContext, mAddresss,
							R.layout.item_training_ground_activity);
					mListView.setAdapter(mAddressAdapter);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, false, "getAddress");
	}

	private void initView() {
		initTopBarAll(R.id.course_three_common_actionbar, "科目三预约", "我的预约", new OnRightClickListenner() {
			
			@Override
			public void onClick() {
				myMake();
			}
		});
		
		mSelectSiteTxt = (TextView) findViewById(R.id.course_three_select_site_txt);
		mCoachTxt = (TextView) findViewById(R.id.course_three_select_coach_txt);
		
		mListView = (MListView) findViewById(R.id.coure_three_address_listview);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				for(TrainingGround trainingGround : mAddresss) {
					trainingGround.setSelect(false);
				}
				mAddresss.get(arg2).setSelect(true);
				mAddressAdapter.notifyDataSetChanged();
				
				if(!TextUtils.isEmpty(mAddresss.get(arg2).getSup_addr())) {
					updateUserAddress(mAddresss.get(arg2).getSup_addr());
				}
			}
		});
		mCheckBox = (CheckBox) findViewById(R.id.course_three_is_pick_up_cb);
		mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) {
					// 显示选择地址
					mListView.setVisibility(View.VISIBLE);
				} else {
					mListView.setVisibility(View.GONE);
				}
			}
		});
		
		mSelectSiteTxt.setOnClickListener(this);
		findViewById(R.id.course_three_select_coach_layout).setOnClickListener(this);
		findViewById(R.id.course_three_next_bt).setOnClickListener(this);
		
		// 初始化默认教练
		if (!TextUtils.isEmpty(mAppContext.mUser.getCoach_name())) {
			mCoach = new Coach();
			mCoach.setName(mAppContext.mUser.getCoach_name());
			mCoach.setAccount(mAppContext.mUser.getCoach_id());
			mCoachTxt.setText(mAppContext.mUser.getCoach_name());
		}
		// 初始化默认训练场地
		if (!TextUtils.isEmpty(mAppContext.mUser.getPlace())) {
			mSelectSiteTxt.setText(mAppContext.mUser.getPlace());
			
			mTrainingGround = new TrainingGround();
			mTrainingGround.setSup_name(mAppContext.mUser.getPlace());
		}
	}

	protected void updateUserAddress(final String sup_addr) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		// 地址
		postData.put("type", "addr");
		postData.put("addr", sup_addr);
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			@Override
			public void responseResult(JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				String result = object.optString("result");
				if("ok".equals(result)) {
					mAppContext.mUser.setAddress(sup_addr);
				}
			}
			
			@Override
			public void responseError() {
			}
		}, false, "save_user_info");
	}
	
	/**   我的预约      */
	private void myMake() {
		Intent intent = new Intent(mContext, MyMakeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", mOrder);
		intent.putExtras(bundle);
		intent.putExtra("bill_num", BILL_NUM);
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.course_three_select_site_txt:
			if(mTrainingGrounds != null) {
				mDialogBuider = new DialogBuider(mContext, mTrainingGrounds, new OnItemClickListener() {
					
					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
						mDialogBuider.dismiss();
						mTrainingGround = mTrainingGrounds.get(arg2);
						mSelectSiteTxt.setText(mTrainingGround.getSup_name());
					}
				});
				mDialogBuider.show();
			}
			break;
		case R.id.course_three_select_coach_layout:
			// 选择教练
			Intent intent = new Intent(mContext, SelectCoachActivity.class);
			Bundle bundle = new Bundle();
			bundle.putSerializable("order", (Serializable) mOrder);
			intent.putExtras(bundle);
			startActivityForResult(intent, PubConstant.SELECT_COACH_CODE);
			break;
		case R.id.course_three_next_bt:
			// 下一步
//			mTrainingGround
			if(mCoach == null) {
				showDialogTips(mContext, "请选择教练");
				return;
			}
			intent = new Intent(mContext, MakeStudyCarActivity.class);
			bundle = new Bundle();
			// 订单
			bundle.putSerializable("order", (Serializable) mOrder);
			// 教练
			bundle.putSerializable("coach", (Serializable) mCoach);
			// 是否需要接送
			bundle.putString("delivery", mCheckBox.isChecked() ? "1" : "0");
			// 训练场
			bundle.putSerializable("trainingGround", (Serializable) mTrainingGround);
			// 训练项目
			bundle.putString("exercise_name", "");
			// 训练类型
			bundle.putString("placeType", "3");	// 科目三
			bundle.putString("bill_num", BILL_NUM);
			intent.putExtras(bundle);
			startActivity(intent);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PubConstant.SELECT_COACH_CODE && resultCode == Activity.RESULT_OK) {
			if(data != null) {
				mCoach = (Coach) data.getExtras().getSerializable("coach");
			}
			mCoachTxt.setText(mCoach.getName());
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
		unregisterReceiver(receiver);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(PubConstant.MAKE_SUCCESS_FINISH_KEY.equals(action)) {
				finish();
			}
		}
	};
	
}
