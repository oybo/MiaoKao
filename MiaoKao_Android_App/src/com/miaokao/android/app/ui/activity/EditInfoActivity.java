package com.miaokao.android.app.ui.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.SchoolAdapter;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.entity.School;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;

/**
 * @TODO 编辑个人信息
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-22 下午10:56:33
 */
public class EditInfoActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private EditText mTxtET;
	private int mType;
	private boolean isMan;
	private TextView mManTxt;
	private TextView mWoManTxt;
	private ListView mListView;
	private Thread mThread;
	private List<School> mSchools;
	private SchoolAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_edit_info);

		mContext = this;
		mType = getIntent().getIntExtra("type", 1);
		initView();
		initData();
	}

	private void initData() {
		String title = "";
		String message = "";
		switch (mType) {
		case 1:
			// 姓名
			title = "姓名";
			message = mAppContext.mUser.getUser_name();
			break;
		case 2:
			// 性别
			title = "性别";
			message = mAppContext.mUser.getSex();
			break;
		case 3:
			// 接送地址
			title = "接送地址";
			message = mAppContext.mUser.getAddress();
			break;
		case 4:
			// 学校
			title = "学校";
			message = mAppContext.mUser.getSchool();
			break;
		case 5:
			// 专业
			title = "专业";
			message = mAppContext.mUser.getMajor();
			break;
		case 6:
			// 年级
			title = "年级";
			message = mAppContext.mUser.getStatus();
			break;
		}

		mTxtET.setHint(title);
		mTxtET.setText(message);

		if (mType == 3 || mType == 4) {
			initTopBarLeftAndTitle(R.id.edit_user_info_common_actionbar, title);
		} else {
			if (mType == 2) {
				if ("男".equals(message)) {
					mManTxt.setSelected(true);
					mWoManTxt.setSelected(false);
				} else {
					mManTxt.setSelected(false);
					mWoManTxt.setSelected(true);
				}
			}
			initTopBarAll(R.id.edit_user_info_common_actionbar, title, "保存", new OnRightClickListenner() {

				@Override
				public void onClick() {
					saveInfo();
				}
			});
		}

		if (!TextUtils.isEmpty(message)) {
			mTxtET.setSelection(message.length());
		}
	}

	private void initView() {
		mTxtET = (EditText) findViewById(R.id.edit_info_et);

		mManTxt = (TextView) findViewById(R.id.edit_info_sex_nan);
		mManTxt.setOnClickListener(this);
		mWoManTxt = (TextView) findViewById(R.id.edit_info_sex_nv);
		mWoManTxt.setOnClickListener(this);

		if (mType == 2) {
			// 性别
			findViewById(R.id.edit_info_et).setVisibility(View.GONE);
			findViewById(R.id.edit_info_sex_layout).setVisibility(View.VISIBLE);

			if (mAppContext.mUser.getSex() == "女") {
				isMan = false;
				mManTxt.setSelected(false);
				mWoManTxt.setSelected(true);
			} else {
				isMan = true;
				mManTxt.setSelected(true);
				mWoManTxt.setSelected(false);
			}
		} else if (mType == 3) {
			// 接送地址
			findViewById(R.id.edit_info_et).setVisibility(View.GONE);
			mListView = (ListView) findViewById(R.id.edit_info_school_lv);
			mListView.setVisibility(View.VISIBLE);

			mSchools = new ArrayList<>();
			mAdapter = new SchoolAdapter(mContext, mSchools, R.layout.item_school_activity);
			mListView.setAdapter(mAdapter);

			getOrder();

			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// 设置接送点
					mTxtET.setText(mSchools.get(arg2).getCity());
					saveInfo();
				}
			});
		} else if (mType == 4) {
			// 学校
			findViewById(R.id.edit_info_et).setVisibility(View.GONE);
			mListView = (ListView) findViewById(R.id.edit_info_school_lv);
			mListView.setVisibility(View.VISIBLE);

			mSchools = new ArrayList<>();
			mAdapter = new SchoolAdapter(mContext, mSchools, R.layout.item_school_activity);
			mListView.setAdapter(mAdapter);

			getSchools();

			mListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
					// 设置学校
					mTxtET.setText(mSchools.get(arg2).getName());
					saveInfo();
				}
			});
		}
	}

	private void getOrder() {
		// 先获取订单
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "order");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
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
							getAddress(order.getMer_id());
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

	private void getAddress(String mer_id) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_delivery_place");
		postData.put("mer_id", mer_id);
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if (jsonArray != null) {
					int len = jsonArray.length();
					for (int i = 0; i < len; i++) {
						School school = new School();

						JSONObject object = jsonArray.optJSONObject(i);
						school.setId(object.optString("sup_no"));
						school.setName(object.optString("sup_name"));
						school.setCity(object.optString("sup_addr"));

						mSchools.add(school);
					}
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mAdapter.notifyDataSetChanged();
						}
					});
				}
			}

			@Override
			public void responseError() {

			}
		}, true, "getAddress");
	}

	private void getSchools() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_school");
		postData.put("page", "0");
		postData.put("size", "30");
		// 以下用作搜索
		postData.put("school_name", "");
		postData.put("province", "");
		postData.put("city", "");
		postData.put("area", "");
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			@Override
			public void responseResult(final JSONObject jsonObject) {
				mThread = new Thread() {
					@Override
					public void run() {
						super.run();
						JSONArray jsonArray = jsonObject.optJSONArray("message");
						if (jsonArray != null) {
							int len = jsonArray.length();
							for (int i = 0; i < len; i++) {
								JSONObject object = jsonArray.optJSONObject(i);
								if (object != null && !"null".equals(object)) {
									School school = new School();
									school.setId(object.optString("id"));
									school.setName(object.optString("name"));
									school.setProvince(object.optString("province"));
									school.setCity(object.optString("city"));
									school.setArea(object.optString("area"));

									mSchools.add(school);
								}
							}
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									mAdapter.notifyDataSetChanged();
								}
							});
						}
					}
				};
				mThread.start();
			}

			@Override
			public void responseError() {

			}
		}, true, getClass().getName());
	}

	protected void saveInfo() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("user_id", mAppContext.mUser.getLoginName());
		final String valueTxt = mTxtET.getText().toString().trim();
		switch (mType) {
		case 1:
			// 姓名
			postData.put("type", "name");
			postData.put("name", valueTxt);
			break;
		case 2:
			// 性别
			postData.put("type", "sex");
			postData.put("sex", isMan ? "男" : "女");
			break;
		case 3:
			// 地址
			postData.put("type", "addr");
			postData.put("addr", valueTxt);
			break;
		case 4:
			// 学校
			postData.put("type", "school");
			postData.put("school_name", valueTxt);
			break;
		case 5:
			// 专业
			postData.put("type", "major");
			postData.put("major", valueTxt);
			break;
		case 6:
			// 年级
			postData.put("type", "grade");
			postData.put("grade", valueTxt);
			break;

		}
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				String result = object.optString("result");
				if ("ok".equals(result)) {
					setResult(Activity.RESULT_OK);
					finish();
					saveInfo(valueTxt);
				} else {
					showDialogTips(mContext, "保存失败");
				}
			}

			@Override
			public void responseError() {
				showDialogTips(mContext, "修改失败");
			}
		}, true, getClass().getName());
	}

	private void saveInfo(String valueTxt) {
		switch (mType) {
		case 1:
			// 姓名
			mAppContext.mUser.setUser_name(valueTxt);
			break;
		case 2:
			// 性别
			mAppContext.mUser.setSex(isMan ? "男" : "女");
			break;
		case 3:
			// 地址
			mAppContext.mUser.setAddress(valueTxt);
			break;
		case 4:
			// 学校
			mAppContext.mUser.setSchool(valueTxt);
			break;
		case 5:
			// 专业
			mAppContext.mUser.setMajor(valueTxt);
			break;
		case 6:
			// 年级
			mAppContext.mUser.setStatus(valueTxt);
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.edit_info_sex_nan:
			isMan = true;
			mManTxt.setSelected(true);
			mWoManTxt.setSelected(false);
			break;
		case R.id.edit_info_sex_nv:
			isMan = false;
			mManTxt.setSelected(false);
			mWoManTxt.setSelected(true);
			break;

		}
	}

}
