package com.miaokao.android.app.ui.activity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;

/**
 * @TODO 申请退款确认页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-29 下午4:48:14 
 */
public class ApplyRefundAffirmActivity extends BaseActivity implements OnClickListener {

	private TextView mInfoTxt, mNameTxt, mPhoneTxt;
	private String mAdviser_phone;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_apply_refund_affirm);
		
		initView();
		initData();
	}

	private void initData() {
		Intent intent = getIntent();
		initTopBarLeftAndTitle(R.id.apply_refund_affirm_common_actionbar, intent.getStringExtra("title"));
		String result = intent.getStringExtra("result");
		if(!TextUtils.isEmpty(result)) {
			try {
				JSONArray jsonArray = new JSONArray(result);
				if(jsonArray != null && !"null".equals(jsonArray)) {
					JSONObject jsonObject = jsonArray.getJSONObject(0);
					String info = jsonObject.optString("info", "学车顾问会尽快与您联系，请耐心等待，您也可以直接联系学车顾问。");
					String adviser_name = jsonObject.optString("adviser_name", "");
					mAdviser_phone = jsonObject.optString("adviser_phone", "");
					
					mInfoTxt.setText(info);
					mNameTxt.setText("学车顾问: " + adviser_name);
					mPhoneTxt.setText("联系电话: " + mAdviser_phone);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}		
	}

	private void initView() {
		mInfoTxt = (TextView) findViewById(R.id.apply_refund_info_txt);
		mNameTxt = (TextView) findViewById(R.id.apply_refund_name_txt);
		mPhoneTxt = (TextView) findViewById(R.id.apply_refund_phone_txt);
		
		mPhoneTxt.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.apply_refund_phone_txt:
			// 拨打电话
			if(!TextUtils.isEmpty(mAdviser_phone)) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mAdviser_phone.replace("-", "")));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			break;
		}
	}
	
}
