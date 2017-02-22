package com.miaokao.android.app.ui.activity;

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
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.MakeStudyCarHourAdapter;
import com.miaokao.android.app.adapter.MakeStudyCarHourTabAdapter;
import com.miaokao.android.app.adapter.MakeStudyCarWeekAdapter;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.CoachArrange;
import com.miaokao.android.app.entity.MakeDate;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.entity.TrainingGround;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.util.TimeUtil;
import com.miaokao.android.app.widget.DialogTips.onDialogCancelListenner;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.MGirdView;
import com.miaokao.android.app.widget.MListView;

/**
 * @TODO 预约教练学车
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-29 下午10:49:58
 */
public class MakeStudyCarActivity extends BaseActivity {

	private static final int TIME_LEN = 18;
	private static final int MAKE_CODE = 1;
	private Context mContext;
	private MListView mListView;
	private MGirdView mGirdView;
	private MGirdView mHourGirdView;
	// 教练
	private Coach mCoach;
	// 每天的时间点
	private List<String> mHourList;
	// 每周
	private List<MakeDate> mMakeDates;
	private MakeStudyCarWeekAdapter mMakeStudyCarWeekAdapter;
	// 7天的时间点
	private List<CoachArrange> mCoachArranges;
	private MakeStudyCarHourAdapter mMakeStudyCarHourAdapter;
	private Map<String, CoachArrange> mTemp1 = new HashMap<>();
	private Map<String, CoachArrange> mTemp2 = new HashMap<>();
	private Map<String, CoachArrange> mTemp3 = new HashMap<>();
	private Map<String, CoachArrange> mTemp4 = new HashMap<>();
	private Map<String, CoachArrange> mTemp5 = new HashMap<>();
	private Map<String, CoachArrange> mTemp6 = new HashMap<>();
	private Map<String, CoachArrange> mTemp7 = new HashMap<>();
	private Order mOrder;
	// 训练场
	private TrainingGround mTrainingGround;
	// 是否接送
	private String mDelivery;
	// 训练项目
	private String mExercise_name;
	// 训练类型
	private String mPlaceType;
	private String mBill_num;
	
