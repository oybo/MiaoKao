package com.miaokao.android.app.ui.activity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.adapter.CutLocationAdapter;
import com.miaokao.android.app.entity.City;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;

/**
 * 切换位置页面
 * 
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-17 上午10:03:09
 */
public class CutLocationActivity extends BaseActivity {

	private TextView mCityTxt;
	private GridView mGridView;
	private List<City> mCitys;
	private CutLocationAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_cut_location);

		initView();
		initData();
		getCity();
	}

	private void getCity() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_merchants_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "get_usable_city");
		AppContext.getInstance().netRequest(this, url, postData, new RequestListenner() {
			@Override
			public void responseResult(JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null) {
					int len = jsonArray.length();
					for(int i=0; i<len; i++) {
						JSONObject object = jsonArray.optJSONObject(i);
						if(object != null && !"null".equals(object)) {
							City city = new City();
							city.setId(object.optString("id"));
							city.setName(object.optString("name"));
							mCitys.add(city);
						}
					}
					mAdapter.notifyDataSetChanged();
				}
			}

			@Override
			public void responseError() {

			}
		}, true, getClass().getName());
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.cut_location_common_actionbar, "切换位置");

		mCityTxt = (TextView) findViewById(R.id.cut_location_city_txt);
		mGridView = (GridView) findViewById(R.id.cut_location_gridview);
	}

	private void initData() {
		// 定位城市
		String cityTxt = PreferenceUtils.getInstance().getString(PubConstant.LOCAL_CITY, "深圳市");
		if (mAppContext.mAMapLocation != null) {
			cityTxt = mAppContext.mAMapLocation.getCity();
		}
		mCityTxt.setText(cityTxt);

		mCitys = new ArrayList<>();
		mAdapter = new CutLocationAdapter(this, mCitys, R.layout.item_cut_location_activity);
		mGridView.setAdapter(mAdapter);

		mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putSerializable("city", (Serializable) mCitys.get(arg2));
				intent.putExtras(bundle);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}

}
