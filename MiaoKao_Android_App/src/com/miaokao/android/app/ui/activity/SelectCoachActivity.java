package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.SelectCoachAdapter;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

/**
 * @TODO 选择常用教练页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-4 下午1:42:20 
 */
public class SelectCoachActivity extends BaseActivity {

	private Context mContext;
	private ListView mListView;
	private SelectCoachAdapter mAdapter;
	private List<Coach> mCoachs;
	private Order mOrder;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_select_coach);
		
		mOrder = (Order) getIntent().getExtras().getSerializable("order");
		mContext = this;
		initView();
		initData();
		
		getData(mOrder.getMer_id());
	}
	
	private void getData(String mer_id) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("mer_id", mer_id);
		postData.put("type", "list");
		postData.put("page", "0");
		postData.put("size", "30");
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(JSONObject jsonObject) {
				PubUtils.analysisCoachs(mCoachs, jsonObject);
				mAdapter.notifyDataSetChanged();
				// 再请求常用联系人，需要循环判断
				getUseCoachs();
			}
			
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void getUseCoachs() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_coach");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				try {
					if(jsonArray != null && !"null".equals(jsonArray)) {
						int len = jsonArray.length();
						for(int i=0; i<len; i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							if(object != null && !"null".equals(object)) {
								String account = object.optString("account");
								for(Coach coach : mCoachs) {
									if(account.equals(coach.getAccount())) {
										coach.setUseCoach(true);
										continue;
									}
								}
							}
						}
						// 刷新喔喔
						mAdapter.notifyDataSetChanged();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void initData() {
		mCoachs = new ArrayList<>();
		mAdapter = new SelectCoachAdapter(mContext, mCoachs, R.layout.item_select_coach_activity);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Coach coach = mCoachs.get(arg2);
				
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("coach", (Serializable) coach);
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				
				finish();
			}
		});
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.select_coach_common_actionbar, "选择教练");
		
		mListView = (ListView) findViewById(R.id.select_coach_lv);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
