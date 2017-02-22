package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.entity.Order;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;

/**
 * @TODO 报名页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-1-2 下午11:29:43 
 */
public class PrepareActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private Order mOrder;
	private TextView mStuTxt, mCerTxt, mPhotoTxt, mHealthTxt;
	private TextView mNoticeTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_prepare);
		
		mOrder = (Order) getIntent().getExtras().getSerializable("order");
		mContext = this;
		initView();
		initData();
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.prepare_common_actionbar, "报名");
		
		mStuTxt = (TextView) findViewById(R.id.prepare_stu);
		mCerTxt = (TextView) findViewById(R.id.prepare_cer);
		mPhotoTxt = (TextView) findViewById(R.id.prepare_photo);
		mHealthTxt = (TextView) findViewById(R.id.prepare_health);
		mNoticeTxt = (TextView) findViewById(R.id.prepare_notice);
		
		findViewById(R.id.prepare_ok_bt).setOnClickListener(this);
	}

	private void initData() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "prepare_notice");
		postData.put("mer_id", mOrder.getMer_id());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if(object != null && !"null".equals(object)) {
					
					mStuTxt.setText(object.optString("sun_stu"));
					mCerTxt.setText(object.optString("sun_cer"));
					mPhotoTxt.setText(object.optString("sun_photo"));
					mHealthTxt.setText(object.optString("sun_health"));
					
					mNoticeTxt.setText("  " + object.optString("sun_notice"));
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.prepare_ok_bt:
			// 资料准备好了
			noticeService();
			break;
		}
	}
	
	private void noticeService() {
		String url = PubConstant.REQUEST_BASE_URL + "/app_reserved_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "prepared");
		postData.put("order_no", mOrder.getOrder_no());
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {
			
			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONArray jsonArray = jsonObject.optJSONArray("message");
				if(jsonArray != null) {
					Intent intent = new Intent(mContext, ApplyRefundAffirmActivity.class);
					intent.putExtra("title", "报到");
					intent.putExtra("result", jsonArray.toString());
					startActivity(intent);
				}
			}
			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
