package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.DialogTips;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

/**
 * @TODO 验证码登录
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-18 上午11:30:14
 */
public class AuthCodeLoginActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private EditText mPhoneET;
	private EditText mAuthCodeET;
	private Button mAuthCodeBT;
	private int mTime;
	private CountDownTimer mCountDownTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_authcode_login);

		mContext = this;
		initView();

	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.auth_code_login_common_actionbar, "短信验证码登录");

		mPhoneET = (EditText) findViewById(R.id.auth_code_login_phone_et);
		mAuthCodeET = (EditText) findViewById(R.id.auth_code_login_auth_code_et);
		mAuthCodeBT = (Button) findViewById(R.id.auth_code_login_get_auth_code_bt);
		mAuthCodeBT.setOnClickListener(this);

		findViewById(R.id.auth_code_login_ok_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.auth_code_login_get_auth_code_bt:
			// 发送验证码
			String phone = mPhoneET.getText().toString().trim();
			if (!PubUtils.isMobileNO(phone)) {
				showDialogTips(mContext, "请填写正确手机号");
				return;
			}
			getAuthCode(phone);
			break;
		case R.id.auth_code_login_ok_bt:
			final String mobile = mPhoneET.getText().toString().trim();
			if (!PubUtils.isMobileNO(mobile)) {
				showDialogTips(mContext, "请填写正确手机号");
				return;
			}
			final String authCode = mAuthCodeET.getText().toString().trim();
			if (TextUtils.isEmpty(authCode)) {
				showDialogTips(mContext, "请填写验证码");
				return;
			}
			// 验证
			authCodeLogin(mobile, authCode);
			
//			// 验证验证码
//			RequestConstants.checkAuthCode(mContext, mobile, authCode, new RequestListenner() {
//
//				@Override
//				public void responseResult(JSONObject jsonObject) {
//					JSONObject object = jsonObject.optJSONObject("message");
//					if (object != null) {
//						String result = object.optString("result");
//						if ("ok".equals(result)) {
//							// 验证通过
//							authCodeLogin(mobile, authCode);
//						} else {
//							showDialogTips(mContext, "验证码错误");
//						}
//					}
//				}
//
//				@Override
//				public void responseError() {
//					
//				}
//			}, getClass().getName());
			break;

		}
	}

	protected void authCodeLogin(String phone, String code) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("mobile", phone);
		postData.put("code", code);
		postData.put("type", "sms_login");
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			@Override
			public void responseResult(JSONObject jsonObject) {

				JSONObject object = jsonObject.optJSONObject("message");
				if (object != null) {
					String result = object.optString("result");
					if ("ok".equals(result)) {
						// 注册成功
						showDialogTipsNotCancel(mContext, "重置密码成功", new onDialogOkListenner() {
							
							@Override
							public void onClick() {
								setResult(Activity.RESULT_OK);
								finish();
							}
						});
					} else {
						// 注册失败
						new DialogTips(mContext, "重置密码失败!").show();
					}
				}

			}

			@Override
			public void responseError() {
				
			}
		}, true, getClass().getName());
	}

	private void getAuthCode(String phone) {
		RequestConstants.getAuthCode(mContext, phone, new RequestListenner() {

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

}
