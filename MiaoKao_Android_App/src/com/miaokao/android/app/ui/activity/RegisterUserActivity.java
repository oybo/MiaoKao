package com.miaokao.android.app.ui.activity;

import org.json.JSONObject;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;

/**
 * @TODO 新用户注册页面 -- 验证短信验证码
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-17 上午11:40:03
 */
public class RegisterUserActivity extends BaseActivity implements OnClickListener {

	private String mPhone;
	private EditText mAuthCodeET;
	private Button mAuthCodeBT;
	private int mTime;
	private Context mContext;
	private CountDownTimer mCountDownTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register_user);

		mContext = this;
		mPhone = getIntent().getStringExtra("phone");

		initView();

		getAuthCode();
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.register_user_common_actionbar, "新用户注册");

		TextView phoneInfoTxt = (TextView) findViewById(R.id.register_phone_info_txt);
		mAuthCodeET = (EditText) findViewById(R.id.register_user_auth_code_et);

		findViewById(R.id.register_phone_ok_bt).setOnClickListener(this);
		mAuthCodeBT = (Button) findViewById(R.id.register_get_auth_code_bt);
		mAuthCodeBT.setOnClickListener(this);

		String phoneInfo = getString(R.string.register_send_yzm_txt, mPhone);
		phoneInfoTxt.setText(phoneInfo);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_get_auth_code_bt:
			// 获取验证码
			getAuthCode();
			break;
		case R.id.register_phone_ok_bt:
			// 完成 - 验证验证码
			String authCode = mAuthCodeET.getText().toString().trim();
			if (TextUtils.isEmpty(authCode)) {
				showDialogTips(mContext, "请输入验证码");
				return;
			}
			// 验证验证码
			RequestConstants.checkAuthCode(mContext, mPhone, authCode, new RequestListenner() {

				@Override
				public void responseResult(JSONObject jsonObject) {
					JSONObject object = jsonObject.optJSONObject("message");
					if (object != null) {
						String result = object.optString("result");
						if ("ok".equals(result)) {
							// 验证通过，跳转到设置密码页面
							Intent intent = new Intent(mContext, RegisterPasswordActivity.class);
							intent.putExtra("phone", mPhone);
							startActivityForResult(intent, PubConstant.REGISTER_AUTH_CODE_REQUEST_CODE);
						} else {
							showDialogTips(mContext, "验证码错误");
						}
					}
				}

				@Override
				public void responseError() {
					
				}
			}, getClass().getName());
			break;

		}
	}

	private void getAuthCode() {
		RequestConstants.getAuthCode(mContext, mPhone, new RequestListenner() {

			@Override
			public void responseResult(JSONObject jsonObject) {

				JSONObject object = jsonObject.optJSONObject("message");
				if (object != null) {
					String authCode = object.optString("result");

					if (!TextUtils.isEmpty(authCode)) {
						showDialogTips(mContext, "验证码已发送,请注意查收");
					}
				}

			}

			@Override
			public void responseError() {
				
			}
		}, getClass().getName());

		// 获取验证码按钮倒计时
		startAuthCodeTimer();
	}

	private void startAuthCodeTimer() {
		mTime = 60;
		mAuthCodeBT.setEnabled(false);
		mAuthCodeBT.setBackgroundResource(R.drawable.bt_unsable_selector);
		mCountDownTimer = new CountDownTimer(60 * 1000, 1000) {

			@Override
			public void onTick(long millisUntilFinished) {
				mTime--;
				mAuthCodeBT.setText(getString(R.string.register_reset_send_txt, mTime + ""));
			}

			@Override
			public void onFinish() {
				mAuthCodeBT.setEnabled(true);
				mAuthCodeBT.setText(getString(R.string.register_get_auth_code_txt));
				mAuthCodeBT.setBackgroundResource(R.drawable.bt_guide_selector);
			}
		};
		mCountDownTimer.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == PubConstant.REGISTER_AUTH_CODE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
			setResult(Activity.RESULT_OK);
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
		if (mCountDownTimer != null) {
			mCountDownTimer.cancel();
		}
	}
}
