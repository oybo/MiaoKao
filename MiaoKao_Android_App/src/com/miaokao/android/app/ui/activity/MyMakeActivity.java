package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.MyMakeAdapter;
import com.miaokao.android.app.entity.Make;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.AlertDialogPopupWindow;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.MGirdView;

/**
 * @TODO 我的预约页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-5 下午1:49:29 
 */
public class MyMakeActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private MGirdView mGirdView;
	private AlertDialogPopupWindow mDialogPopupWindow;
	private MyMakeAdapter mAdapter;
	private List<Make> mMakes;
	private String mBill_num;
	private Order mOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_my_make);
		
		Intent intent = getIntent();
		mBill_num = intent.getStringExtra("bill_num");
		mOrder = (Order) intent.getExtras().getSerializable("order");
		mContext = this;
		initView();
		initData();
		getData();
	}

	private void getData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "user_arrange");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null && !"null".equals(jsonArray)) {
					mMakes.clear();
					int len = jsonArray.length();
					for(int i=0; i<len; i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						Make make = new Make();
						make.setCoach_id(object.optString("coach_id"));
						make.setCoach_name(object.optString("coach_name"));
						make.setExercise_name(object.optString("exercise_name"));
						make.setR_date(object.optString("r_date"));
						make.setTime_node(object.optString("time_node"));
						mMakes.add(make);
					}
				}
				mAdapter.notifyDataSetChanged();
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void initData() {
		mMakes = new ArrayList<>();
		mAdapter = new MyMakeAdapter(this, mMakes, R.layout.item_my_make_activity);
		mGirdView.setAdapter(mAdapter);
		mGirdView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
				// 取消预约
				showDialogTipsAndCancel(mContext, "是否取消这次预约?", new onDialogOkListenner() {
					@Override
					public void onClick() {
						cancelMake(mMakes.get(arg2));
					}
				});
			}
		});
	}

	protected void cancelMake(Make make) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "cancle_arrange");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("coach_id", make.getCoach_id());
		postData.put("date", make.getR_date());
		String time_node = make.getTime_node();
		postData.put("time_node", time_node.replace(":00", ""));
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				String result = object.optString("result");
				if("ok".equals(result) || "操作成功".equals(result)) {
					getData();
				} else {
					showDialogTips(mContext, result);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, "cancelMake");
	}

	private void initView() {
		initTopBarAll(R.id.make_common_actionbar, "我的预约", R.drawable.gengduo, new OnRightClickListenner() {
			@Override
			public void onClick() {
				String title = "";
				if("2".equals(mBill_num)) {
					if("alipay".equals(mOrder.getPay_channel())) {
						title = "确认完成后，请支付驾考培训费用第三批款项";
					} else {
						title = "确定本阶段完成学习?";
					}
				} else if("3".equals(mBill_num)) {
					title = "确定本阶段完成学习?";
				}
				mDialogPopupWindow = new AlertDialogPopupWindow(MyMakeActivity.this, title, itemsOnClick);
				mDialogPopupWindow.showAtLocation(mGirdView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); // 设置layout在PopupWindow中显示的位置
			}
		});
		
		mGirdView = (MGirdView) findViewById(R.id.my_make_gv);
		
		findViewById(R.id.my_make_ok_bt).setOnClickListener(this);
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			mDialogPopupWindow.dismiss();
			switch (v.getId()) {
			case R.id.alert_dialog_ok_bt:
				// 确定
				confirm();
				break;
			case R.id.alert_dialog_cancel_bt:
				// 取消
				
				break;
			}
		}

	};

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_make_ok_bt:
			// 确认完成
			confirm();
			break;
		}
	}
	
	private void confirm() {
		if("2".equals(mBill_num)) {
			// 科目二的时候
			if("alipay".equals(mOrder.getPay_channel())) {
				// 如果是线上支付，我就不用调用这个确认接口，直接到支付页面，  如果是cash 我就直接调用接口
				goPay("3");
			} else {
				affirm();
			}
		} else if("3".equals(mBill_num)) {
			// 科目三的时候
			affirm();
		}
	}
	
	private void goPay(String id) {
		Intent intent = new Intent(mContext, PayActivity.class);
		intent.putExtra("id", id);	// 第几批
		Bundle bundle = new Bundle();
		bundle.putSerializable("order", (Serializable) mOrder);
		intent.putExtras(bundle);
		startActivityForResult(intent, PubConstant.PAY_SUCCESS_CODE);
	}
	
	private void affirm() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_index_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "study_status");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		postData.put("bill_num", (Integer.parseInt(mBill_num) + 1) + "");
		postData.put("order_no", mOrder.getOrder_no());
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				String result = object.optString("result");
				if("ok".equals(result)) {
					// 确认完成 退出 返回到首页。
					sendBroadcast(new Intent(PubConstant.MAKE_SUCCESS_FINISH_KEY));
					finish();
				} else {
					showDialogTips(mContext, result);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, "affirm");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PubConstant.PAY_SUCCESS_CODE && resultCode == Activity.RESULT_OK) {
			setResult(Activity.RESULT_OK);
			finish();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
		mAppContext.callRequest("affirm");
	}
	
	
}
