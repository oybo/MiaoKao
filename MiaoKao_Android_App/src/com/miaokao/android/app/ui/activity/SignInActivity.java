package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.miaokao.android.app.R;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.adapter.MyMakeAdapter;
import com.miaokao.android.app.entity.Make;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.MGirdView;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

/**
 * @TODO 签到页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-3 下午10:39:08 
 */
public class SignInActivity extends BaseActivity {

	private Context mContext;
	private MGirdView mGirdView;
	private MyMakeAdapter mAdapter;
	private List<Make> mMakes;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_sign_in);
		
		mContext = this;
		initView();
		initData();
		getData();
	}

	/**   获取当天预约信息     */
	private void getData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "user_arrange");
		postData.put("range", "day");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null && !"null".equals(jsonArray)) {
					mMakes.clear();
					int len = jsonArray.length();
					if(len > 0) {
						findViewById(R.id.sign_in_layout).setVisibility(View.VISIBLE);
					}
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
				// 签到 -- 调转到生成二维码页面
				Intent intent = new Intent(mContext, SignInForScanActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("make", (Serializable) mMakes.get(arg2));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.sign_in_common_actionbar, "当天预约");
		
		mGirdView = (MGirdView) findViewById(R.id.sign_in_gv);
	}
	
	
}
