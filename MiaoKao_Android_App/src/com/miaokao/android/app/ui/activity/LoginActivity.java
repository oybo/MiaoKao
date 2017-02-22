package com.miaokao.android.app.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.DecriptUtils;
import com.miaokao.android.app.util.PreferenceUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;
import com.miaokao.android.app.widget.LoadingDialog;

public class LoginActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private EditText mUserNameET;
	private EditText mPasswordET;
	private LoadingDialog mLoadingDialog;
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(PubConstant.LOGIN_STATE_KEY.equals(action)) {
				mLoadingDialog.dismiss();
				boolean isLogin = intent.getBooleanExtra("isLogin", false);
				if(isLogin) {
					finish();
				} else {
					showDialogTips(mContext, "登录失败");
				}
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		mContext = this;
		initView();
		initLoginNameAndPwd();
		initReceiver();
	}

	private void initReceiver() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(PubConstant.LOGIN_STATE_KEY);
		registerReceiver(receiver, filter);
	}

	private void initLoginNameAndPwd() {
		String loginName = PreferenceUtils.getInstance().getString(PubConstant.LOGIN_NAME_KEY, "");
		String password = PreferenceUtils.getInstance().getString(PubConstant.PASSWORD_KEY, "");
		if(!TextUtils.isEmpty(loginName)) {
			mUserNameET.setText(loginName);
			mUserNameET.setSelection(loginName.length());
		}
		if(!TextUtils.isEmpty(password)) {
			password = DecriptUtils.decryptBASE64(password);
			mPasswordET.setText(password);
			mPasswordET.setSelection(password.length());
		}
	}

	private void initView() {
		initTopBarAll(R.id.login_common_actionbar, "登录", getString(R.string.login_register_txt),
				new OnRightClickListenner() {
					@Override
					public void onClick() {
						startActivity(new Intent(LoginActivity.this, RegisterPhoneActivity.class));
					}
				});

		mUserNameET = (EditText) findViewById(R.id.login_username_et);
		mPasswordET = (EditText) findViewById(R.id.login_password_et);

		findViewById(R.id.login_login_bt).setOnClickListener(this);
		findViewById(R.id.login_forget_pwd_txt).setOnClickListener(this);
		findViewById(R.id.login_sms_login_txt).setOnClickListener(this);
		
		mLoadingDialog = LoadingDialog.createLoadingDialog(mContext, "autoLogin");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_login_bt:
			// 登录
			String userName = mUserNameET.getText().toString().trim();
			String password = mPasswordET.getText().toString().trim();
			if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password)) {
				showDialogTips(mContext, "用户名和密码不能为空");
				return;
			}
			
			RequestConstants.autoLogin(mContext, userName, password);
			mLoadingDialog.show();
			break;
		case R.id.login_forget_pwd_txt:
			// 忘记密码
			startActivity(new Intent(mContext, ForgetPasswordActivity.class));
			break;
		case R.id.login_sms_login_txt:
			// 短信验证码登录
			startActivity(new Intent(mContext, AuthCodeLoginActivity.class));
			break;

		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);
	}

}
