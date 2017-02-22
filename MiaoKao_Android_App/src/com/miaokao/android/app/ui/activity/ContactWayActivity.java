package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
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
import com.miaokao.android.app.AppContext;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.RequestConstants;
import com.miaokao.android.app.entity.User;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;

/**
 * 联系方式页面 --完善个人信息
 * 
 * @author ouyangbo
 * 
 */
public class ContactWayActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private String mSexTxt;
	private EditText mNameET, mPhoneET, mAuthCodeET, mAddressET;
	private TextView mManCBox, mWoManCBox;
	private Button mAuthCodeBT;
	private int mTime;
	private CountDownTimer mCountDownTimer;
	private int mLoginType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contact_way);

		mLoginType = getIntent().getIntExtra("type", 0);
		mContext = this;
		initView();
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.contact_common_actionbar, "联系方式");

		mNameET = (EditText) findViewById(R.id.perfect_info_username_et);
		mPhoneET = (EditText) findViewById(R.id.perfect_info_phone_et);
		mAuthCodeET = (EditText) findViewById(R.id.perfect_info_authcode_et);
		mAddressET = (EditText) findViewById(R.id.perfect_info_address_et);
		mManCBox = (TextView) findViewById(R.id.perfect_info_man_cbox);
		mWoManCBox = (TextView) findViewById(R.id.perfect_info_woman_cbox);
		mAuthCodeBT = (Button) findViewById(R.id.perfect_info_get_authcode_bt);
		mAuthCodeBT.setOnClickListener(this);

		findViewById(R.id.perfect_info_ok_bt).setOnClickListener(this);

		mManCBox.setOnClickListener(checkListenner);
		mWoManCBox.setOnClickListener(checkListenner);
		setSex(true);

		if (mAppContext.mUser != null) {
			// 名称
			mNameET.setText(mAppContext.mUser.getUser_name());
			// 性别
			String sex = mAppContext.mUser.getSex();
			if ("男".equals(sex)) {
				setSex(true);
			} else {
				setSex(false);
			}
			// 地址
			mAddressET.setText(mAppContext.mUser.getAddress());
		}

		switch (mLoginType) {
		case 1:
			// 未登录
			// 需要完善信息和登录一块
			break;
		case 2:
			// 已登录，未完善资料
			// 隐藏填写手机号，只需要完善信息即可
			findViewById(R.id.contact_phone_layout).setVisibility(View.GONE);
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.perfect_info_get_authcode_bt:
			// 获取验证码
			String phone = mPhoneET.getText().toString().trim();

			if (!PubUtils.isMobileNO(phone)) {
				showDialogTips(mContext, "请输入正确的手机号码");
				return;
			}
			getAuthCode(phone);
			break;
		case R.id.perfect_info_ok_bt:
			// 完成
			// 各个信息不能为空!
			// 1,先验证短信验证码
			final String name = mNameET.getText().toString().trim();
			if (TextUtils.isEmpty(name)) {
				showDialogTips(mContext, "联系人不能为空");
				return;
			}
			final String address = mAddressET.getText().toString().trim();
			if (TextUtils.isEmpty(address)) {
				showDialogTips(mContext, "联系地址不能为空");
				return;
			}
			if (mLoginType == 1) {
				final String mobile = mPhoneET.getText().toString().trim();
				if (!PubUtils.isMobileNO(mobile)) {
					showDialogTips(mContext, "请输入正确的手机号码");
					return;
				}
				final String authCode = mAuthCodeET.getText().toString().trim();
				if (TextUtils.isEmpty(authCode)) {
					showDialogTips(mContext, "请输入验证码");
					return;
				}
				
				// 登录
				saveInfoAndLogin(name, mSexTxt, mobile, authCode, address);
				
//				RequestConstants.checkAuthCode(mContext, mobile, authCode, new RequestListenner() {
//					@Override
//					public void responseResult(JSONObject jsonObject) {
//						JSONObject object = jsonObject.optJSONObject("message");
//						if (object != null) {
//							String result = object.optString("result");
//							if ("ok".equals(result)) {
//								// 登录
//								saveInfoAndLogin(name, mSexTxt, mobile, authCode, address);
//							} else {
//								showDialogTips(mContext, "验证码错误");
//							}
//						}
//					}
//
//					@Override
//					public void responseError() {
//
//					}
//				}, getClass().getName());
			} else {
				// 完善信息
				perfectUserInfo(name, mSexTxt, address);
				saveInfoAndLogin(name, mSexTxt, mAppContext.mUser.getLoginName(), "", address);
			}
			break;
		}
	}

	// 完善个人信息
	private void perfectUserInfo(String name, String sex, String address) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "update_member_info");
		postData.put("name", name);
		postData.put("sex", sex);
		postData.put("addr", address);
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if (object != null && !"null".equals(object)) {
					String result = object.optString("result");
					if ("ok".equals(result)) {
						// 获取用户信息
						getUserInfo(mAppContext.mUser.getLoginName());
					} else {
						showDialogTips(mContext, "登录失败");
					}
				}

			}

			@Override
			public void responseError() {

			}
		}, true, getClass().getName());
	}

	// 完善信息加短信登录
	private void saveInfoAndLogin(String name, String sex, final String mobile, String authCode, String address) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "sms_login");
		postData.put("name", name);
		postData.put("sex", sex);
		postData.put("mobile", mobile);
		postData.put("code", authCode);
		postData.put("addr", address);
		mAppContext.netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(final JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if (object != null && !"null".equals(object)) {
					String result = object.optString("result");
					if ("ok".equals(result)) {
						// 获取用户信息
						getUserInfo(mobile);
					} else {
						showDialogTips(mContext, "登录失败");
					}
				}

			}

			@Override
			public void responseError() {

			}
		}, false, getClass().getName());
	}

	protected void getUserInfo(final String userName) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "member_info");
		postData.put("user_id", userName);
		AppContext.getInstance().netRequest(mContext, url, postData, new RequestListenner() {

			@Override
			public void responseResult(JSONObject jsonObject) {
				JSONObject object = jsonObject.optJSONObject("message");
				if (object != null && !"null".equals(object)) {
					User user = new User();
					user.setLoginName(userName);
					user.setUser_no(object.optString("user_no"));
					user.setUser_id(object.optString("user_id"));
					user.setUser_name(object.optString("user_name"));
					user.setSex(object.optString("sex"));
					user.setCareer(object.optString("career"));
					user.setEmail(object.optString("email"));
					user.setMobile(object.optString("mobile"));
					user.setHead_img(object.optString("head_img"));
					user.setStatus(object.optString("status"));
					user.setAddress(object.optString("address"));
					user.setSchool(object.optString("school"));
					user.setMajor(object.optString("major"));

					mAppContext.mUser = user;

					// 发送登录成功广播
					sendBroadcast(new Intent(PubConstant.LOGIN_STATE_KEY));
					finish();
				} else {
					showDialogTips(mContext, "登录失败");
				}
			}

			@Override
			public void responseError() {

			}
		}, true, "get_user_info");
	}

	private View.OnClickListener checkListenner = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.perfect_info_man_cbox) {
				// 先生
				setSex(true);
			} else {
				// 女士
				setSex(false);
			}
		}
	};

	private void setSex(boolean isMan) {
		if (isMan) {
			// 先生
			mSexTxt = "男";
			mManCBox.setSelected(true);
			mWoManCBox.setSelected(false);
		} else {
			// 女士
			mSexTxt = "女";
			mManCBox.setSelected(false);
			mWoManCBox.setSelected(true);
		}
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAppContext.callRequest(getClass().getName());
	}

}
