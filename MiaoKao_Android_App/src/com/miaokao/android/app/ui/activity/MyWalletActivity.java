package com.miaokao.android.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.miaokao.android.app.R;
import com.miaokao.android.app.ui.BaseActivity;
import com.miaokao.android.app.widget.HeaderView.OnRightClickListenner;

/**
 * @TODO 我的钱包
 * @author ouyangbo & 944533800@qq.com 
 * @version 创建时间：2016-3-9 下午1:35:50 
 */
public class MyWalletActivity extends BaseActivity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_wallet);
		
		initView();
	}

	private void initView() {
		initTopBarAll(R.id.my_wallet_common_actionbar, "我的钱包", "账单明细", new OnRightClickListenner() {
			@Override
			public void onClick() {
				startActivity(new Intent(MyWalletActivity.this, ConsumeDetailsActivity.class));
			}
		});
		
		((TextView) findViewById(R.id.my_wallet_price_txt)).setText("¥ " + mAppContext.mUser.getBalance() + "元");
	}
	
}