	private String mUserId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_make_study_car);

		Bundle bundle = getIntent().getExtras();
		mOrder = (Order) bundle.getSerializable("order");
		mCoach = (Coach) bundle.getSerializable("coach");
		mTrainingGround = (TrainingGround) bundle.getSerializable("trainingGround");
		mDelivery = bundle.getString("delivery");
		mExercise_name = bundle.getString("exercise_name");
		mPlaceType = bundle.getString("placeType");
		mBill_num = bundle.getString("bill_num");
		mUserId = AppContext.getInstance().mUser.getLoginName();
		mContext = this;
		initView();
		initWeekList();
		initHourTab();
		initHourList();
		// 获取教练本周安排
		getReservedService();
		initReceiver();
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubConstant.MAKE_SUCCESS_FINISH_KEY);
		registerReceiver(receiver, filter);
	}

	private void initView() {
		initTopBarAll(R.id.make_study_car_common_actionbar, mCoach.getName(), "我的预约", new OnRightClickListenner() {
			
			@Override
			public void onClick() {
				myMake();
			}
		});
		
		mListView = (MListView) findViewById(R.id.make_study_car_week_lv);
		mGirdView = (MGirdView) findViewById(R.id.make_study_car_week_gv);
		mHourGirdView = (MGirdView) findViewById(R.id.make_study_car_hour_gv);
		mGirdView.setNumColumns(TIME_LEN);
		mHourGirdView.setNumColumns(TIME_LEN);
		
		mGirdView.post(new Runnable() {

			@Override
			public void run() {
				LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(PubUtils.dip2px(mContext, 55 * TIME_LEN),
						LinearLayout.LayoutParams.WRAP_CONTENT);
				mGirdView.setLayoutParams(lParams);
				mHourGirdView.setLayoutParams(lParams);
			}
		});
	}

	/**   我的预约      */
	private void myMake() {
		Intent intent = new Intent(mContext, MyMakeActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", mOrder);
		intent.putExtras(bundle);
		intent.putExtra("bill_num", mBill_num);
		startActivityForResult(intent, MAKE_CODE);
	}

	private void initWeekList() {
		mMakeDates = new ArrayList<>();
		mMakeDates.add(new MakeDate("周一", "01月01日", ""));
		mMakeDates.add(new MakeDate("周二", "01月02日", ""));
		mMakeDates.add(new MakeDate("周三", "01月03日", ""));
		mMakeDates.add(new MakeDate("周四", "01月04日", ""));
		mMakeDates.add(new MakeDate("周五", "01月05日", ""));
		mMakeDates.add(new MakeDate("周六", "01月06日", ""));
		mMakeDates.add(new MakeDate("周日", "01月07日", ""));
		mMakeStudyCarWeekAdapter = new MakeStudyCarWeekAdapter(mContext, mMakeDates, R.layout.item_week_activity);
		mListView.setAdapter(mMakeStudyCarWeekAdapter);
		new Thread(){
			public void run() {
				mMakeDates.clear();
				mMakeDates.addAll(TimeUtil.getWeekDate());
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mMakeStudyCarWeekAdapter.notifyDataSetChanged();
					}
				});
			};
		}.start();
	}

	private void initHourTab() {
		mHourList = new ArrayList<>();
		mHourList.add("6-7:00");
		mHourList.add("7-8:00");
		mHourList.add("8-9:00");
		mHourList.add("9-10:00");
		mHourList.add("10-11:00");
		mHourList.add("11-12:00");
		mHourList.add("12-13:00");
		mHourList.add("13-14:00");
		mHourList.add("14-15:00");
		mHourList.add("15-16:00");
		mHourList.add("16-17:00");
		mHourList.add("17-18:00");
		mHourList.add("18-19:00");
		mHourList.add("19-20:00");
		mHourList.add("20-21:00");
		mHourList.add("21-22:00");
		mHourList.add("22-23:00");
		mHourList.add("23-24:00");
		mHourGirdView.setAdapter(new MakeStudyCarHourTabAdapter(mContext, mHourList, R.layout.item_hour_tab_activity));
	}
	
	private void initHourList() {
		mCoachArranges = new ArrayList<>();
		int len = 7 * TIME_LEN;
		for (int i = 0; i < len; i++) {
			mCoachArranges.add(null);
		}
		mMakeStudyCarHourAdapter = new MakeStudyCarHourAdapter(mContext, mCoachArranges, R.layout.item_hour_activity);
		mGirdView.setAdapter(mMakeStudyCarHourAdapter);
		mGirdView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				final CoachArrange arrange = mCoachArranges.get(arg2);
				if(arrange != null) {
					if(mUserId.equals(arrange.getUser_id())) {
						// 点击取消预约
						showDialogTipsAndCancel(mContext, "是否取消这次预约?", new onDialogOkListenner() {
							@Override
							public void onClick() {
								cancelMake(arrange);
							}
						});
						return;
					}
					showDialogTips(mContext, "请选择其它时间预约!");
					return;
				}
				String date = mMakeDates.get(arg2 / TIME_LEN).getDateYear();
				String time_node = mHourList.get(arg2 % TIME_LEN).split(":")[0];
				
				makeStudyCar(date, time_node);
			}
		});
	}

	/**    预约       */
	protected void makeStudyCar(String date, String time_node) {
		if(TextUtils.isEmpty(mAppContext.mUser.getLoginName())) {
			showDialogTips(mContext, "userid为空!");
			return;
		}
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "add");
		postData.put("delivery", mDelivery);	// 是否需要接送
		postData.put("place", mTrainingGround != null ? mTrainingGround.getSup_name() : "");	// 练习场地
		postData.put("exercise_name", mExercise_name);	// 练习项目
		postData.put("p_type", mPlaceType);	// 场地类型  -- 科目二  / 科目三
		postData.put("date", date);	// 预定日期
		postData.put("time_node", time_node);	// 时间段
		postData.put("mer_id", mOrder.getMer_id());	
		postData.put("user_id", mAppContext.mUser.getLoginName());	
		postData.put("coach_id", mCoach.getAccount());	// 教练帐号
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null) {
					JSONObject object = jsonArray.optJSONObject(0);
					if(object != null && !"null".equals(object) && !TextUtils.isEmpty(object.optString("user_id"))) {
						// 预约成功
						// 刷新
						getReservedService();
						// 提示
						String message = "预约成功,查看预约安排或继续预约?";
						showDialogTipsAll(mContext, message, "继续预约", new onDialogCancelListenner() {
							@Override
							public void onClick() {
								
							}
							
						}, "查看预约安排", new onDialogOkListenner() {
							@Override
							public void onClick() {
								// 跳转到我的预约页面
								myMake();
							}
							
						});
					} else {
						showDialogTips(mContext, "预约失败");
					}
				}
			}
			
			@Override
			public void responseError() {
				showDialogTips(mContext, "预约失败");
			}
		}, true, "makeStudyCar");
	}

	protected void cancelMake(final CoachArrange arrange) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "cancle_arrange");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("coach_id", arrange.getCoach_id());
		postData.put("date", arrange.getR_date());
		String time_node = arrange.getTime_node();
		postData.put("time_node", time_node.replace(":00", ""));
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				String result = object.optString("result");
				if("ok".equals(result) || "操作成功".equals(result)) {
					// 刷新
					getReservedService();
				} else {
					showDialogTips(mContext, result);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, "cancelMake");
	}
	
	private void getReservedService() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "coach_arrange");
		postData.put("coach_id", mCoach.getAccount());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				initCoachArrange(mTemp1);
				initCoachArrange(mTemp2);
				initCoachArrange(mTemp3);
				initCoachArrange(mTemp4);
				initCoachArrange(mTemp5);
				initCoachArrange(mTemp6);
				initCoachArrange(mTemp7);
				new Thread() {
					public void run() {
						try {
							JSONArray jsonArray_1 = jsonObject.getJSONArray("1");
							if(jsonArray_1 != null && !"null".equals(jsonArray_1)) {
								int len = jsonArray_1.length();
								for(int i=0; i<len; i++) {
									JSONObject object = jsonArray_1.optJSONObject(i);
									if(object != null && !"null".equals(object)) {
										CoachArrange arrange = new CoachArrange();
										getCoachArrange(arrange, object);
										
										mTemp1.put(arrange.getTime_node().split("-")[0], arrange);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						try {
							JSONArray jsonArray_2 = jsonObject.getJSONArray("2");
							if(jsonArray_2 != null && !"null".equals(jsonArray_2)) {
								int len = jsonArray_2.length();
								for(int i=0; i<len; i++) {
									JSONObject object = jsonArray_2.optJSONObject(i);
									if(object != null && !"null".equals(object)) {
										CoachArrange arrange = new CoachArrange();
										getCoachArrange(arrange, object);
										
										mTemp2.put(arrange.getTime_node().split("-")[0], arrange);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						try {
							JSONArray jsonArray_3 = jsonObject.getJSONArray("3");
							if(jsonArray_3 != null && !"null".equals(jsonArray_3)) {
								int len = jsonArray_3.length();
								for(int i=0; i<len; i++) {
									JSONObject object = jsonArray_3.optJSONObject(i);
									if(object != null && !"null".equals(object)) {
										CoachArrange arrange = new CoachArrange();
										getCoachArrange(arrange, object);
										
										mTemp3.put(arrange.getTime_node().split("-")[0], arrange);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						try {
							JSONArray jsonArray_4 = jsonObject.getJSONArray("4");
							if(jsonArray_4 != null && !"null".equals(jsonArray_4)) {
								int len = jsonArray_4.length();
								for(int i=0; i<len; i++) {
									JSONObject object = jsonArray_4.optJSONObject(i);
									if(object != null && !"null".equals(object)) {
										CoachArrange arrange = new CoachArrange();
										getCoachArrange(arrange, object);
										
										mTemp4.put(arrange.getTime_node().split("-")[0], arrange);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						try {
							JSONArray jsonArray_5 = jsonObject.getJSONArray("5");
							if(jsonArray_5 != null && !"null".equals(jsonArray_5)) {
								int len = jsonArray_5.length();
								for(int i=0; i<len; i++) {
									JSONObject object = jsonArray_5.optJSONObject(i);
									if(object != null && !"null".equals(object)) {
										CoachArrange arrange = new CoachArrange();
										getCoachArrange(arrange, object);
										
										mTemp5.put(arrange.getTime_node().split("-")[0], arrange);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						try {
							JSONArray jsonArray_6 = jsonObject.getJSONArray("6");
							if(jsonArray_6 != null && !"null".equals(jsonArray_6)) {
								int len = jsonArray_6.length();
								for(int i=0; i<len; i++) {
									JSONObject object = jsonArray_6.optJSONObject(i);
									if(object != null && !"null".equals(object)) {
										CoachArrange arrange = new CoachArrange();
										getCoachArrange(arrange, object);
										
										mTemp6.put(arrange.getTime_node().split("-")[0], arrange);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						try {
							JSONArray jsonArray_7 = jsonObject.getJSONArray("7");
							if(jsonArray_7 != null && !"null".equals(jsonArray_7)) {
								int len = jsonArray_7.length();
								for(int i=0; i<len; i++) {
									JSONObject object = jsonArray_7.optJSONObject(i);
									if(object != null && !"null".equals(object)) {
										CoachArrange arrange = new CoachArrange();
										getCoachArrange(arrange, object);
										
										mTemp7.put(arrange.getTime_node().split("-")[0], arrange);
									}
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						// 遍历map
						checkCoachArrange(TIME_LEN * 0, mTemp1);
						checkCoachArrange(TIME_LEN * 1, mTemp2);
						checkCoachArrange(TIME_LEN * 2, mTemp3);
						checkCoachArrange(TIME_LEN * 3, mTemp4);
						checkCoachArrange(TIME_LEN * 4, mTemp5);
						checkCoachArrange(TIME_LEN * 5, mTemp6);
						checkCoachArrange(TIME_LEN * 6, mTemp7);
						// 刷新
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mMakeStudyCarHourAdapter.notifyDataSetChanged();
							}
						});
					};
				}.start();
				
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
	private void initCoachArrange(Map<String, CoachArrange> temp) {
		temp.put("6", null);
		temp.put("7", null);
		temp.put("8", null);
		temp.put("9", null);
		temp.put("10", null);
		temp.put("11", null);
		temp.put("12", null);
		temp.put("13", null);
		temp.put("14", null);
		temp.put("15", null);
		temp.put("16", null);
		temp.put("17", null);
		temp.put("18", null);
		temp.put("19", null);
		temp.put("20", null);
		temp.put("21", null);
		temp.put("22", null);
		temp.put("23", null);
	}

	private void checkCoachArrange(int i, Map<String, CoachArrange> temp) {
		for (String key : temp.keySet()) {
			for(int k=6; k<=23; k++) {
				if(key.equals(k + "")) {
					int tag = i + (k - 6);
					mCoachArranges.remove(tag);
					mCoachArranges.add(tag, temp.get(key));
					break;
				}
			}
		}
	}
	
	private void getCoachArrange(CoachArrange arrange, JSONObject object) {
		String time = object.optString("time_node");
		arrange.setR_date(object.optString("r_date"));
		arrange.setTime_node(time);
		arrange.setExercise_name(object.optString("exercise_name"));
		arrange.setCoach_id(object.optString("coach_id"));
		arrange.setUser_id(object.optString("user_id"));
		arrange.setStatus(object.optString("status"));
		arrange.setUser_name(object.optString("user_name"));
		arrange.setHead_img(object.optString("head_img"));
		arrange.setOrder_no(object.optString("order_no"));
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == MAKE_CODE) {
			// 刷新
			getReservedService();
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
