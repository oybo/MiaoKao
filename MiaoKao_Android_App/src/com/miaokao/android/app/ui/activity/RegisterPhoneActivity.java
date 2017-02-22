package com.miaokao.android.app.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

/**
 * @TODO 手机注册页面 -- 填写手机号
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-17 上午11:40:03
 */
public class RegisterPhoneActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private EditText mPhoneET;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register_phone);

		mContext = this;
		initView();

	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.register_common_actionbar, "手机注册");

		mPhoneET = (EditText) findViewById(R.id.register_phone_et);

		findViewById(R.id.register_register_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_register_bt:
			String phone = mPhoneET.getText().toString().trim();

			if (!PubUtils.isMobileNO(phone)) {
				showDialogTips(mContext, "请输入正确的手机号码");
				return;
			}
			Intent intent = new Intent(mContext, RegisterUserActivity.class);
			intent.putExtra("phone", phone);
			startActivityForResult(intent, PubConstant.REGISTER_PHONE_REQUEST_CODE);
			break;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PubConstant.REGISTER_PHONE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			finish();
		}
	}

}
