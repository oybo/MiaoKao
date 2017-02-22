package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.miaokao.android.app.adapter.CoachAdapter;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

/**
 * 获取教练列表
 * @author ouyangbo
 *
 */
public class CoachActivity extends BaseActivity {

	private Context mContext;
	private ListView mListView;
	private List<Coach> mCoachs;
	private CoachAdapter mAdapter;
	private boolean mIsSelectCoach;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_coach);
		
		mContext = this;
		initView();
		initList();
		
		Intent intent = getIntent();
		mIsSelectCoach = intent.getBooleanExtra("isSelectCoach", false);
		getData(intent.getStringExtra("mer_id"));
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
			}
			
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.coach_common_actionbar, "教练");
		
		mListView = (ListView) findViewById(R.id.coach_listview);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				Coach coach = mCoachs.get(arg2);
				if(mIsSelectCoach) {
					// 选择教练
					Intent intent = new Intent();
					Bundle extras = new Bundle();
					extras.putSerializable("coach", (Serializable) coach);
					intent.putExtras(extras);
					setResult(Activity.RESULT_OK, intent);
					finish();
				} else {
					// 进入教练详情
					Intent intent = new Intent(mContext, CoachDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("coach", (Serializable) coach);
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}
	
	private void initList() {
		mCoachs = new ArrayList<>();
		mAdapter = new CoachAdapter(this, mCoachs, R.layout.item_coach);
		mListView.setAdapter(mAdapter);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		AppContext.getInstance().callRequest(getClass().getName());
	}
}
