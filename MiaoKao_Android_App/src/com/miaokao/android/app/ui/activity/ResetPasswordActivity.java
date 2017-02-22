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
import android.widget.TextView;

import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.DecriptUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.DialogTips;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

/**
 * @TODO 重置密码页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-17 下午10:39:57 
 */
public class ResetPasswordActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private EditText mPwdET_1;
	private EditText mPwdET_2;
	private EditText mAuthCodeET;
	private String mPhone;
	private Button mAuthCodeBT;
	private int mTime;
	private CountDownTimer mCountDownTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_reset_password);
		
		mContext = this;
		mPhone = getIntent().getStringExtra("phone");
		
		initView();
		
//		getAuthCode(mPhone);
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.reset_pwd_common_actionbar, "重置密码");
		
		TextView phoneInfoTxt = (TextView) findViewById(R.id.reset_pwd_phone_info_txt);
		mPwdET_1 = (EditText) findViewById(R.id.reset_pwd_1_et);
		mPwdET_2 = (EditText) findViewById(R.id.reset_pwd_2_et);
		mAuthCodeET = (EditText) findViewById(R.id.reset_pwd_auth_code_et);
		mAuthCodeBT = (Button) findViewById(R.id.reset_pwd_get_auth_code_bt);
		mAuthCodeBT.setOnClickListener(this);
		
		String phoneInfo = getString(R.string.register_send_yzm_txt, mPhone);
		phoneInfoTxt.setText(phoneInfo);
		
		findViewById(R.id.reset_pwd_ok_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.reset_pwd_get_auth_code_bt:
			// 发送验证码
			getAuthCode(mPhone);
			break;
		case R.id.reset_pwd_ok_bt:
			// 重置密码
			final String pwd1 = mPwdET_1.getText().toString().trim();
			final String pwd2 = mPwdET_2.getText().toString().trim();
			
			if(TextUtils.isEmpty(pwd1) || TextUtils.isEmpty(pwd2)) {
				showDialogTips(mContext, "新密码不能为空");
				return;
			}
			if(!pwd1.equals(pwd2)) {
				showDialogTips(mContext, "新密码不一致");
				return;
			}
			final String authCode = mAuthCodeET.getText().toString().trim();
			if(TextUtils.isEmpty(authCode)) {
				showDialogTips(mContext, "请输入验证码");
				return;
			}
			
			// 重置密码
			resetPassword(pwd1, authCode);
		}
	}
	
	private void resetPassword(String password, String authCode) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("mobile", mPhone);
		postData.put("code", authCode);
		postData.put("user_pwd", DecriptUtils.SHA1(password));
		postData.put("type", "reset_pwd");
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
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}
	
}
