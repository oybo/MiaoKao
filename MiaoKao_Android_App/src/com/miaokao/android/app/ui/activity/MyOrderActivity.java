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
import android.widget.ListView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.MyOrderAdapter;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

/**
 * @TODO 我的订单页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-24 上午9:27:18 
 */
public class MyOrderActivity extends BaseActivity {

	private Context mContext;
	private ListView mListView;
	private List<Order> mOrders;
	private MyOrderAdapter mAdapter;
	private Thread mThread;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_my_order);
		
		mContext = this;
		initView();
		initList();
		getOrders();
	}
	
	private void initView() {
		initTopBarLeftAndTitle(R.id.my_order_common_actionbar, "我的订单");
		
		mListView = (ListView) findViewById(R.id.my_order_listview);
	}

	private void initList() {
		mOrders = new ArrayList<>();
		mAdapter = new MyOrderAdapter(mContext, mOrders, R.layout.item_my_order_activity);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Intent intent = new Intent(mContext, MyOrderDetailActivity.class);
				Bundle bundle = new Bundle();
				bundle.putSerializable("order", (Serializable) mOrders.get(arg2));
				intent.putExtras(bundle);
				startActivityForResult(intent, PubConstant.REFRESH_ORDER_SUCCESS_CODE);
			}
		});
	}
	
	private void getOrders() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "order");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				final JSONArray jsonArray = jsonObject.optJSONArray("message");
				mThread = new Thread(){
					public void run() {
						mOrders.clear();
						PubUtils.analysisMyOrder(mOrders, jsonArray);
						
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mAdapter.notifyDataSetChanged();
							}
						});
					};
				};
				mThread.start();
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == PubConstant.REFRESH_ORDER_SUCCESS_CODE) {
			// 刷新订单列表
			getOrders();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
