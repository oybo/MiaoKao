package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.R;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.adapter.CommentAdapter;
import com.miaokao.android.app.entity.MerComment;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ListView;

/**
 * @TODO 驾校评论页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-21 上午11:10:42 
 */
public class CommentActivity extends BaseActivity {

	private ListView mListView;
	private Thread mThread;
	private String mMerId;
	private String mCoachId;
	private List<MerComment> mMerComments;
	private CommentAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_comment);
		
		initView();
		initData();
		if(!TextUtils.isEmpty(mMerId)) {
			// 驾校
			String url = PubConstant.REQUEST_BASE_URL + "/app_merchants_service.php";
			getComments(url, 1);
		} else {
			// 教练
			String url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";
			getComments(url, 2);
		}
		
	}

	private void getComments(String url, int type) {
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_comment");
		if(type == 1) {
			// 驾校
			postData.put("mer_id", mMerId);
		} else if(type == 2) {
			postData.put("coach_id", mCoachId);
		}
		postData.put("page", "0");
		postData.put("size", "30");
		AppContext.getInstance().netRequest(this, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				
				mThread = new Thread(){
					public void run() {
						JSONArray jsonArray = jsonObject.optJSONArray("message");
						PubUtils.analysisComment(mMerComments, jsonArray);
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

	@SuppressWarnings("unchecked")
	private void initData() {
		try {
			Intent intent = getIntent();
			mMerId = intent.getStringExtra("mer_id");
			mCoachId = intent.getStringExtra("coach_id");
			mMerComments = (List<MerComment>) intent.getExtras().getSerializable("comment_list");
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAdapter = new CommentAdapter(this, mMerComments, R.layout.item_comment_activity);
		mListView.setAdapter(mAdapter);
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.comment_common_actionbar, "全部评论");
		
		mListView = (ListView) findViewById(R.id.comment_listview);
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
