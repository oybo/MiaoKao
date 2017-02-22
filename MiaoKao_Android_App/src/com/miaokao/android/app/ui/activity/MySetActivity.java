package com.miaokao.android.app.ui.activity;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.miaokao.android.app.AppContext.RequestListenner;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.util.PubConstant;
import com.miaokao.android.app.util.PubUtils;
import com.miaokao.android.app.widget.DialogTips.onDialogOkListenner;

/**
 * @TODO 我的设置页面
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2015-12-24 上午9:27:18 
 */
public class MySetActivity extends BaseActivity implements OnClickListener {

	private Context mContext;
	private TextView mNameTxt;
	private TextView mPhoneTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		setContentView(R.layout.activity_my_set);
		
		mContext = this;
		initView();
		initData();
	}

	private void initData() {
		mNameTxt.setText(mAppContext.mUser.getLoginName());
		mPhoneTxt.setText(mAppContext.mUser.getMobile());
	}

	private void initView() {
		initTopBarLeftAndTitle(R.id.my_set_common_actionbar, "我的设置");
		
		mNameTxt = (TextView) findViewById(R.id.my_set_name);
		mPhoneTxt = (TextView) findViewById(R.id.my_set_phone);
		
		findViewById(R.id.my_set_update_pwd).setOnClickListener(this);
		findViewById(R.id.my_set_update_pwd).setOnClickListener(this);
		findViewById(R.id.my_set_logout_bt).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.my_set_update_pwd:
			// 修改密码
			Intent intent = new Intent(mContext, ResetPasswordActivity.class);
			intent.putExtra("phone", mPhoneTxt.getText().toString());
			startActivity(intent);
			break;
		case R.id.my_set_logout_bt:
			// 退出登录 --- 需要调用接口
			showDialogTipsAndCancel(mContext, "确定退出登录吗?", new onDialogOkListenner() {
				@Override
				public void onClick() {
					PubUtils.logout(mContext, false);
				}
			});
			break;

		}
	}
	
}
