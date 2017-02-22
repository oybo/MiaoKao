package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.SearchDrivingCoachAdapter;
import com.miaokao.android.app.adapter.SearchDrivingSchoolAdapter;
import com.miaokao.android.app.entity.Coach;
import com.miaokao.android.app.entity.DrivingSchool;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.KeyBoardUtils;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

/**
 * @TODO 驾校搜索
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-18 下午1:52:47
 */
public class SearchDrivingSchoolActivity extends BaseActivity {

	private Context mContext;
	private EditText mSearchTxtET;
	private ListView mListView;
	private SearchDrivingSchoolAdapter mSchoolAdapter;
	// 驾校集合
	private List<DrivingSchool> mDrivingSchools;
	private SearchDrivingCoachAdapter mCoachAdapter;
	// 教练集合
	private List<Coach> mDrivingCoachs;

	private boolean mIsSchool;
	private Thread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_search_driving);

		mContext = this;
		mIsSchool = getIntent().getBooleanExtra("isSchool", true);

		initView();
		initData();

		if(mIsSchool) {
			mSearchTxtET.setHint("请输入驾校名称");	
			searchDS("");
		} else {
			mSearchTxtET.setHint("请输入教练名称");
			searchCoath("");
		}
		
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.search_d_school_common_actionbar, "驾校搜索");

		mSearchTxtET = (EditText) findViewById(R.id.search_d_s_name_et);
		mListView = (ListView) findViewById(R.id.search_d_s_listview);

		mSearchTxtET.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {
					String txt = mSearchTxtET.getText().toString().trim();
					
					if(mIsSchool) {
						searchDS(txt);
					} else {
						searchCoath(txt);
					}
					
					KeyBoardUtils.closeKeybord(mContext);
				}
				return false;
			}
		});
	}
	
	private void initData() {
		if(mIsSchool) {
			mDrivingSchools = new ArrayList<DrivingSchool>();
			mSchoolAdapter = new SearchDrivingSchoolAdapter(this, mDrivingSchools, R.layout.item_search_driving_school);
			mListView.setAdapter(mSchoolAdapter);
		} else {
			mDrivingCoachs = new ArrayList<Coach>();
			mCoachAdapter = new SearchDrivingCoachAdapter(this, mDrivingCoachs, R.layout.item_search_driving_school);
			mListView.setAdapter(mCoachAdapter);
		}
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				if(mIsSchool) {
					Intent intent = new Intent(mContext, DrivingSchoolDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("drivingSchool", (Serializable) mDrivingSchools.get(arg2));
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Intent intent = new Intent(mContext, DrivingCoachDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putSerializable("coach", (Serializable) mDrivingCoachs.get(arg2));
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
	}

	/**
	 * 搜索驾校
	 * 
	 * @param txt
	 */
	private void searchDS(String txt) {
		String cityTxt = PreferenceUtils.getInstance().getString(PubConstant.LOCAL_CITY, "深圳市");
		
		String url = PubConstant.REQUEST_BASE_URL + "/app_merchants_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "list");
		postData.put("rank", "rate");
		postData.put("page", "0");
		postData.put("size", "100");
		postData.put("mer_name", txt);
		postData.put("city", cityTxt);
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				mThread = new Thread(){
					@Override
					public void run() {
						super.run();
						mDrivingSchools.clear();
						mDrivingSchools.addAll(PubUtils.analysisDSchool(jsonObject));
						// 主线程刷新
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mSchoolAdapter.notifyDataSetChanged();
							}
						});
					}
				};
				mThread.start();
			}

			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
	/**
	 * 搜索教练
	 * 
	 * @param txt
	 */
	private void searchCoath(String txt) {
		String cityTxt = PreferenceUtils.getInstance().getString(PubConstant.LOCAL_CITY, "深圳市");
		
		String url = PubConstant.REQUEST_BASE_URL + "/app_coach_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "all_coach_list");
		postData.put("rank", "rate");
		postData.put("page", "0");
		postData.put("size", "100");
		postData.put("mer_name", txt);
		postData.put("city", cityTxt);
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				mThread = new Thread(){
					@Override
					public void run() {
						super.run();
						mDrivingCoachs.clear();
						List<Coach> temp = PubUtils.analysisDCoach(jsonObject);
						mDrivingCoachs.addAll(temp);
						// 主线程刷新
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mCoachAdapter.notifyDataSetChanged();
							}
						});
					}
				};
				mThread.start();
			}
			
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

}
