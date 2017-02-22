package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.DecriptUtils;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

/**
 * @TODO 设置密码页面
 * @author ouyangbo & 944533800@qq.com
 * @version 创建时间：2015-12-17 上午11:40:03
 */
public class RegisterPasswordActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private EditText mPasswordET;
	private Button mEyePwdBT;
	private String mPhone;
	private boolean isVisible = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_register_password);

		mContext = this;
		mPhone = getIntent().getStringExtra("phone");

		initView();

	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.register_password_common_actionbar, "设置密码");

		mPasswordET = (EditText) findViewById(R.id.register_set_pwd_et);
		mEyePwdBT = (Button) findViewById(R.id.register_eye_pwd_bt);

		findViewById(R.id.register_eye_pwd_layout).setOnClickListener(this);
		findViewById(R.id.register_set_password_ok_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register_eye_pwd_layout:
			isVisible = !isVisible;
			if (isVisible) {
				// 可见
				mPasswordET.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
				mEyePwdBT.setSelected(true);
			} else {
				// 不可见
				mPasswordET.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				mEyePwdBT.setSelected(false);
			}
			String txt = mPasswordET.getText().toString();
			if (!TextUtils.isEmpty(txt)) {
				mPasswordET.setSelection(txt.length());
			}
			break;
		case R.id.register_set_password_ok_bt:

			String password = mPasswordET.getText().toString().trim();
			if (TextUtils.isEmpty(password) || password.length() < 6) {
				showDialogTips(mContext, "请输入正确的密码");
				return;
			}

			registerUser(password);
			break;

		}
	}

	private void registerUser(String password) {
		String url = PubConstant.REQUEST_BASE_URL + "/app_member_service.php";
		Map<String, String> postData = new HashMap<String, String>();
		postData.put("app_key", "b6589fc6ab0dc82cf12099d1c2d40ab994e8410c");
		postData.put("type", "add_member");
		postData.put("mobile", mPhone);
		postData.put("user_pwd", DecriptUtils.SHA1(password));
		mAppContext.netRequest(this, url, postData, new RequestListenner() {
			@Override
			public void responseResult(JSONObject jsonObject) {

				JSONObject object = jsonObject.optJSONObject("message");

				if (object != null) {
					String result = object.optString("result");
					if ("ok".equals(result)) {
						// 注册成功
						showDialogTipsNotCancel(mContext, "注册成功", new onDialogOkListenner() {
							@Override
							public void onClick() {
								setResult(Activity.RESULT_OK);
								finish();
							}
						});
					} else {
						// 注册失败
						showDialogTips(mContext, "注册失败!");
					}
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
